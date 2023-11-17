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

import dev.runefox.json.JsonNode;
import dev.runefox.json.codec.JsonCodec;
import dev.runefox.rms.Resource;
import dev.runefox.rms.ResourceManager;

import java.util.function.Supplier;

/**
 * A {@link JsonResourceType} that decodes the JSON tree using a {@link JsonCodec}. You don't need to implement this
 * interface if you just want the default features, you can use {@link #create} to get an instance with basic
 * functionality.
 *
 * @author Samū
 * @since 1.1
 */
public interface JsonCodecResourceType<R extends Resource> extends JsonResourceType<R> {
    @Override
    default R load(ResourceManager res, String ns, String name, JsonNode in) {
        return codec(res, ns, name).decode(in);
    }

    /**
     * Returns the {@link JsonCodec} to be used to decode resource instances.
     *
     * @param res  The resource manager.
     * @param ns   The namespace.
     * @param name The resource's name.
     * @return The {@link JsonCodec} to decode with.
     *
     * @since 1.1
     */
    JsonCodec<R> codec(ResourceManager res, String ns, String name);

    /**
     * Creates a basic {@link JsonCodecResourceType} with a fixed codec and a supplied fallback instance. This is a
     * convenience method to quickly have a resource type to load JSON resources.
     *
     * @param codec    The codec.
     * @param fallback The fallback supplier.
     * @return A new {@link JsonCodecResourceType}.
     *
     * @since 1.1
     */
    static <R extends Resource> JsonCodecResourceType<R> create(JsonCodec<R> codec, Supplier<R> fallback) {
        return new JsonCodecResourceType<>() {
            @Override
            public JsonCodec<R> codec(ResourceManager res, String ns, String name) {
                return codec;
            }

            @Override
            public R createFallback(ResourceManager res, String ns, String name) {
                return fallback.get();
            }
        };
    }
}
