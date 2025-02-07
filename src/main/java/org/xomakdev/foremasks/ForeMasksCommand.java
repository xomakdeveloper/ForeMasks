package org.xomakdev.foremasks;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

public class ForeMasksCommand implements CommandExecutor, TabCompleter {

    private final ForeMasks plugin;

    public ForeMasksCommand(ForeMasks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfig().getString("prefix");
        if (!(sender instanceof Player)) {
            sender.sendMessage(cr.color(plugin.getConfig().getString("only_players_cmd", "%PREFIX% &cТолько игроки могут использовать эту команду.")
                    .replace("%PREFIX%", prefix)));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(cr.color(plugin.getConfig().getString("use_cmd", "%PREFIX% &fИспользование: /foremasks &f<&ereload&f|&esetmask&f|&eunsetmask&f>")
                    .replace("%PREFIX%", prefix)));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                ForeMasks.getInstance().reloadConfig();
                player.sendMessage(cr.color(plugin.getConfig().getString("reload_config", "%PREFIX% &aКонфигурация перезагружена!")
                        .replace("%PREFIX%", prefix)));
                break;

            case "setmask":
            case "unsetmask":
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType() == Material.AIR) {
                    player.sendMessage(cr.color(plugin.getConfig().getString("mask_no_item", "%PREFIX% &cЭтот предмет нельзя использовать как маску!")
                            .replace("%PREFIX%", prefix)));
                    return true;
                }

                // Проверка, что предмет разрешен в списке allowed-items
                List<String> allowed = ForeMasks.getInstance().getConfig().getStringList("allowed-items");
                if (allowed == null || !allowed.contains(item.getType().name())) {
                    player.sendMessage(cr.color(plugin.getConfig().getString("mask_no_item", "%PREFIX% &cЭтот предмет нельзя использовать как маску!")
                            .replace("%PREFIX%", prefix)));
                    return true;
                }

                if (args[0].equalsIgnoreCase("setmask")) {
                    item.editMeta(meta -> {
                        PersistentDataContainer data = meta.getPersistentDataContainer();
                        data.set(maskKey, PersistentDataType.BYTE, (byte) 1);
                    });
                    player.sendMessage(cr.color(plugin.getConfig().getString("mask_set", "%PREFIX% &aМаска успешно установлена на предмет в руке")
                            .replace("%PREFIX%", prefix)));

                } else if (args[0].equalsIgnoreCase("unsetmask")) {
                    item.editMeta(meta -> {
                        PersistentDataContainer data = meta.getPersistentDataContainer();
                        data.remove(maskKey);
                    });
                    player.sendMessage(cr.color(plugin.getConfig().getString("mask_unset", "%PREFIX% &aМаска успешно снята с предмета в руке")
                            .replace("%PREFIX%", prefix)));
                }
                break;

            default:
                player.sendMessage(cr.color(plugin.getConfig().getString("unknown_agrument", "%PREFIX% &cНеизместный аргумент")
                        .replace("%PREFIX%", prefix)));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "setmask", "unsetmask").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}