$ErrorActionPreference = 'Stop'
$docker = $env:DOCKER_EXE
if (-not $docker -or -not (Test-Path -LiteralPath $docker)) {
    Write-Error "DOCKER_EXE is missing or invalid: '$docker'"
}
$prev = (& curl.exe -k -s -o NUL -w '%{http_code}' 'https://localhost:8200/v1/sys/health')
Write-Host "Vault health before unseal: $prev"
function Get-TrimmedKey([string] $s) {
    if ($null -eq $s) { return '' }
    return $s.Trim()
}
$keys = @(
    (Get-TrimmedKey $env:VAULT_UNSEAL_KEY_1),
    (Get-TrimmedKey $env:VAULT_UNSEAL_KEY_2),
    (Get-TrimmedKey $env:VAULT_UNSEAL_KEY_3)
)
foreach ($k in $keys) {
    if ([string]::IsNullOrWhiteSpace($k)) {
        Write-Error 'Empty Vault unseal key after Trim(). Check Jenkins secret text credentials.'
    }
    & $docker compose --env-file .env exec -T -e VAULT_ADDR=https://127.0.0.1:8200 vault vault operator unseal -- $k
    if ($LASTEXITCODE -ne 0) {
        Write-Error "vault operator unseal failed (exit $LASTEXITCODE)"
    }
}
$vcode = (& curl.exe -k -s -o NUL -w '%{http_code}' 'https://localhost:8200/v1/sys/health')
Write-Host "Vault health after unseal: $vcode"
if ($vcode -eq '200' -or $vcode -eq '429') { exit 0 }
Write-Error 'Vault is not ready after unseal.'