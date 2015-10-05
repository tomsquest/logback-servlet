# logback-servlet

Servlet for Logback to configure log levels at runtime.

## Features

* Change Logback level per logger at **runtime**
* Reset levels to their default from the initial logback.xml
* Sort logger by name or level
* Filter logger by name

## Usage

Put the file [LogbackServlet.java](https://raw.githubusercontent.com/tomsquest/logback-servlet/master/src/main/java/ch/qos/logback/servlet/LogbackServlet.java) in your project and declare it as a Servlet.

Servlet 3 example :

``` java
Dynamic logback = servletContext.addServlet("logback", new LogbackServlet());
logback.addMapping("/logback/*");
logback.setLoadOnStartup(1);
```

Servlet 2 example :

``` xml
<servlet>
  <servlet-name>logback</servlet-name>
  <servlet-class>com.tomquest.logback.LogbackServlet</servlet-class>
</servlet>

<servlet-mapping>
  <servlet-name>logback</servlet-name>
  <url-pattern>/logback/*</url-pattern>
</servlet-mapping>
```

## Screenshot

![Logback Servlet Screenshot](logback-servlet.png)