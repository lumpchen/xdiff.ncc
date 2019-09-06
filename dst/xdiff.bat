
cd C:\dev\xdiff
SET folder=%~dp0
echo %folder%

java -Xms512M -Xmx1024M -jar xdiff.ncc.jar -config ^"%folder%\config.properties^" ^"%folder%\control.pdf^" ^"%folder%\test.pdf^" ^"%folder%\report^"

echo %ERRORLEVEL%