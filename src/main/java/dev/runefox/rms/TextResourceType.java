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
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link ResourceType} that loads from one specific text file in UTF-8, which has the same name as the resource.
 *
 * @author Samū
 * @since 1.0
 */
public interface TextResourceType<R extends Resource> extends ResourceType<R> {
    @Override
    default R load(ResourceManager res, String ns, String name, Path directory) throws Exception {
        Path resFile = locate(directory, name);
        try (BufferedReader reader = Files.newBufferedReader(resFile, charset())) {
            return load(res, ns, name, reader);
        }
    }

    /**
     * Locates the resource file. Override this to change the behaviour.
     *
     * @param directory The directory in which the resource should be.
     * @param name      The resource name.
     * @return The resource's path.
     *
     * @since 1.0
     */
    default Path locate(Path directory, String name) {
        return directory.resolve(name + "." + extension());
    }

    /**
     * Returns the charset of the file that the resource loader loads. Usually UTF-8.
     *
     * @return The charset.
     *
     * @since 1.0
     */
    default Charset charset() {
        return StandardCharsets.UTF_8;
    }

    /**
     * Attempt loading a resource. When loading fails, it falls back to {@link #createFallback}.
     *
     * @param res  The {@link ResourceManager}.
     * @param ns   The namespace.
     * @param name The name of the resource.
     * @param in   The {@link BufferedReader} of the file.
     * @return The loaded resource.
     *
     * @throws Exception If loading fails.
     * @since 1.0
     */
    R load(ResourceManager res, String ns, String name, BufferedReader in) throws Exception;

    /**
     * Returns the extension that resources of this type have.
     *
     * @return The file extension.
     *
     * @since 1.0
     */
    String extension();
}
