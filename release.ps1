# release.ps1
# paths are resolved relative to this script's own location
#
# steps: bump version everywhere -> mvn clean package -> jlink -> jpackage -> zip -> gh release

$ErrorActionPreference = "Stop"

# 0. setup

$repoRoot = $PSScriptRoot
Set-Location $repoRoot

# auto-detects jdk install path
function Find-JdkBin {
    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\jlink.exe"))) {
        return (Join-Path $env:JAVA_HOME "bin")
    }

    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
    if (-not $javaCmd) {
        throw "No 'java' found on PATH. Install a JDK 21+ and try again."
    }

    # resolves the real install dir even through oracle's javapath redirector
    $props = & java -XshowSettings:properties -version 2>&1
    $javaHomeLine = $props | Select-String "java.home"
    if (-not $javaHomeLine) {
        throw "Could not determine java.home from 'java -XshowSettings:properties'."
    }
    $javaHome = ($javaHomeLine -split "=")[1].Trim()
    $bin = Join-Path $javaHome "bin"

    if (-not (Test-Path (Join-Path $bin "jlink.exe"))) {
        throw "Java install at $javaHome has no jlink -- install a full JDK 21+ (not a JRE)."
    }
    if (-not (Test-Path (Join-Path $bin "jpackage.exe"))) {
        throw "Java install at $javaHome has no jpackage -- install a full JDK 21+ (not a JRE)."
    }
    return $bin
}

$jdkBin = Find-JdkBin
Write-Host "Using JDK at: $jdkBin"

$appJava      = Join-Path $repoRoot "src\main\java\com\complexcalc\App.java"
$documentHtml = Join-Path $repoRoot "src\main\resources\com\complexcalc\document.html"
$pomXml       = Join-Path $repoRoot "pom.xml"

foreach ($f in @($appJava, $documentHtml, $pomXml)) {
    if (-not (Test-Path $f)) {
        throw "Expected file not found: $f"
    }
}

# 1. reads current version from pom.xml, asks for new version (can overwrite old one)

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

$defaultCommitMsg = "release v$newVersion"
$commitMsgInput = Read-Host "Commit message (press Enter for '$defaultCommitMsg')"
if ([string]::IsNullOrWhiteSpace($commitMsgInput)) {
    $commitMsg = $defaultCommitMsg
} else {
    $commitMsg = $commitMsgInput
}

Write-Host "`n--- Updating version references: $currentVersion -> $newVersion ---"

foreach ($f in @($appJava, $documentHtml)) {
    $content = Get-Content -Path $f -Raw

    # handles both "v0.7.3" and "v.0.7.3" style prefixes, whichever appears
    $found = $content.Contains("v.$currentVersion") -or $content.Contains("v$currentVersion")

    if (-not $found) {
        Write-Warning "No version string found in $f -- check it manually."
    } else {
        $content = $content.Replace("v.$currentVersion", "v.$newVersion")
        $content = $content.Replace("v$currentVersion", "v$newVersion")
        Set-Content -Path $f -Value $content -NoNewline
        Write-Host "Updated $f"
    }
}

$pomContent = Get-Content -Path $pomXml -Raw
$pomFound = $pomContent.Contains(">$currentVersion<")
if (-not $pomFound) {
    Write-Warning "No >$currentVersion< found in pom.xml -- check it manually."
} else {
    $pomContent = $pomContent.Replace(">$currentVersion<", ">$newVersion<")
    Set-Content -Path $pomXml -Value $pomContent -NoNewline
    Write-Host "Updated $pomXml"
}

# 2. compiles clean mvn package

Write-Host "`n--- mvn clean package ---"
mvn clean package
if ($LASTEXITCODE -ne 0) { throw "Maven build failed." }

$jarName = "complex-calculator-$newVersion.jar"
$jarPath = Join-Path $repoRoot "target\$jarName"
if (-not (Test-Path $jarPath)) {
    throw "Expected jar not found: $jarPath (did the version bump in pom.xml take effect?)"
}

# 3. commits the version bump

Write-Host "`n--- Committing version bump ---"
git add $appJava $documentHtml $pomXml
git diff --cached --quiet
if ($LASTEXITCODE -eq 0) {
    Write-Host "No changes to commit."
} else {
    git commit -m $commitMsg
    if ($LASTEXITCODE -ne 0) { throw "git commit failed." }
    git push
    if ($LASTEXITCODE -ne 0) { throw "git push failed." }
    Write-Host "Committed and pushed: $commitMsg"
}

