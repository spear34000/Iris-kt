@ECHO OFF
SET DIR=%~dp0
SET APP_BASE_NAME=gradlew
SET APP_HOME=%DIR%
SET DEFAULT_JVM_OPTS=-Xmx64m -Xms64m
SET CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
IF EXIST "%JAVA_HOME%\bin\java.exe" (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
) ELSE (
  SET JAVA_EXE=java.exe
)
IF NOT EXIST "%JAVA_EXE%" (
  ECHO ERROR: JAVA_HOME is not set and no 'java.exe' command could be found in your PATH.
  EXIT /B 1
)
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*