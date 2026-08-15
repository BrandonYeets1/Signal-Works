$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$patch = Join-Path $root 'patch_files'
$project = Get-Location
Get-ChildItem -Path $patch -Recurse -File | ForEach-Object {
    $relative = $_.FullName.Substring($patch.Length).TrimStart('\\','/')
    $target = Join-Path $project $relative
    $dir = Split-Path -Parent $target
    if (!(Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
    if ([System.IO.Path]::GetFullPath($_.FullName) -ne [System.IO.Path]::GetFullPath($target)) {
        Copy-Item -Force $_.FullName $target
    }
}
Write-Host 'Signal Works a3.5.6 source patch applied.' -ForegroundColor Green
Write-Host 'Use mod_version=3.5.6-alpha and archive label a3.5.6 in your Gradle config.'
