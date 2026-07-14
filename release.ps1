# release.ps1
# Run from anywhere -- paths are resolved relative to this script's own location,
# so it works regardless of where the repo is checked out.
#
# Steps: bump version everywhere -> mvn clean package -> jlink -> jpackage -> zip -> gh release

$ErrorActionPreference = "Stop"

# ---------------------------------------------------------------------------
# 0. Setup
# ---------------------------------------------------------------------------

$repoRoot = $PSScriptRoot
Set-Location $repoRoot

# Edit this if your JDK install path is ever different.
$jdkBin = "C:\Program Files\Java\jdk-24\bin"

$appJava      = Join-Path $repoRoot "src\main\java\com\complexcalc\App.java"
$documentHtml = Join-Path $repoRoot "src\main\resources\com\complexcalc\document.html"
$pomXml       = Join-Path $repoRoot "pom.xml"

foreach ($f in @($appJava, $documentHtml, $pomXml)) {
    if (-not (Test-Path $f)) {
        throw "Expected file not found: $f"
    }
}

# ---------------------------------------------------------------------------
# 1. Read current version from pom.xml, ask only for the new version
# ---------------------------------------------------------------------------

$pomForVersionRead = Get-Content -Path $pomXml -Raw
$versionMatch = [regex]::Match($pomForVersionRead, "<version>(\d+\.\d+\.\d+)</version>")
if (-not $versionMatch.Success) {
    throw "Could not find a <version>X.Y.Z</version> entry in pom.xml -- check it manually."
}
$currentVersion = $versionMatch.Groups[1].Value
Write-Host "Current version (read from pom.xml): $currentVersion"

$versionParts = $currentVersion.Split(".")
$suggestedVersion = "$($versionParts[0]).$($versionParts[1]).$([int]$versionParts[2] + 1)"

$newVersionInput = Read-Host "New version (press Enter for $suggestedVersion)"
if ([string]::IsNullOrWhiteSpace($newVersionInput)) {
    $newVersion = $suggestedVersion
} else {
    $newVersion = $newVersionInput
}

Write-Host "`n--- Updating version references: $currentVersion -> $newVersion ---"

foreach ($f in @($appJava, $documentHtml)) {
    $content = Get-Content -Path $f -Raw
    $before  = $content

    # Handles both "v0.7.3" and "v.0.7.3" style prefixes, whichever appears.
    $content = $content.Replace("v.$currentVersion", "v.$newVersion")
    $content = $content.Replace("v$currentVersion", "v$newVersion")

    if ($content -eq $before) {
        Write-Warning "No version string found in $f -- check it manually."
    } else {
        Set-Content -Path $f -Value $content -NoNewline
        Write-Host "Updated $f"
    }
}

$pomContent = Get-Content -Path $pomXml -Raw
$pomBefore  = $pomContent
$pomContent = $pomContent.Replace(">$currentVersion<", ">$newVersion<")
if ($pomContent -eq $pomBefore) {
    Write-Warning "No >$currentVersion< found in pom.xml -- check it manually."
} else {
    Set-Content -Path $pomXml -Value $pomContent -NoNewline
    Write-Host "Updated $pomXml"
}

# ---------------------------------------------------------------------------
# 1.5. Commit the version bump
# ---------------------------------------------------------------------------

Write-Host "`n--- Committing version bump ---"
git add $appJava $documentHtml $pomXml
git diff --cached --quiet
if ($LASTEXITCODE -eq 0) {
    Write-Host "No changes to commit."
} else {
    git commit -m "release v$newVersion"
    if ($LASTEXITCODE -ne 0) { throw "git commit failed." }
    git push
    if ($LASTEXITCODE -ne 0) { throw "git push failed." }
    Write-Host "Committed and pushed: release v$newVersion"
}

# ---------------------------------------------------------------------------
# 2. mvn clean package
# ---------------------------------------------------------------------------

Write-Host "`n--- mvn clean package ---"
mvn clean package
if ($LASTEXITCODE -ne 0) { throw "Maven build failed." }

$jarName = "complex-calculator-$newVersion.jar"
$jarPath = Join-Path $repoRoot "target\$jarName"
if (-not (Test-Path $jarPath)) {
    throw "Expected jar not found: $jarPath (did the version bump in pom.xml take effect?)"
}

