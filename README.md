ghostme4j
========
:ghost: Ghostme4j - HTTP Proxifier
Search and use proxies in an easy way.


[![Apache License](http://img.shields.io/badge/license-ASL-blue.svg)](https://github.com/brunocvcunha/ghostme4j/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/brunocvcunha/ghostme4j.svg)](https://travis-ci.org/brunocvcunha/ghostme4j)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.brunocvcunha.ghostme4j/ghostme4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.brunocvcunha.ghostme4j/ghostme4j)



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


You can also get a random User-Agent, to fake a real browser connection (e.g. `Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36`)

To apply in the system properties:
```java
  GhostMe.applyUserAgent();
```

Or to get an User-Agent:
```java
  String userAgent = GhostMe.getRandomUserAgent();
```

Full GhostMe example:
```java
  Proxy used = GhostMe.ghostMySystemProperties(true); //true if it needs to test proxy connectivity/anonymity

  URL url = new URL("https://github.com/brunocvcunha/ghostme4j");
  URLConnection conn = url.openConnection(used.getJavaNetProxy());
  conn.addRequestProperty(HttpHeaders.USER_AGENT, GhostMe.getRandomUserAgent());

```


 [1]: https://search.maven.org/remote_content?g=org.brunocvcunha.ghostme4j&a=ghostme4j&v=LATEST
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
