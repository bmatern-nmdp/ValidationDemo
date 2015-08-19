REM You probably don't need to recompile the code, since I provided the .class files as well as the .java files.
REM If you want to rebuild the code, you need Java SE with JDK, available here:
REM http://www.oracle.com/technetwork/java/javase/downloads/index.html

javac SchematronValidator.java

echo "***Press [Enter] to run a Schematron Validation using Probatron.jar"
pause
java -jar jar/probatron.jar "hml/Element4.CSB.bad.attributes.xml" "schematron/MiringAll.sch"

echo -p "***Press [Enter] to run a Schematron Validation using reflection to call Probatron's methods"
pause
java SchematronValidator "hml/Element4.CSB.bad.attributes.xml" "schematron/MiringAll.sch"




