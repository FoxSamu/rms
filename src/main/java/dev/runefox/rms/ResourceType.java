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
 * A type of resources. An instance of this interface is meant to load a resource of a certain type, and determine what
 * to do in case it cannot be loaded. Typically, you create one instance of each resource type in your application and
 * keep it in a {@code public static final} field.
 *
 * @author Samū
 * @since 1.0
 */
public interface ResourceType<R extends Resource> {
    /**
     * Attempt loading a resource. When loading fails, it falls back to {@link #createFallback}.
     *
     * @param res       The {@link ResourceManager}.
     * @param ns        The namespace.
     * @param name      The name of the resource.
     * @param directory The directory in which it should be.
     * @return The loaded resource.
     *
     * @throws Exception If loading fails.
     * @since 1.0
     */
    R load(ResourceManager res, String ns, String name, Path directory) throws Exception;

    /**
     * Create a fallback resource, which is done after it failed loading a specific resource. This may return null but
     * with the consequence that {@link ResourceManager#get(ResourceType, String, String)} will also return null.
     *
     * @param res  The {@link ResourceManager}.
     * @param ns   The namespace.
     * @param name The name of the resource.
     * @return The created fallback.
     *
     * @since 1.0
     */
    R createFallback(ResourceManager res, String ns, String name);
}
