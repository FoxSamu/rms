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

/**
 * A resource. Implementations usually are some in-memory form of a resource that was loaded with the RMS.
 *
 * @author Samū
 * @since 1.0
 */
public interface Resource {
    /**
     * Disposes the resource. Use this to clean up any native resources that this resource holds.
     *
     * @throws Exception When any exception occurs during disposing.
     * @since 1.0
     */
    void dispose() throws Exception;
}
