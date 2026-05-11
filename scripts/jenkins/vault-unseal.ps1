$ErrorActionPreference = 'Stop'
$docker = $env:DOCKER_EXE
if (-not $docker -or -not (Test-Path -LiteralPath $docker)) {
    Write-Error "DOCKER_EXE is missing or invalid: '$docker'"
}
function Get-UnsealKeyLine([string] $raw) {
    if ($null -eq $raw) { return '' }
    foreach ($line in $raw -split '\r?\n') {
        $t = $line.Trim().Trim([char]0xFEFF)
        if ($t.Length -gt 0) {
            return $t
        }
    }
    return ''
}
$prev = (& curl.exe -k -s -o NUL -w '%{http_code}' 'https://localhost:8200/v1/sys/health')
Write-Host "Vault health before unseal: $prev"
$keys = @(
    (Get-UnsealKeyLine $env:VAULT_UNSEAL_KEY_1),
    (Get-UnsealKeyLine $env:VAULT_UNSEAL_KEY_2),
    (Get-UnsealKeyLine $env:VAULT_UNSEAL_KEY_3)
)
$i = 0
foreach ($k in $keys) {
    $i++
    if ([string]::IsNullOrWhiteSpace($k)) {
        Write-Error "Empty Vault unseal key after normalization (credential slot $i). Check Jenkins Secret text credentials."
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
    if ($LASTEXITCODE -ne 0) {
        Write-Error "vault operator unseal failed on key $i (exit $LASTEXITCODE)"
    }
}
$vcode = (& curl.exe -k -s -o NUL -w '%{http_code}' 'https://localhost:8200/v1/sys/health')
Write-Host "Vault health after unseal: $vcode"
if ($vcode -eq '200' -or $vcode -eq '429') {
    exit 0
}
Write-Error 'Vault is not ready after unseal.'