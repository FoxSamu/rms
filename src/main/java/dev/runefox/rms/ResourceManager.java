/*
 * Copyright (C) 2023  Samū
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.runefox.rms;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A resource manager is the general holder of all resources that are currently known. It manages the loading and
 * caching of resources, as well as cleaning them up after use. To get started, create a {@link ResourceType} and add it
 * to the resource manager through {@link #register}.
 * <p>
 * Every resource has a namespace and a name. In simple cases, the namespace is just one common name, so it can be set
 * as the standard namespace in the resource manager. Typically, resources are located in the folder
 * {@code <resource_path>/<namespace>/<resource_type>/}, and the file name usually is the resource name plus some
 * extension. The {@code <resource_type>} directory is the directory given with the resource type when its being
 * registered.
 * </p>
 *
 * @author Samū
 * @see ResourceType
 * @see ResourceManager
 * @since 1.0
 */
public class ResourceManager {
    private final Map<ResourceType<?>, ResourceSet<?>> types = new IdentityHashMap<>();
    private Path root;
    private final String stdNamespace;
    private ResourceLogger logger = ResourceLogger.SIMPLE;

    /**
     * Creates a {@link ResourceManager}.
     *
     * @param root         The root folder where all the resources are located.
     * @param stdNamespace The standard namespace.
     * @throws NullPointerException When either the root folder or the standard namespace is null.
     * @see #classpath(String, ClassLoader)
     * @since 1.0
     */
    public ResourceManager(Path root, String stdNamespace) {
        if (root == null) {
            throw new NullPointerException("root = null");
        }
        if (stdNamespace == null) {
            throw new NullPointerException("stdNamespace = null");
        }
        this.root = root;
        this.stdNamespace = stdNamespace;
    }

    /**
     * Returns the root path of the resource manager. This is the path where all resources should be located.
     *
     * @return The root path.
     *
     * @since 1.0
     */
    public Path rootPath() {
        return root;
    }

    /**
     * Returns the standard namespace. If no namespace is given when getting a resource, it uses this namespace.
     *
     * @return The standard namespace.
     *
     * @since 1.0
     */
    public String standardNamespace() {
        return stdNamespace;
    }

    /**
     * Sets a {@link ResourceLogger}. In certain events, the resource manager wants to log something. This is usually
     * because of exceptions it caught. The standard implementation just prints to the standard output. When a null
     * argument is parsed, it completely disables logging - this also causes all exceptions to be ignored, with the
     * exception of {@link Error}s. See {@link ResourceLogger#NULL} and {@link ResourceLogger#SIMPLE} for more info.
     *
     * @param logger The resource logger.
     * @return This instance.
     *
     * @see ResourceLogger
     * @since 1.0
     */
    public ResourceManager logger(ResourceLogger logger) {
        if (logger == null) {
            logger = ResourceLogger.NULL;
        }
        this.logger = logger;
        return this;
    }

    /**
     * Registers a {@link ResourceType}. Before a resource type may be used, you must inform the resource manager about
     * it. With this you specify a directory in which resources of this type are ideally located.
     *
     * @param type      The type to be registered.
     * @param directory The directory where resources of this type must be in.
     * @throws NullPointerException     When either argument is null.
     * @throws IllegalArgumentException When the given type is already registered.
     * @see ResourceType
     * @see ResourceManager
     * @since 1.0
     */
    public <R extends Resource> void register(ResourceType<R> type, String directory) {
        if (type == null) {
            throw new NullPointerException("type = null");
        }
        if (directory == null) {
            throw new NullPointerException("directory = null");
        }
        if (types.containsKey(type)) {
            throw new IllegalArgumentException("Resource type is already registered");
        }
        types.put(type, new ResourceSet<>(this, type, directory));
    }

