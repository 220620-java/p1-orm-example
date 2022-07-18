# p1-orm-example

## Setup Guide

1. Once you've pulled the code, use Maven to package it as a .jar file.
``` sh
mvn package
```

2. If you want to add the `rev-orm-0.0.1.jar` file directly to your application's build path, continue. If you want to add the application to your local Maven repository and access it via your `pom.xml`, skip to step 6.

3. Create a `lib/` folder in your application's folder (top-level - in the same location where the `src/` folder is).

4. Copy and paste the `rev-orm-0.0.1.jar` file into the `lib/` folder that you just created.

5. Add the project to your build path. In Eclipse, you can do this by right-clicking the jar file and choose "Add to build path".

6. If you are adding the application to your local Maven repository instead of the above, run the following.
``` sh
mvn install
```

7. Now, add the following to your `pom.xml` as a dependency.
``` xml
<dependency>
    <groupId>com.revature.orm</groupId>
    <artifactId>rev-orm</artifactId>
    <version>0.0.1</version>
</dependency>
```

8. You may need to run a Maven>Update Project to load in the dependency, and you may need to run the application itself a few times to clear out the Maven errors.

## Usage Examples

See the [p1-web-petapp](https://github.com/220620-java/p1-web-petapp.git) for more examples.

