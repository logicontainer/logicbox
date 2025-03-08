Weird error:
```
Exception in thread "main" java.lang.UnsupportedClassVersionError:
zio/internal/MutableQueueFieldsPadding has been compiled by a more recent
version of the Java Runtime (class file version 55.0), this version of the Java
Runtime only recognizes class file versions up to 52.0 at
logicbox.Main$.main(Main.scala:37)
```

So went to `target/docker/stage/2/opt/docker/lib` and did `jar tvf
<some_zio_jar_file>.jar` to list classes
```bash
λ jar tvf dev.zio.zio_3-2.1.9.jar | grep "MutableQueue"
 1225 Fri Jan 01 00:00:00 CET 2010 zio/internal/MutableQueueFieldsPadding.class
```

Then looked at details about 'major version' with `javap -v <class>`
```bash
λ javap -v -classpath dev.zio.zio_3-2.1.9.jar zio/internal/MutableQueueFieldsPadding | grep "major version"
 major version: 55
```

So problem is that library jar files are done with 55 (java 11), but docker is
set to $8$ (evident by base image being `openjdk:11`), seen in `target/docker/stage/Dockerfile`:
```Docker
FROM openjdk:8 as stage0
// ...

FROM openjdk:8 as mainstage
// ...
```
