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
import dev.runefox.rms.Resource;

/**
 * A {@link JsonResourceType} that uses {@linkplain Json#json5() JSON 5} to parse a resource.
 *
 * @author Samū
 * @see JsonResourceType
 * @see Json5CodecResourceType
 * @since 1.1
 */
public interface Json5ResourceType<R extends Resource> extends JsonResourceType<R> {
    @Override
    default Json json() {
        return Json.json5();
    }

    @Override
    default String extension() {
        return "json5";
    }
}
