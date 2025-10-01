package xyz.xomakdev.foremasks.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import xyz.xomakdev.foremasks.ForeMasks;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static xyz.xomakdev.foremasks.listeners.ForeListener.MASK_KEY;
import static xyz.xomakdev.foremasks.other.Utils.color;

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

public class ForeCommand implements CommandExecutor, TabCompleter {
    private final ForeMasks plugin;
    private final String prefix;
    public ForeCommand(ForeMasks plugin) {
        this.plugin = plugin;
        this.prefix = color(plugin.getConfig().getString("prefix", "&7[&2Fore&7] "));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + color("&cТолько игроки!"));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(prefix + color("&f/foremasks <reload|setmask|unsetmask>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadConfig();
                player.sendMessage(prefix + color("&aКонфиг перезагружен."));
                return true;
            }
            case "setmask", "unsetmask" -> handleMaskAction(player, args[0].equalsIgnoreCase("setmask"));
            default -> {
                player.sendMessage(prefix + color("&cНеизвестная команда."));
                return true;
            }
        };
        return true;
    }
    private boolean handleMaskAction(Player player, boolean set) {
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(prefix + color("&cПредмет не в руке!"));
            return true;
        }
        var allowed = plugin.getConfig().getStringList("allowed-items");
        if (!allowed.contains(item.getType().name())) {
            player.sendMessage(prefix + color("&cЭтот предмет не маску!"));
            return true;
        }
        item.editMeta(meta -> {
            var pdc = meta.getPersistentDataContainer();
            if (set) {
                pdc.set(MASK_KEY, PersistentDataType.BYTE, (byte) 1);
            } else {
                pdc.remove(MASK_KEY);
            }
        });
        player.sendMessage(prefix + color(set ? "&aМаска установлена." : "&aМаска снята."));
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "setmask", "unsetmask").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}