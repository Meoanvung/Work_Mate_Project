# Starts a local MongoDB for development WITHOUT needing admin rights or the Windows service.
# Uses the user-writable data directory C:\Users\DELL\mongodb-data (edit $DataDir if needed).
#
# NOTE: the built-in "MongoDB" Windows service (C:\Program Files\MongoDB\Server\8.2\bin\mongod.cfg,
# dbPath "C:\Program Files\MongoDB\Server\8.2\data") crashed with an out-of-memory error and now
# requires an elevated (admin) shell to start via `Start-Service MongoDB`. This script is the
# non-admin alternative for local development.
#
# Usage:  powershell -ExecutionPolicy Bypass -File scripts\start-mongodb.ps1

$MonGOD = "C:\Program Files\MongoDB\Server\8.2\bin\mongod.exe"
$DataDir = Join-Path $env:USERPROFILE "mongodb-data"

if (-not (Test-Path $MonGOD)) {
  Write-Host "mongod.exe not found at $MonGOD" -ForegroundColor Red
  Write-Host "Install MongoDB Community Server or set MONGODB_URI in .env to Atlas instead."
  exit 1
}

New-Item -ItemType Directory -Force $DataDir | Out-Null

# Is something already listening on 27017?
$listening = Test-NetConnection -ComputerName 127.0.0.1 -Port 27017 -InformationLevel Quiet -ErrorAction SilentlyContinue
if ($listening) {
  Write-Host "MongoDB is already running on 127.0.0.1:27017." -ForegroundColor Green
  exit 0
}

Write-Host "Starting mongod on 127.0.0.1:27017 (dbPath: $DataDir) ..."
Start-Process -FilePath $MonGOD -ArgumentList @('--dbpath', "`"$DataDir`"", '--port', '27017', '--bind_ip', '127.0.0.1', '--wiredTigerCacheSizeGB', '0.5') -WindowStyle Hidden
Write-Host "MongoDB starting in background. Verify with: Test-NetConnection 127.0.0.1 -Port 27017" -ForegroundColor Yellow
