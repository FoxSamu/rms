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
import dev.runefox.json.codec.JsonCodec;
import dev.runefox.rms.Resource;
import dev.runefox.rms.ResourceManager;

import java.util.function.Supplier;

/**
 * A {@link Json5ResourceType} that decodes the JSON tree using a {@link JsonCodec}. You don't need to implement this
 * interface if you just want the default features, you can use {@link #create} to get an instance with basic
 * functionality.
 *
 * @author Samū
 * @since 1.1
 */
public interface Json5CodecResourceType<R extends Resource> extends JsonCodecResourceType<R>, Json5ResourceType<R> {
    // I don't know which of the interfaces takes priority if we don't
    // override these methods, so I'll override them just to be sure
    // (plus it's probably clearer what's happening).

    @Override
    default Json json() {
        return Json.json5();
    }

    @Override
    default String extension() {
        return "json5";
    }

    /**
     * Creates a basic {@link Json5CodecResourceType} with a fixed codec and a supplied fallback instance. This is a
     * convenience method to quickly have a resource type to load JSON resources.
     *
     * @param codec    The codec.
     * @param fallback The fallback supplier.
     * @return A new {@link JsonCodecResourceType}.
     *
     * @since 1.1
     */
    static <R extends Resource> Json5CodecResourceType<R> create(JsonCodec<R> codec, Supplier<R> fallback) {
        return new Json5CodecResourceType<>() {
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
