# Download dependencies
$libs = @(
    "https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/21/javafx-controls-21-win.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/21/javafx-fxml-21-win.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/21/javafx-graphics-21-win.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-base/21/javafx-base-21-win.jar"
)

$libDir = "lib"
if (!(Test-Path $libDir)) {
    New-Item -ItemType Directory $libDir | Out-Null
}

foreach ($url in $libs) {
    $filename = Split-Path -Leaf $url
    $outPath = Join-Path $libDir $filename
    if (!(Test-Path $outPath)) {
        Write-Host "Downloading $filename..."
        Invoke-WebRequest -Uri $url -OutFile $outPath -UseBasicParsing
    }
}

# Compile
Write-Host "Compiling..."
$cp = (Get-ChildItem lib\*.jar | ForEach-Object { $_.FullName }) -join ";"
javac -cp $cp -d out\production\CurrencyConverter src\*.java

# Run with module path for JavaFX
Write-Host "Running..."
$javaFxLibs = (Get-ChildItem lib\javafx-*.jar | ForEach-Object { $_.FullName }) -join ";"
java --module-path "$javaFxLibs" --add-modules javafx.controls,javafx.fxml -cp "out\production\CurrencyConverter;lib\json-20231013.jar" Main
