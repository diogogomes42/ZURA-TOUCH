# Sincroniza imagens de prémios: IMAGENS ROLETA -> app Android
$src = Join-Path $PSScriptRoot "..\..\..\IMAGENS ROLETA"
$dst = Join-Path $PSScriptRoot "..\feature\sale-cards\src\main\res\drawable-nodpi"

if (-not (Test-Path $src)) {
    Write-Error "Pasta nao encontrada: $src"
    exit 1
}

New-Item -ItemType Directory -Force -Path $dst | Out-Null

$renameMap = @{
    "card_porta_chaves.png" = "card_portachaves.png"
    "card_bilhete.png"      = "card_concert.png"
}

$sourceFiles = Get-ChildItem $src -Filter "card_*.png"
$copied = 0
$expectedDest = @()

foreach ($file in $sourceFiles) {
    $destName = if ($renameMap.ContainsKey($file.Name)) { $renameMap[$file.Name] } else { $file.Name }
    if ($destName -notmatch '^[a-z0-9_]+\.png$') {
        Write-Warning "SKIP nome invalido: $($file.Name)"
        continue
    }
    Copy-Item $file.FullName (Join-Path $dst $destName) -Force
    Write-Host ("OK {0} from {1}" -f $destName, $file.Name)
    $expectedDest += $destName
    $copied++
}

$logoSrc = Join-Path $PSScriptRoot "..\feature\sale-wheel\src\main\res\drawable-nodpi\zura_touch_logo.png"
if (Test-Path $logoSrc) {
    Copy-Item $logoSrc (Join-Path $dst "zura_touch_logo.png") -Force
    Write-Host "OK zura_touch_logo.png"
}

Get-ChildItem $dst -Filter "*.png" | ForEach-Object {
    if ($_.Name -ne "zura_touch_logo.png" -and $expectedDest -notcontains $_.Name) {
        Remove-Item $_.FullName -Force
        Write-Host ("REMOVED {0}" -f $_.Name)
    }
}

Write-Host ""
Write-Host "Copiadas $copied imagens para:"
Write-Host $dst
Write-Host ""
Write-Host 'Depois corre: .\gradlew clean :app-customer:installDebug'
