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

public enum ContentResourceType implements StringResourceType<ContentResource> {
    INSTANCE;

    @Override
    public ContentResource load(ResourceManager res, String ns, String name, String content) {
        return new ContentResource(content);
    }

    @Override
    public ContentResource createFallback(ResourceManager res, String ns, String name) {
        return new ContentResource("FAILED");
    }

    @Override
    public String extension() {
        return "txt";
    }
}
