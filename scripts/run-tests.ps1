param([string]$Test = "")
$ErrorActionPreference = "Stop"
if ([string]::IsNullOrWhiteSpace($Test)) {
    & mvn test
} else {
    & mvn test "-Dtest=$Test"
}
exit $LASTEXITCODE
