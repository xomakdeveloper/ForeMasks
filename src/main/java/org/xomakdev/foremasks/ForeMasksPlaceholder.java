package org.xomakdev.foremasks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.xomakdev.foremasks.ForeMasksListener.maskKey;

/*
 * This file is part of ForeMasks.
 *
 * ForeMasks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Additional Condition: This software and all derivative works may not be used
 * for commercial purposes, including selling, licensing, or distributing for profit.
 *
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/agpl-3.0.html>.
 */

public class ForeMasksPlaceholder extends PlaceholderExpansion {

    private final ForeMasks plugin;

    public ForeMasksPlaceholder(ForeMasks plugin) {
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
        return ForeMasks.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return null;

        ItemStack helmet = player.getInventory().getHelmet();
        List<String> allowed = plugin.getConfig().getStringList("allowed-items");

        if (helmet != null && allowed.contains(helmet.getType().name())) {
            ItemMeta meta = helmet.getItemMeta();
            if (meta != null) {
                if (meta.getPersistentDataContainer().has(maskKey, PersistentDataType.STRING)) {
                    return "true";
                }
            }
        }
        return "false";
    }
}
