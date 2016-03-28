ghostme4j
========
Ghostme4j - HTTP Proxifier

[![Apache License](http://img.shields.io/badge/license-ASL-blue.svg)](https://github.com/brunocvcunha/ghostme4j/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/brunocvcunha/ghostme4j.svg)](https://travis-ci.org/brunocvcunha/ghostme4j)


Download
--------

Download [the latest JAR][1] or grab via Maven:
```xml
<dependency>
  <groupId>org.brunocvcunha.ghostme4j</groupId>
  <artifactId>ghostme4j</artifactId>
  <version>0.1-SNAPSHOT</version>
</dependency>
```
or Gradle:
```groovy
compile 'org.brunocvcunha.ghostme4j:ghostme4j:0.1-SNAPSHOT'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

ghostme4j requires at minimum Java 6.


Usage Example
--------

```java
  Proxy used = GhostMe.ghostMySystemProperties(true); //true if it needs to test proxy connectivity/anonymity
  URL url = new URL("https://github.com/brunocvcunha/ghostme4j");
  URLConnection conn = url.openConnection(used.getJavaNetProxy());
```

or apply to the system properties (`http.proxyHost` and `http.proxyPort`), and no need to pass it in parameters:

```java
  Proxy used = GhostMe.getProxy(true);  //true if it needs to test proxy connectivity/anonymity
  URL url = new URL("https://github.com/brunocvcunha/ghostme4j");
  URLConnection conn = url.openConnection();
```




 [1]: https://search.maven.org/remote_content?g=org.brunocvcunha.ghostme4j&a=ghostme4j&v=LATEST
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
