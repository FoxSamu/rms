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

import dev.runefox.rms.ResourceType;

/**
 * Basic JSON integration with RMS. This module provides a {@link ResourceType} that loads JSON formatted resources.
 *
 * @author Samū
 * @since 1.1
 */
module dev.runefox.rms.json {
    exports dev.runefox.rms.json;
    requires dev.runefox.rms;
    requires dev.runefox.json;
}
