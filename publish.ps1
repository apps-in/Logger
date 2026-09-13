param(
    [string]$GpgPassword
)

$ErrorActionPreference = 'Stop'

# Full GPG fingerprint for Nepomniashchyi Ihor's signing key.
$keyFingerprint = '8B6112D03626D4576F8B1EDE40EA355621912616'

if (-not $GpgPassword) {
    $securePassword = Read-Host 'GPG key password' -AsSecureString
    $GpgPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    )
}

$gpgOutput = & gpg --export-secret-keys --armor $keyFingerprint 2>&1
if ($LASTEXITCODE -ne 0) {
    throw "Failed to export GPG key $keyFingerprint: $gpgOutput"
}

$armoredKey = ($gpgOutput | Where-Object { $_ -is [string] }) -join "`n"
if (-not $armoredKey.Contains('BEGIN PGP PRIVATE KEY BLOCK')) {
    throw "Exported data does not look like an armored private key."
}

# When using in-memory signing, do not keep signing.keyId / signing.useGpgCmd in
# %USERPROFILE%\.gradle\gradle.properties — they conflict with publish.ps1.
$env:ORG_GRADLE_PROJECT_signingInMemoryKey = $armoredKey
$env:ORG_GRADLE_PROJECT_signingInMemoryKeyPassword = $GpgPassword
Remove-Item Env:ORG_GRADLE_PROJECT_signingInMemoryKeyId -ErrorAction SilentlyContinue
Remove-Item Env:ORG_GRADLE_PROJECT_signing_keyId -ErrorAction SilentlyContinue
Remove-Item Env:ORG_GRADLE_PROJECT_signing_useGpgCmd -ErrorAction SilentlyContinue

./gradlew --stop
./gradlew :android_logger:assembleRelease
./gradlew :android_logger:publishAndReleaseToMavenCentral
