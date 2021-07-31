## Welcome
Simple Mock is really that simple, just one class with a couple of
static methods.

To get started using it please refer to the examples in the Unit test.

```
{project-root}/src/test/java/net/wrightnz/testing/simple/SimpleMockerTest.java
```

Created because I'm sick of complex heavy weight and unreliable 
mocking libraries making my unit test hard to right and fragile.
I hope you might find this useful too!

How to add to your project as Maven dependency:
```
<dependency>
  <groupId>net.wrightnz.simple.testing</groupId>
  <artifactId>simplemock</artifactId>
  <version>0.3.1</version>
</dependency>
```
Thanks,
Richard Wright (richard@wrightnz.net).

#### Notes to auther nexus publishing
```bash
mvn -B package --file pom.xml
mvn clean deploy -s C:\Users\richa\.m2\settings.xml
```
