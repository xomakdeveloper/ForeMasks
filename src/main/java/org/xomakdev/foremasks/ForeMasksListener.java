package org.xomakdev.foremasks;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

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

public class ForeMasksListener implements Listener {

    private static int taskID = -1;
    public static final NamespacedKey maskKey = new NamespacedKey(ForeMasks.getInstance(), "mask");

    public static void startTask() {
        int interval = ForeMasks.getInstance().getConfig().getInt("check-interval", 40);
        taskID = new BukkitRunnable() {
            @Override
            public void run() {
                checkPlayers();
            }
        }.runTaskTimer(ForeMasks.getInstance(), 0, interval).getTaskId();
    }

    public static void stopTask() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public static void hideNametag(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("masked_players");
        if (team == null) {
            team = scoreboard.registerNewTeam("masked_players");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
        team.addEntry(player.getName());
    }

    public static void showNametag(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("masked_players");

        if (team != null) {
            team.removeEntry(player.getName());
        }
    }

    private static void checkPlayers() {
        List<String> allowed = ForeMasks.getInstance().getConfig().getStringList("allowed-items");

        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack helmet = player.getInventory().getHelmet();
            boolean hasMask = false;

            if (helmet != null && allowed.contains(helmet.getType().name())) {
                PersistentDataContainer data = helmet.getItemMeta().getPersistentDataContainer();
                if (data.has(maskKey, PersistentDataType.BYTE)) {
                    hasMask = true;
                }
            }

            if (hasMask) {
                hideNametag(player);
            } else {
                showNametag(player);
            }
        }
    }

}
