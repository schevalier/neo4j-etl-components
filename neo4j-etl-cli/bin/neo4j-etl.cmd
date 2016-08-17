@echo off

setlocal
	dir neo4j-etl.jar /b/s>temp
	set /p jar=<temp
endlocal & java -jar %jar% %*
