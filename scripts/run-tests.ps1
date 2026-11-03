param([string]$Test = "")
# Run from the project root in a PowerShell terminal. No credentials in history.
$ErrorActionPreference = "Stop"
$userName = (Read-Host "Your own LinkedIn login email").Trim()
if ([string]::IsNullOrWhiteSpace($userName)) { throw "Email cannot be empty." }
$secret = Read-Host "LinkedIn password (hidden)" -AsSecureString
$plainPassword = $null
$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secret)
try {
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    if ([string]::IsNullOrWhiteSpace($plainPassword)) { throw "Password cannot be empty." }
    $env:LINKEDIN_USERNAME = $userName
    $env:LINKEDIN_PASSWORD = $plainPassword
    $env:LINKEDIN_SEARCH_TERM = "software testing"
    if ($Test -ne "") { & mvn test "-Dtest=$Test" } else { & mvn test }
    $result = $LASTEXITCODE
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    Remove-Item Env:LINKEDIN_USERNAME -ErrorAction SilentlyContinue
    Remove-Item Env:LINKEDIN_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:LINKEDIN_SEARCH_TERM -ErrorAction SilentlyContinue
    $plainPassword = $null
    $secret = $null
}
if ($result -ne 0) { throw "Maven reported an error. Read the test report; do not repeatedly retry login." }
