# 🔧 SAKO API URL Updater
# Script untuk update BASE_URL di ApiConfig.kt

param(
    [Parameter(Mandatory=$false)]
    [string]$NewUrl
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   SAKO API URL UPDATER" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$apiConfigPath = "app\src\main\java\com\sako\data\remote\retrofit\ApiConfig.kt"

# Check if file exists
if (-not (Test-Path $apiConfigPath)) {
    Write-Host "❌ ApiConfig.kt not found!" -ForegroundColor Red
    Write-Host "   Expected: $apiConfigPath" -ForegroundColor Yellow
    exit 1
}

# Read current content
$content = Get-Content $apiConfigPath -Raw

# Extract current URL
if ($content -match 'private const val BASE_URL = "(.+?)"') {
    $currentUrl = $matches[1]
    Write-Host "📍 Current BASE_URL:" -ForegroundColor Cyan
    Write-Host "   $currentUrl" -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "⚠️  Could not find BASE_URL in file" -ForegroundColor Red
    exit 1
}

# If no URL provided, prompt user
if (-not $NewUrl) {
    Write-Host "🔗 Enter new ngrok URL (without /api/):" -ForegroundColor Cyan
    Write-Host "   Example: https://abc-123-def.ngrok-free.app" -ForegroundColor White
    Write-Host ""
    $NewUrl = Read-Host "New URL"
    
    if (-not $NewUrl) {
        Write-Host "❌ No URL provided!" -ForegroundColor Red
        exit 1
    }
}

# Clean URL (remove trailing slashes and /api/ if provided)
$NewUrl = $NewUrl.TrimEnd('/')
$NewUrl = $NewUrl -replace '/api$', ''

# Add /api/
$NewUrlWithApi = "$NewUrl/api/"

Write-Host ""
Write-Host "🔄 Updating BASE_URL to:" -ForegroundColor Yellow
Write-Host "   $NewUrlWithApi" -ForegroundColor Green
Write-Host ""

# Update content
$oldLine = 'private const val BASE_URL = "' + $currentUrl + '"'
$newLine = 'private const val BASE_URL = "' + $NewUrlWithApi + '"'

$newContent = $content -replace [regex]::Escape($oldLine), $newLine

# Write back to file
$newContent | Set-Content $apiConfigPath -NoNewline

Write-Host "✅ ApiConfig.kt updated!" -ForegroundColor Green
Write-Host ""

# Verify
$verifyContent = Get-Content $apiConfigPath -Raw
if ($verifyContent -match 'private const val BASE_URL = "(.+?)"') {
    $verifiedUrl = $matches[1]
    if ($verifiedUrl -eq $NewUrlWithApi) {
        Write-Host "✅ Verification successful!" -ForegroundColor Green
        Write-Host "   New URL: $verifiedUrl" -ForegroundColor White
    } else {
        Write-Host "⚠️  Verification warning:" -ForegroundColor Yellow
        Write-Host "   Expected: $NewUrlWithApi" -ForegroundColor White
        Write-Host "   Got:      $verifiedUrl" -ForegroundColor White
    }
}

Write-Host ""
Write-Host "📱 Next steps:" -ForegroundColor Cyan
Write-Host "   1. Rebuild app: ./gradlew clean assembleDebug" -ForegroundColor White
Write-Host "   2. Install to device: adb install app/build/outputs/apk/debug/app-debug.apk" -ForegroundColor White
Write-Host "   3. Test connection!" -ForegroundColor White
Write-Host ""

Read-Host "Press Enter to close"
