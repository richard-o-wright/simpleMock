## Welcome

Simple Mock is really that simple. The interface is just on mock factory class 
(with 3 static methods) and one PoJo.

I hope you might find it useful too!

### Background

I've written simpleMock because I got frustrated with the behaviour I was getting with
other mocking frameworks, mainly to do with Having multiple instance of the same class
in the same set of tests, particularly when so instances are mocked and others not.
I suspect that this behaviour is to do the use of some static look up tables in the
frameworks' implementation? That spark some additional concerns about thread safely, 
hence we have simpleMock.

Because simpleMock doesn't store any references to the classes it has mocked its
functionality is limited compared to other frameworks. However, it does 
have the important features to get the job done:-)

### Limitations

simpleMock works best for mocking interfaces (Which it turns out is quite easy to do:-))
Mocking concrete classes is supported but quite limited specifically:
* Counting invocations of the method on a mocked concrete classes doesn't currently work.
* When mocking a concrete classes method that returns an object it cannot mock the returned 
  objects behaviour, what is returned is a blank instance of the object.
* When mocking a concrete classes method that returns an object, the returned object cannot
  also be a mock. I don't fully understand why this is and so if could be something that is
  fixed in a future version.

### Getting Started

To get started using simpleMock please refer to the examples in the Unit test.
```
{project-root}/src/test/java/net/wrightnz/testing/simple/SimpleMockerTest.java
```

How to add to your project as Maven dependency:
```
<dependency>
  <groupId>net.wrightnz.simple.testing</groupId>
  <artifactId>simplemock</artifactId>
  <version>1.3.4</version>
</dependency>
```
Thanks,
Richard Wright (github@wrightnz.net).
