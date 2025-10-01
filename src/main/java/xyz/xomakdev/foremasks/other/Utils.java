package xyz.xomakdev.foremasks.other;

import org.bukkit.ChatColor;

/*

This file is part of ForeMasks.

ForeMasks is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Additional Condition: This software and all derivative works may not be used
for commercial purposes, including selling, licensing, or distributing for profit.

See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <https://www.gnu.org/licenses/agpl-3.0.html>.

*/

public class Utils {
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}