    /**
     * Obtains a resource by type, namespace and name. This first looks for a cached resource and otherwise tries to
     * load it. If, for some reason, loading the resource fails, then it is logged to a {@link ResourceLogger} which can
     * be set using {@link #logger(ResourceLogger)}.
     *
     * @param type      The resource type.
     * @param namespace The namespace. The standard namespace is used when this is null.
     * @param name      The resource name.
     * @return The loaded resource. If the resource type returns a null fallback, this may be null when loading fails.
     *
     * @throws NullPointerException     When the type or name is null.
     * @throws IllegalArgumentException When the resource type is not yet registered.
     * @see #get(ResourceType, String)
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public <R extends Resource> R get(ResourceType<R> type, String namespace, String name) {
        if (type == null) {
            throw new NullPointerException("type = null");
        }
        if (namespace == null) {
            namespace = stdNamespace;
        }
        if (name == null) {
            throw new NullPointerException("name = null");
        }
        if (!types.containsKey(type)) {
            throw new IllegalArgumentException("Unknown resource type, make sure to register it first");
        }

        ResourceSet<R> set = (ResourceSet<R>) types.get(type);
        return set.get(namespace, name);
    }

    /**
     * Obtains a resource by type and name, under the standard namespace. This first looks for a cached resource and
     * otherwise tries to load it. If, for some reason, loading the resource fails, then it is logged to a
     * {@link ResourceLogger} which can be set using {@link #logger(ResourceLogger)}.
     *
     * @param type The resource type.
     * @param name The resource name.
     * @return The loaded resource. If the resource type returns a null fallback, this may be null when loading fails.
     *
     * @throws NullPointerException     When the type or name is null.
     * @throws IllegalArgumentException When the resource type is not yet registered.
     * @see #get(ResourceType, String, String)
     * @see #standardNamespace()
     * @since 1.0
     */
    public <R extends Resource> R get(ResourceType<R> type, String name) {
        return get(type, stdNamespace, name);
    }

    /**
     * Obtains a {@link Handle} by type, namespace and name. A handle lazily loads the resource, and it remains valid
     * after {@link #dispose()} - it simply gets invalidated and loads the resource again from a possibly new path.
     *
     * @param type      The resource type.
     * @param namespace The namespace. The standard namespace is used when this is null.
     * @param name      The resource name.
     * @return The loaded resource. If the resource type returns a null fallback, this may be null when loading fails.
     *
     * @throws NullPointerException     When the type or name is null.
     * @throws IllegalArgumentException When the resource type is not yet registered.
     * @see #get(ResourceType, String)
     * @see Handle
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public <R extends Resource> Handle<R> handle(ResourceType<R> type, String namespace, String name) {
        if (type == null) {
            throw new NullPointerException("type = null");
        }
        if (namespace == null) {
            namespace = stdNamespace;
        }
        if (name == null) {
            throw new NullPointerException("name = null");
        }
        if (!types.containsKey(type)) {
            throw new IllegalArgumentException("Unknown resource type, make sure to register it first");
        }

        ResourceSet<R> set = (ResourceSet<R>) types.get(type);
        return set.handle(namespace, name);
    }

    /**
     * Obtains a {@link Handle} by type and name, under the standard namespace. A handle lazily loads the resource, and
     * it remains valid after {@link #dispose()} - it simply gets invalidated and loads the resource again from a
     * possibly new path.
     *
     * @param type The resource type.
     * @param name The resource name.
     * @return The loaded resource. If the resource type returns a null fallback, this may be null when loading fails.
     *
     * @throws NullPointerException     When the type or name is null.
     * @throws IllegalArgumentException When the resource type is not yet registered.
     * @see #get(ResourceType, String)
     * @see Handle
     * @since 1.0
     */
    public <R extends Resource> Handle<R> handle(ResourceType<R> type, String name) {
        return handle(type, stdNamespace, name);
    }

    /**
     * Disposes all resources by calling {@link Resource#dispose()} on all cached resources, and then clears the cache.
     * If any resource fails disposing, it is logged to the {@link ResourceLogger}.
     *
     * @see Resource#dispose()
     * @see #dispose(Path)
     * @since 1.0
     */
    public void dispose() {
        logger.logDispose();
        types.forEach((type, set) -> set.dispose());
    }

    /**
     * Disposes all resources by calling {@link Resource#dispose()} on all cached resources, and then clears the cache.
     * If any resource fails disposing, it is logged to the {@link ResourceLogger}. It then sets the new root path. This
     * is particularly useful if you wanna switch root path and reload all resources.
     *
     * @see Resource#dispose()
     * @see #dispose()
     * @since 1.0
     */
    public void dispose(Path newRoot) {
        if (newRoot == null) {
            throw new NullPointerException("newRoot = null");
        }

        dispose();
        root = newRoot;
    }

