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

public class TestRMS {
    public static void main(String[] args) {
        Path path1 = ResourceManager.classpath("path1/.rmsroot", TestRMS.class.getClassLoader());
        Path path2 = ResourceManager.classpath("path2/.rmsroot", TestRMS.class.getClassLoader());

        ResourceManager mgr = new ResourceManager(path1, "ns");
        mgr.register(ContentResourceType.INSTANCE, "content");

        Handle<ContentResource> handle1 = mgr.handle(ContentResourceType.INSTANCE, "content1");
        Handle<ContentResource> handle2 = mgr.handle(ContentResourceType.INSTANCE, "content2");

        System.out.println(handle1.get().content());
        System.out.println(handle2.get().content());

        mgr.dispose(path2);

        System.out.println(handle1.get().content());
        System.out.println(handle2.get().content());
    }
}
