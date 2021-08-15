#### Notes to author nexus publishing
```bash
mvn -B package --file pom.xml
```
Then
```
mvn clean deploy -s C:\Users\richa\.m2\settings.xml
```
Windows
```
mvn clean deploy -s $HOME\.m2\settings.xml
```
OS X/Linux
```
mvn clean deploy -s $HOME/.m2/settings.xml
```
