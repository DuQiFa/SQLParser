# SQLParser
Last common ancestor (LCA) parser for sqltable results from Hieranoid2.

# Steps to run the program
## On Debian install prerequisites
You must be logged as root
```
apt-get install openjdk-7-jdk maven git
```

## Download sources
```
git clone https://github.com/mashu/SQLParser
```
## Build package
Maven will take care of all required libraries and will place them in apprioprate jar file with dependencies.
```
SQLParser/
mvn package
```
## Run jar
```
java -jar target/sqlparser-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
-o ~/pairs.out -p sequences/ -sql sqltables/ -t tree/66species.tre
```
Running jar without arguments prints the help.
