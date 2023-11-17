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

import java.util.function.Supplier;

/**
 * A resource handle. In some systems, you wanna be able to dispose all resources and reload them again without having
 * to restart the application. This means that every resource anywhere in the system must be reobtained from the
 * resource manager. A resource handle does this for you. Instead of obtaining the resource itself, you can obtain a
 * handle which loads the resource from the manager as soon as you need it. If it ever got disposed, it loads it again.
 *
 * @author Samū
 * @since 1.0
 */
public interface Handle<R extends Resource> extends Supplier<R> {
    /**
     * Returns the {@link ResourceManager} this handle was obtained from.
     *
     * @return The resource manager.
     * @since 1.0
     */
    ResourceManager manager();

    /**
     * Returns the {@link ResourceType} of the referenced resource.
     *
     * @return The resource's type.
     * @since 1.0
     */
    ResourceType<R> type();

    /**
     * Returns the namespace of the referred resource.
     *
     * @return The resource's namespace.
     * @since 1.0
     */
    String namespace();

    /**
     * Returns the name of the referred resource.
     *
     * @return The resource's name.
     * @since 1.0
     */
    String name();

    /**
     * Loads the referred resource if it's not already loaded. This can be used to pre-load the resource when getting
     * the handle, if that is preferred.
     *
     * @return This handle, for method chaining.
     * @since 1.0
     */
    Handle<R> load();

    /**
     * Obtains the referred resource. Only when this is called, does the resource manager actually load the resource.
     *
     * @return The referred resource.
     * @since 1.0
     */
    @Override
    R get();
}
