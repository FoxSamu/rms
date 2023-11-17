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

package dev.runefox.rms.json;

import dev.runefox.json.Json;
import dev.runefox.json.JsonNode;
import dev.runefox.rms.Resource;
import dev.runefox.rms.ResourceManager;
import dev.runefox.rms.TextResourceType;

import java.io.BufferedReader;

/**
 * A {@link TextResourceType} that uses {@linkplain Json#json() JSON} to parse a resource.
 *
 * @author Samū
 * @see Json5ResourceType
 * @see JsonCodecResourceType
 * @since 1.1
 */
public interface JsonResourceType<R extends Resource> extends TextResourceType<R> {
    @Override
    default R load(ResourceManager res, String ns, String name, BufferedReader in) throws Exception {
        return load(res, ns, name, json().parse(in));
    }

    @Override
    default String extension() {
        return "json";
    }

    /**
     * Returns a {@link Json} instance that is used to parse a resource.
     *
     * @return The {@link Json} instance.
     *
     * @since 1.1
     */
    default Json json() {
        return Json.json();
    }

    /**
     * Attempt loading a resource. When loading fails, it falls back to {@link #createFallback}.
     *
     * @param res  The {@link ResourceManager}.
     * @param ns   The namespace.
     * @param name The name of the resource.
     * @param in   The {@link JsonNode} that was loaded.
     * @return The loaded resource.
     *
     * @throws Exception If loading fails.
     * @since 1.1
     */
    R load(ResourceManager res, String ns, String name, JsonNode in) throws Exception;
}
