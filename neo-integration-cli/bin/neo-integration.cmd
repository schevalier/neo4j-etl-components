@echo off

setlocal
	dir neo-integration.jar /b/s>temp
	set /p jar=<temp
endlocal & java -jar %jar% %*