# ---------------------------------------------------------------------------
# 2.5. Commit pom.xml changes left behind by the build (e.g. dependency-reduced-pom)
# ---------------------------------------------------------------------------

Write-Host "`n--- Committing post-build pom changes ---"
git add $pomXml
git diff --cached --quiet
if ($LASTEXITCODE -eq 0) {
    Write-Host "No post-build pom changes to commit."
} else {
    git commit -m "release v$newVersion (post-build pom update)"
    if ($LASTEXITCODE -ne 0) { throw "git commit failed." }
    git push
    if ($LASTEXITCODE -ne 0) { throw "git push failed." }
    Write-Host "Committed and pushed post-build pom changes."
}

# ---------------------------------------------------------------------------
# 3. jlink -- rebuild the custom runtime from scratch each time
# ---------------------------------------------------------------------------

Write-Host "`n--- jlink ---"
$runtimeDir = Join-Path $repoRoot "custom-runtime"
if (Test-Path $runtimeDir) { Remove-Item $runtimeDir -Recurse -Force }

& "$jdkBin\jlink.exe" --module-path "$jdkBin\..\jmods" `
    --add-modules java.base,java.desktop,java.logging,java.net.http,java.scripting,jdk.jfr,jdk.jsobject,jdk.unsupported,jdk.xml.dom `
    --output $runtimeDir `
    --strip-debug --no-header-files --no-man-pages --compress=2
if ($LASTEXITCODE -ne 0) { throw "jlink failed." }

# ---------------------------------------------------------------------------
# 4. jpackage
# ---------------------------------------------------------------------------

Write-Host "`n--- jpackage ---"
$distDir = Join-Path $repoRoot "dist"
if (Test-Path $distDir) { Remove-Item $distDir -Recurse -Force }

& "$jdkBin\jpackage.exe" --type app-image `
    --input (Join-Path $repoRoot "target") `
    --main-jar $jarName `
    --main-class com.complexcalc.Launcher `
    --runtime-image $runtimeDir `
    --name ComplexCalculator `
    --app-version $newVersion `
    --dest $distDir
if ($LASTEXITCODE -ne 0) { throw "jpackage failed." }

# ---------------------------------------------------------------------------
# 5. Zip the app-image without double-nesting
# ---------------------------------------------------------------------------

Write-Host "`n--- Packaging zip ---"
$appImageDir = Join-Path $distDir "ComplexCalculator"
$stagingDir  = Join-Path $distDir "complex-calculator"
$zipPath     = Join-Path $distDir "complex-calculator.zip"

if (-not (Test-Path $appImageDir)) { throw "jpackage output not found: $appImageDir" }
if (Test-Path $stagingDir) { Remove-Item $stagingDir -Recurse -Force }
if (Test-Path $zipPath)    { Remove-Item $zipPath -Force }

Copy-Item -Path $appImageDir -Destination $stagingDir -Recurse
Compress-Archive -Path $stagingDir -DestinationPath $zipPath
Remove-Item $stagingDir -Recurse -Force

Write-Host "Created $zipPath"

# ---------------------------------------------------------------------------
# 6. Publish a GitHub release with the jar and the zip
# ---------------------------------------------------------------------------

Write-Host "`n--- Publishing release ---"

$ghAvailable = Get-Command gh -ErrorAction SilentlyContinue
if (-not $ghAvailable) {
    Write-Warning "GitHub CLI ('gh') not found on PATH -- skipping release publish."
    Write-Warning "Install it from https://cli.github.com and run 'gh auth login' to enable this step."
} else {
    $confirm = Read-Host "Publish GitHub release v$newVersion with $jarName and complex-calculator.zip? (y/N)"
    if ($confirm -eq "y") {
        Write-Host "gh will now prompt you to confirm the title and open your editor for release notes..."
        gh release create "v$newVersion" `
            $jarPath `
            $zipPath `
            --title "v$newVersion"
        if ($LASTEXITCODE -ne 0) { throw "gh release create failed." }
        Write-Host "Release v$newVersion published."
    } else {
        Write-Host "Skipped publishing. Artifacts are ready at:"
        Write-Host "  $jarPath"
        Write-Host "  $zipPath"
    }
}

Write-Host "`nDone."
