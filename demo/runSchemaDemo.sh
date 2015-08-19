# You probably don't need to recompile the code, since I provided the .class files as well as the .java files.
# If you want to rebuild the code, you need Java SE with JDK, available here:
# http://www.oracle.com/technetwork/java/javase/downloads/index.html

# compile
javac SchemaValidator.java

read -p "***Press [Enter] to run a Schema Validation against HML Schema"
java SchemaValidator "hml/Element4.CSB.bad.attributes.xml" "schema/hml-1.0.1.xsd"

read -p "***Press [Enter] to run a Schema Validation against MIRING Tier 1 Schema"
java SchemaValidator "hml/Element4.CSB.bad.attributes.xml" "schema/MiringTier1.xsd"




