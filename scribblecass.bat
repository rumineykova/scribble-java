@echo off

set ANTLR=scribble-parser\lib\antlr-3.5.2-complete.jar

set DIR=%~dp0%

set SMTZ3=%USERPROFILE%\.m2\repository\org\sosy-lab\java-smt\native\x86_64-windows

set Z3=%USERPROFILE%\.m2\repository\org\sosy-lab\java-smt\1.0.1\com.microsoft.z3.jar

set JAVASMT=%USERPROFILE%\.m2\repository\org\sosy-lab\java-smt\1.0.1\java-smt-1.0.1.jar

set LIB=scribble-dist\target\lib

set CLASSPATH=%Z3%;%SMTZ3%;%JAVASMT%;%DIR%\scribble-assertions\target\classes;\%DIR%\scribble-cli\target\classes\;%DIR%\scribble-core\target\classes;%DIR%\scribble-parser\target\classes;%DIR%\scribble-codegen\target\classes;%ANTLR%;%DIR%\%LIB%\antlr.jar;%DIR%\%LIB%\antlr-runtime.jar;%DIR%\%LIB%\commons-io.jar;%DIR%\%LIB%\scribble-cli.jar;%DIR%\%LIB%\scribble-core.jar;%DIR%\%LIB%\scribble-parser.jar;%DIR%\%LIB%\scribble-codegen.jar;%DIR%\%LIB%\stringtemplate.jar

java -cp "%CLASSPATH%" org.scribble.ext.assrt.cli.AssrtCommandLine %*