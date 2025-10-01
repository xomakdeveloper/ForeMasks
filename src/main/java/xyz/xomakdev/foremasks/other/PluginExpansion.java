package xyz.xomakdev.foremasks.other;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.xomakdev.foremasks.ForeMasks;
import static xyz.xomakdev.foremasks.listeners.ForeListener.MASK_KEY;

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

public class PluginExpansion extends PlaceholderExpansion {
    private final ForeMasks plugin;
    public PluginExpansion(ForeMasks plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "foremasks";
    }

    @Override
    public @NotNull String getAuthor() {
        return "xomakdev";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return null;
        var helmet = player.getInventory().getHelmet();
        var allowed = plugin.getConfig().getStringList("allowed-items");
        if (helmet != null && allowed.contains(helmet.getType().name())) {
            var meta = helmet.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(MASK_KEY, PersistentDataType.BYTE)) {
                return "true";
            }
        }
        return "false";
    }
}