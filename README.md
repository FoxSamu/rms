# RMS (Resource Management System)
This is a very simple Java tool for games and other apps to manage resources. 

# Installation
The current version is `1.1`. This version is compatible with Java 17 and above.

The artifact can be installed from my [Maven repository](https://maven.shadew.net/).

## Gradle
```kotlin
// Kotlin DSL:

repositories {
    // Add my repository
    maven { url = uri("https://maven.shadew.net/") }
}

dependencies {
    // Add the artifact
    implementation("dev.runefox:rms:1.1")
}
```

```groovy
// Groovy DSL:

repositories {
    // Add my repository
    maven { url "https://maven.shadew.net/" }
}

dependencies {
    // Add the artifact
    implementation "dev.runefox:rms:1.1"
}
```

## Maven
```xml
<repositories>
    <!-- Add my repository -->
    <repository>
        <id>Runefox Maven</id>
        <url>https://maven.shadew.net/</url>
    </repository>
</repositories>

<dependencies>
    <!-- Add the artifact -->
    <dependency>
        <groupId>dev.runefox</groupId>
        <artifactId>rms</artifactId>
        <version>1.1</version>
    </dependency>
</dependencies>
```

# Usage
There are 3 core components to the system:
1. `ResourceManager`
2. `Resource`
3. `ResourceType`

## 1. Creating the `ResourceManager`

You need at the very least a `ResourceManager`, which is a concrete class you can simply instantiate:
```java
ResourceManager mgr = new ResourceManager(path, "namespace");
```
There are 2 main things you need to create the resource manager:
- A `Path`, to the root directory that contains all your resources.
- A "standard namespace", which is the main namespace of your application. This is usually one identifier, like `rms`.

There is a special static method on `ResourceManager` that allows you to get a `Path` for a resource folder on the class path. This method uses the `ClassLoader.getResource` method, which needs a resource file in order to work. So, to get the resources from your jar file, you need to define some empty file in the root directory of your resources folder, and call `ResourceManager.classpath("/.resources_root")` if it's named `.resources_root`. The returned path is then the parent directory of that file, possibly inside of a jar file.

```java
ResourceManager mgr = new ResourceManager(ResourceManager.classpath(".resources_root"), "namespace");
```

Whenever you're done with your resources, or need to reload all of them, call `ResourceManager.dispose`.

## 2. Implementing `Resource`

The next step is to create a `Resource` implementation. `Resource` is an interface you can implement and it just has one method: `dispose`. This method is called by the `ResourceManager` upon `ResourceManager.dispose`. Usually, this method does nothing, but if your resource acquires some native resources (like off-heap memory) that must be cleaned up, then this is the place to do that. This is a resource that just contains a string:
```java
public class ContentResource implements Resource {
    private final String content;
    
    public ContentResource(String content) {
        this.content = content;
    }
    
    public String content() {
        return content;
    }
    
    @Override
    public void dispose() {
        // Not applicable
    }
}
```

## 3. Loading the `Resource` using a `ResourceType`

Once you've got a `Resource` implementation, you have to define a way this resource is loaded. The `ResourceManager` assumes this resource to be in a certain folder, so all you have to do is locate it inside of that folder, read the files that must be read and turn them into your resource. Typically, the resource is in a file with the same name as the name of the resource. Resources may have path names, so you can group similar resources of the same type into a subfolder.

The loading of resources from the filesystem is done by a `ResourceType`. The `ResourceType` interface has two methods: `load` and `createFallback`. First `load` is called to locate and load the resource, and if that fails, `createFallback` should be able to create some kind of fallback in case the resource fails loading. For above example we could create the following resource type:
```java
public class ContentResourceType implements ResourceType<ContentResource> {
    @Override
    public ContentResource load(ResourceManager res, String ns, String name, Path directory) throws Exception {
        // The `directory` parameter already accounts for the namespace, so you don't need to incorporate the
        // namespace into locating the resource.
        try (BufferedReader in = Files.newBufferedReader(directory.resolve(name + ".txt"), StandardCharsets.UTF_8)) {
            StringWriter writer = new StringWriter();
            in.transferTo(writer);
            return new ContentResource(writer.toString());
        }
    }
    
    @Override
    public ContentResource createFallback(ResourceManager res, String ns, String name) {
        // The fallback should be created from memory, it should not load from a file.
        return new ContentResource("[Failed loading.]");
    }
}
```
You may, however, encounter resources made of text in this specific structure quite often. There are 3 common interfaces that extend `ResourceType` which ease the loading of single-file resources (which are resources of just one file which has the name of the resource):
- `FileResourceType` is a type for loading binary files: it provides you with an `InputStream` that streams the contents of the file.
- `TextResourceType` is a type for loading text files: it provides you with a `Reader` that reads the contents of the file, by default in UTF-8.
- `StringResourceType` is an extension on `TextResourceType` that reads the entire file into a `String`, which it then provides to you.

Above example could benefit from a `StringResourceType`:
```java
public class ContentResourceType implements StringResourceType<ContentResource> {
    @Override
    public ContentResource load(ResourceManager res, String ns, String name, String content) {
        return new ContentResource(content);
    }
    
    @Override
    public String extension() {
        return "txt";
    }
    
    @Override
    public ContentResource createFallback(ResourceManager res, String ns, String name) {
        return new ContentResource("[Failed loading.]");
    }
}
```
`StringResourceType` does the exact `Reader.transferTo(StringWriter)` operation to provide us the string content. We don't need to do this ourselves.

Whereas you will have multiple instances of your `Resource` implementation, you only have one matching `ResourceType`. So typically you'd create `public static final` field somewhere with its instance because you still need it later to retrieve resources:

```java
public static final ContentResourceType INSTANCE = new ContentResourceType();
```

## 4. Loading a resource

When you got a `ResourceManager`, `Resource` and `ResourceType`, the next thing is loading the resources. First you will have to tell your `ResourceManager` about your `ResourceType`. This is so it can cache resources properly. You do this using the `register` method:
```java
// ...create resource manager here...

mgr.register(ContentResourceType.INSTANCE, "content");
```
You pass a directory name, which is the directory in which the resources are. The resource is then located in `<root path>/<namespace>/<type directory>/`, where:
- `<root path>` is the path given when creating the resource manager
- `<namespace>` is the namespace of the resource
- `<type directory>` is the directory name given when registering a resource type to the resource manager

You can now retrieve a resource using `ResourceManager.get`:

```java
ContentResource myContent = mgr.get(ContentResourceType.INSTANCE, "namespace", "my_content");
// This will load <root path>/namespace/content/my_content.txt

// You can leave out the namespace, it will fall back on the namespace given when creating the ResourceManager
ContentResource myContent = mgr.get(ContentResourceType.INSTANCE, "my_content");
```


## Extra: Using handles

In some occasion, you may want to reload all the resources in your application. This means all the `Resource` instances change. You will have to re-obtain all the resources yourself, which can be quite annoying to do. That is, unless you use `Handle`s. A `Handle` is a reference to a resource. It will store the resource for quick access but it's controlled by the resource manager and reset upon `dispose()`. Instead of calling `ResourceManager.get`, you can call `ResourceManager.handle` in exactly the same way to get a handle instead. You can then store this handle anywhere in your app without ever having to think about reloading it again. Another advantage of using handles is that they're lazily loaded: it will load the resource only when you request it using `Handle.get`. A handle is simply just a glorified `Supplier`.
```java
private final Handle<ContentResource> myContent = mgr.handle(ContentResourceType.INSTANCE, "my_content");
...
ContentResource instance = myContent.get();
```

Now you can call `dispose` at any time to simply unload all resources. RMS will invalidate all handles and the next time you call `get` on any of them, it will reload the resource. In fact, you can call `dispose` and give a new root path, and the resource will load as usual from the new path.

# Libraries for certain formats

Below is a list of libraries that load files in certain formats, with an artifact that belongs to it. All artifacts have the same version as the main project and all artifacts depend on the main project with the same version (in the list referred to with `[rms-version]`).

- [JSON](https://github.com/FoxSamu/json/): use `dev.runefox:rms-json:[rms-version]`.

In the future I may add more supported formats through my libraries or third-party libraries.

# License
Licensed under the LGPLv3 license. For the full LGPL+GPLv3 license, see `LICENSE`.

    Copyright (C) 2023  Samū

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
