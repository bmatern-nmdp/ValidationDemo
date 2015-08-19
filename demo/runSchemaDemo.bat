REM You probably don't need to recompile the code, since I provided the .class files as well as the .java files.
REM If you want to rebuild the code, you need Java SE with JDK, available here:
REM http://www.oracle.com/technetwork/java/javase/downloads/index.html

REM compile
javac SchemaValidator.java

echo "***Press [Enter] to run a Schema Validation against HML Schema"
pause
java SchemaValidator "hml/Element4.CSB.bad.attributes.xml" "schema/hml-1.0.1.xsd"

echo "***Press [Enter] to run a Schema Validation against MIRING Tier 1 Schema"
pause
java SchemaValidator "hml/Element4.CSB.bad.attributes.xml" "schema/MiringTier1.xsd"




