param(
    [Parameter(Mandatory = $true)]
    [string]$GpgPassword
)

$ErrorActionPreference = 'Stop'

$keyId = '21912616'

$env:ORG_GRADLE_PROJECT_signingInMemoryKey = gpg --export-secret-keys --armor $keyId
$env:ORG_GRADLE_PROJECT_signingInMemoryKeyId = $keyId
$env:ORG_GRADLE_PROJECT_signingInMemoryKeyPassword = $GpgPassword

if (-not $env:ORG_GRADLE_PROJECT_signingInMemoryKey) {
    throw "Failed to export GPG key $keyId. Check that gpg is installed and the key exists."
}

./gradlew --stop
./gradlew :android_logger:assembleRelease
./gradlew :android_logger:publishAndReleaseToMavenCentral