    /**
     * Locates some resource file through the given class loader, and returns the containing folder. This may be a file
     * inside a JAR file, but the NIO {@link Path} system should be able to deal with that. The located resource should
     * be a file because of how Java resources function. Typically, you'd just create a dummy resource at the root of
     * your resource tree and locate it using this method.
     *
     * @param rootFile The root file to locate.
     * @return The path if the root file was found, otherwise null.
     *
     * @since 1.0
     */
    public static Path classpath(String rootFile, ClassLoader loader) {
        URL root = loader.getResource(rootFile);
        if (root == null) {
            return null;
        }
        try {
            return Paths.get(root.toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ResourceSet<R extends Resource> {
        private final ResourceManager res;
        private final ResourceType<R> type;
        private final String directory;
        private final Map<String, Map<String, R>> resources = new HashMap<>();
        private final Map<String, Map<String, WeakReference<RmsHandle<R>>>> handles = new HashMap<>();

        private ResourceSet(ResourceManager res, ResourceType<R> type, String directory) {
            this.res = res;
            this.type = type;
            this.directory = directory;
        }

        public RmsHandle<R> handle(String ns, String name) {
            Map<String, WeakReference<RmsHandle<R>>> nshandles = handles.computeIfAbsent(ns, k -> new HashMap<>());
            RmsHandle<R> handle;
            if (!nshandles.containsKey(name)) {
                handle = new RmsHandle<>(res, type, ns, name);
                nshandles.put(name, new WeakReference<>(handle));
            } else {
                WeakReference<RmsHandle<R>> ref = nshandles.get(name);
                handle = ref.get();

                // If handle got dropped, create a new one
                if (handle == null) {
                    handle = new RmsHandle<>(res, type, ns, name);
                    nshandles.put(name, new WeakReference<>(handle));
                }
            }
            return handle;
        }

        public R get(String ns, String name) {
            return resources.computeIfAbsent(ns, k -> new HashMap<>())
                            .computeIfAbsent(name, k -> load(ns, name));
        }

        private R load(String ns, String name) {
            Path dir = res.root.resolve(ns).resolve(directory);
            try {
                return type.load(res, ns, name, dir);
            } catch (Throwable exc) {
                res.logger.warnException(type, dir, directory, ns, name, exc);
                return type.createFallback(res, ns, name);
            }
        }

        public void dispose() {
            resources.forEach((ns, nsRes) -> {
                nsRes.forEach((name, rs) -> {
                    if (rs == null)
                        return;
                    try {
                        rs.dispose();
                    } catch (Throwable exc) {
                        res.logger.warnDisposeException(type, ns, name, rs, exc);
                    }
                });
                nsRes.clear();
            });
            resources.clear();

            // Drop the references that were garbage collected
            handles.forEach((ns, nsHandles) -> {
                var itr = nsHandles.values().iterator();
                while (itr.hasNext()) {
                    var ref = itr.next();
                    var handle = ref.get();
                    if (handle == null) {
                        itr.remove();
                    } else {
                        handle.invalidate();
                    }
                }
            });
        }
    }

    private static class RmsHandle<R extends Resource> implements Handle<R> {
        private final ResourceManager manager;
        private final ResourceType<R> type;
        private final String namespace;
        private final String name;
        private boolean loaded = false;
        private R resource = null;

        private RmsHandle(ResourceManager manager, ResourceType<R> type, String namespace, String name) {
            this.manager = manager;
            this.type = type;
            this.namespace = namespace;
            this.name = name;
        }

        @Override
        public ResourceManager manager() {
            return manager;
        }

        @Override
        public ResourceType<R> type() {
            return type;
        }

        @Override
        public String namespace() {
            return namespace;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Handle<R> load() {
            if (!loaded) {
                resource = manager.get(type, namespace, name);
            }
            return this;
        }

        @Override
        public R get() {
            load();
            return resource;
        }

        public void invalidate() {
            loaded = false;
            resource = null;
        }
    }
}
