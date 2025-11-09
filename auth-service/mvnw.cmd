@ECHO OFF
SET MVN_CMD=mvn
WHERE %MVN_CMD% >NUL 2>&1
IF %ERRORLEVEL% EQU 0 (
  %MVN_CMD% %*
) ELSE (
  ECHO Maven is required. Please install Maven or use the project Docker build.
  EXIT /B 1
)
