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

import java.io.BufferedReader;
import java.io.StringWriter;

/**
 * A {@link TextResourceType} that reads the entire file into a {@link String} and then loads a resource from it.
 *
 * @author Samū
 * @since 1.0
 */
public interface StringResourceType<R extends Resource> extends TextResourceType<R> {
    @Override
    default R load(ResourceManager res, String ns, String name, BufferedReader in) throws Exception {
        StringWriter out = new StringWriter();
        in.transferTo(out);
        in.close();

        return load(res, ns, name, out.toString());
    }

    /**
     * Attempt loading a resource. When loading fails, it falls back to {@link #createFallback}.
     *
     * @param res  The {@link ResourceManager}.
     * @param ns   The namespace.
     * @param name The name of the resource.
     * @param text The {@link String} contents of the file.
     * @return The loaded resource.
     *
     * @throws Exception If loading fails.
     * @since 1.0
     */
    R load(ResourceManager res, String ns, String name, String text) throws Exception;
}
