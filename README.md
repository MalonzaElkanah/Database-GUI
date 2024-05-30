# Malone DBMS

## Dependencies
- mysql-connector-j-8.3.0
- javafx-sdk-21.0.2
- ant

## HOW TO RUN
### Swing Application
#### 1. javac >> java
```bash
mkdir build
cd build
mkdir classes
cd ../

javac -sourcepath src -d build/classes -cp resources
```
### Run built classes
```bash
cd build/classes

java -cp ../../resources/itext-2.1.7.jar:../../resources/mysql-connector-java-5.1.46 com.malone.dbms.desktopSwing.App

```

#### 2. ANT


