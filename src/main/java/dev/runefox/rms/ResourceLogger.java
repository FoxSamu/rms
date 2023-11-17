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

import java.nio.file.Path;

/**
 * The resource management system (RMS) often encounters some cases where it may want to log something. Logging is
 * usually done by some logging library in the host application. A {@link ResourceLogger} is an interface that can be
 * implemented to log using your application's logging system.
 *
 * @author Samū
 * @since 1.0
 */
public interface ResourceLogger {
    /**
     * Log that an exception has occurred when loading a resource, and that it used the fallback instead.
     *
     * @param type      The resource type.
     * @param path      The full path to the directory in which the resource should have been.
     * @param directory The directory name which was given with the resource type upon registration.
     * @param namespace The namespace of the resource.
     * @param name      The name of the resource.
     * @param exception The exception that was caught.
     * @since 1.0
     */
    void warnException(ResourceType<?> type, Path path, String directory, String namespace, String name, Throwable exception);

    /**
     * Log that disposing all resources has started.
     *
     * @since 1.0
     */
    void logDispose();

    /**
     * Log that an exception has occurred when disposing a resource.
     *
     * @param type      The resource type.
     * @param namespace The namespace of the resource.
     * @param name      The name of the resource.
     * @param resource  The resource instance.
     * @param exception The exception that was caught.
     * @since 1.0
     */
    void warnDisposeException(ResourceType<?> type, String namespace, String name, Resource resource, Throwable exception);

    /**
     * A {@link ResourceLogger} that logs nothing. All it does is rethrowing any {@link Error}s. It is not recommended
     * to use this, try using {@link #SIMPLE} instead.
     *
     * @since 1.0
     */
    static ResourceLogger NULL = new ResourceLogger() {
        @Override
        public void warnException(ResourceType<?> type, Path path, String directory, String ns, String name, Throwable exception) {
            // Rethrow cuz Error too important
            if (exception instanceof Error e) {
                throw e;
            }
        }

        @Override
        public void logDispose() {
        }

        @Override
        public void warnDisposeException(ResourceType<?> type, String namespace, String name, Resource resource, Throwable exception) {
            // Rethrow cuz Error too important
            if (exception instanceof Error e) {
                throw e;
            }
        }
    };

    /**
     * A {@link ResourceLogger} that logs some basic info to the standard output. It rethrows any {@link Error}.
     *
     * @since 1.0
     */
    static ResourceLogger SIMPLE = new ResourceLogger() {
        @Override
        public void warnException(ResourceType<?> type, Path path, String directory, String ns, String name, Throwable exception) {
            System.err.println("Failed to load resource " + ns + ":" + name);
            System.err.println("- Type:      " + type);
            System.err.println("- Directory: " + directory);
            System.err.println("- Path:      " + path);
            exception.printStackTrace();

            // Rethrow cuz Error too important
            if (exception instanceof Error e) {
                throw e;
            }
        }

        @Override
        public void logDispose() {
            System.out.println("Disposing resources");
        }

        @Override
        public void warnDisposeException(ResourceType<?> type, String ns, String name, Resource resource, Throwable exception) {
            System.err.println("Failed to dispose resource " + ns + ":" + name);
            System.err.println("- Type:     " + type);
            System.err.println("- Instance: " + resource);
            exception.printStackTrace();

            // Rethrow cuz Error too important
            if (exception instanceof Error e) {
                throw e;
            }
        }
    };
}
