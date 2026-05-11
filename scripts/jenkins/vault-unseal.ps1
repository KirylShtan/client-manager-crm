$ErrorActionPreference = 'Stop'
$docker = $env:DOCKER_EXE
if (-not $docker -or -not (Test-Path -LiteralPath $docker)) {
    Write-Error "DOCKER_EXE is missing or invalid: '$docker'"
}

function Extract-VaultUnsealToken([string] $raw) {
    if ([string]::IsNullOrWhiteSpace($raw)) { return '' }

    # Zero-width/BOM-ish noise from copy-paste
    $strippedControls = ([regex]::Replace(
        $raw,
        "[" + ([char]0xFEFF) + [char]0x200B + [char]0x200C + [char]0x200D + "]",
        ''
    ))

    # Prefer longest base64-ish run (handles "Unseal Key 1: xxx" on one line)
    $normalized = (($strippedControls -replace "\r?\n", ' ') -replace '\s{2,}', ' ').Trim()
    $pattern = '[A-Za-z0-9+/]{20,}={0,2}'
    $best = ''
    foreach ($m in [regex]::Matches($normalized, $pattern)) {
        if ($m.Success -and ($m.Value.Length -gt $best.Length)) { $best = $m.Value }
    }
    if ($best.Length -gt 0) { return $best }

    # Fallback: strip "label:" prefix then collapse whitespace-only line payloads
    foreach ($line in $raw -split '\r?\n') {
        $t = $line.Trim().Trim([char]0xFEFF)
        if ($t.Length -eq 0) { continue }

        # "Unseal Key 3: xxx" → xxx
        if ($t.Contains(':')) {
            $colon = $t.IndexOf(':')
            $after = ($t.Substring($colon + 1)).Trim()
            if ($after.Length -gt 0 -and $after -match '^[A-Za-z0-9+/]+=*$') { return $after }
            $joined = ($after -replace '\s','')
            if ($joined -match '^[A-Za-z0-9+/]{20,}=*$') { return $joined }
        }

        $noWs = ($t -replace '\s','')
        if ($noWs -match '^[A-Za-z0-9+/]+=*$') { return $noWs }
    }

    return ''
}

function Get-KeyFingerprint16([string] $s) {
    $sha = [System.Security.Cryptography.SHA256]::Create()
    try {
        $bytes = $sha.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($s))
        return ([BitConverter]::ToString($bytes, 0, [Math]::Min(8, $bytes.Length))).Replace('-', '')
    } finally {
        $sha.Dispose()
    }
}

$prev = (& curl.exe -k -s -o NUL -w '%{http_code}' 'https://localhost:8200/v1/sys/health')
Write-Host "Vault health before unseal: $prev"

$keysFromFile = $null
$filePath = $env:VAULT_UNSEAL_KEYS_FILE
if (-not [string]::IsNullOrWhiteSpace($filePath) -and (Test-Path -LiteralPath $filePath)) {
    $acc = New-Object System.Collections.Generic.List[string]
    foreach ($line in Get-Content -LiteralPath $filePath -ErrorAction Stop) {
        $tok = Extract-VaultUnsealToken $line
        if ($tok.Length -gt 0) { $acc.Add($tok) | Out-Null }
    }
    if ($acc.Count -ge 3) { $keysFromFile = @($acc[0], $acc[1], $acc[2]) }
}

if ($null -ne $keysFromFile) {
    $keys = @($keysFromFile[0], $keysFromFile[1], $keysFromFile[2])
    Write-Host 'Vault unseal keys source: bundle file'
} else {
    $keys = @(
        (Extract-VaultUnsealToken $env:VAULT_UNSEAL_KEY_1),
        (Extract-VaultUnsealToken $env:VAULT_UNSEAL_KEY_2),
        (Extract-VaultUnsealToken $env:VAULT_UNSEAL_KEY_3)
    )
    Write-Host 'Vault unseal keys source: Jenkins string credentials'
}

$idxLen = 0
foreach ($kk in $keys) {
    $idxLen++
    $fp = Get-KeyFingerprint16 $kk
    Write-Host ('Unseal key {0} length (after normalization): {1}; fingerprint SHA256[..8]={2}' -f $idxLen, $kk.Length, $fp)
    if (-not ($kk -match '^[A-Za-z0-9+/]+=*$')) {
        Write-Error ('Key slot {0} is not Vault base64 after normalization.' -f $idxLen)
    }
}

$uniq = New-Object System.Collections.Generic.HashSet[string]
foreach ($kk in $keys) { [void]$uniq.Add($kk) }
if ($uniq.Count -lt 3) {
    Write-Host 'ERROR: fewer than 3 DISTINCT keys after normalization (duplicate pasted into two Jenkins secrets?). Fix credentials.'
    exit 1
}

$i = 0
foreach ($k in $keys) {
    $i++
    if ([string]::IsNullOrWhiteSpace($k)) {
        Write-Error "Empty Vault unseal token (slot $i). Fix credential text or paste only the bare key tokens."
    }
    $exeArgs = @(
        'compose',
        '--env-file', '.env',
        'exec', '-T',
        '-e', 'VAULT_ADDR=https://127.0.0.1:8200',
        'vault', 'vault', 'operator', 'unseal', '--',
        $k
    )
    & $docker @exeArgs
    $exitCode = $LASTEXITCODE
    if ($null -eq $exitCode) { $exitCode = 0 }
    if ($exitCode -ne 0) {
        Write-Host ("vault operator unseal FAILED on submission {0}: exit code {1}. See Vault/API output above." -f $i, $exitCode)
        exit $exitCode
    }
}

$vcode = (& curl.exe -k -s -o NUL -w '%{http_code}' 'https://localhost:8200/v1/sys/health')
Write-Host "Vault health after unseal: $vcode"
if ($vcode -eq '200' -or $vcode -eq '429') {
    exit 0
}
Write-Error 'Vault is not ready after unseal.'