# 4. jlink rebuilds the custom runtime

Write-Host "`n--- jlink ---"
$runtimeDir = Join-Path $repoRoot "custom-runtime"
if (Test-Path $runtimeDir) { Remove-Item $runtimeDir -Recurse -Force }

& "$jdkBin\jlink.exe" --module-path "$jdkBin\..\jmods" `
    --add-modules java.base,java.desktop,java.logging,java.net.http,java.scripting,jdk.jfr,jdk.jsobject,jdk.unsupported,jdk.xml.dom `
    --output $runtimeDir `
    --strip-debug --no-header-files --no-man-pages --compress=2
if ($LASTEXITCODE -ne 0) { throw "jlink failed." }

# 5. jpackage

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

# 6. zips the app-image without double-nesting

Write-Host "`n--- Packaging zip ---"
$appImageDir = Join-Path $distDir "ComplexCalculator"
$zipPath     = Join-Path $distDir "complex-calculator.zip"

if (-not (Test-Path $appImageDir)) { throw "jpackage output not found: $appImageDir" }
if (Test-Path $zipPath) { Remove-Item $zipPath -Force }

Compress-Archive -Path (Join-Path $appImageDir "*") -DestinationPath $zipPath

Write-Host "Created $zipPath"

# 7. publishes a github release with the jar and the zip

Write-Host "`n--- Publishing release ---"

$ghAvailable = Get-Command gh -ErrorAction SilentlyContinue
if (-not $ghAvailable) {
    Write-Warning "GitHub CLI ('gh') not found on PATH -- skipping release publish."
    Write-Warning "Install it from https://cli.github.com and run 'gh auth login' to enable this step."
} else {
    $releaseTag = "v$newVersion"

    gh release view $releaseTag *> $null
    $releaseExists = ($LASTEXITCODE -eq 0)

    if ($releaseExists) {
        Write-Host "Release $releaseTag already exists on GitHub."
        $overwrite = Read-Host "Overwrite its jar/zip assets? (y/N)"
        if ($overwrite -eq "y") {
            gh release upload $releaseTag $jarPath $zipPath --clobber
            if ($LASTEXITCODE -ne 0) { throw "gh release upload failed." }
            Write-Host "Assets on $releaseTag updated."

            $editNotes = Read-Host "Also edit the release title/notes? (y/N)"
            if ($editNotes -eq "y") {
                $existing = gh release view $releaseTag --json name,body | ConvertFrom-Json

                $currentTitle = if ($existing.name) { $existing.name } else { $releaseTag }
                $newTitle = Read-Host "Title (press Enter to keep '$currentTitle')"
                if ([string]::IsNullOrWhiteSpace($newTitle)) { $newTitle = $currentTitle }

                $notesFile = [System.IO.Path]::GetTempFileName()
                Set-Content -Path $notesFile -Value $existing.body -NoNewline
                Write-Host "Opening notepad to edit release notes -- save and close it to continue..."
                Start-Process notepad.exe -ArgumentList $notesFile -Wait

                gh release edit $releaseTag --title $newTitle --notes-file $notesFile
                if ($LASTEXITCODE -ne 0) { throw "gh release edit failed." }
                Remove-Item $notesFile -Force
                Write-Host "Release notes updated."
            }
        } else {
            Write-Host "Skipped updating existing release. Artifacts are ready at:"
            Write-Host "  $jarPath"
            Write-Host "  $zipPath"
        }
    } else {
        $confirm = Read-Host "Publish GitHub release $releaseTag with $jarName and complex-calculator.zip? (y/N)"
        if ($confirm -eq "y") {
            Write-Host "gh will now prompt you to confirm the title and open your editor for release notes..."
            gh release create $releaseTag `
                $jarPath `
                $zipPath `
                --title $releaseTag
            if ($LASTEXITCODE -ne 0) { throw "gh release create failed." }
            Write-Host "Release $releaseTag published."
        } else {
            Write-Host "Skipped publishing. Artifacts are ready at:"
            Write-Host "  $jarPath"
            Write-Host "  $zipPath"
        }
    }
}

Write-Host "`nDone."