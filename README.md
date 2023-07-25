```
mvn clean compile jlink:jlink
./target/maven-jlink/default/bin/server
```

Then go to `localhost:9000`. Server should be running,
but the button shouldn't work.

Uncomment the dependency in the pom.xml and in the `module-info.java`.
Then you should run into issues