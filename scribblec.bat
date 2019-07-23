@echo off

set ANTLR=scribble-parser\lib\antlr-3.5.2-complete.jar

set DIR=%~dp0%

set LIB=scribble-dist\target\lib

set CLASSPATH=%DIR%\scribble-cli\target\classes\;%DIR%\scribble-core\target\classes;%DIR%\scribble-parser\target\classes;%DIR%\scribble-codegen\target\classes;%ANTLR%;%DIR%\%LIB%\antlr.jar;%DIR%\%LIB%\antlr-runtime.jar;%DIR%\%LIB%\commons-io.jar;%DIR%\%LIB%\scribble-cli.jar;%DIR%\%LIB%\scribble-core.jar;%DIR%\%LIB%\scribble-parser.jar;%DIR%\%LIB%\scribble-codegen.jar;%DIR%\%LIB%\stringtemplate.jar


java -cp "%CLASSPATH%" org.scribble.cli.CommandLine %*


