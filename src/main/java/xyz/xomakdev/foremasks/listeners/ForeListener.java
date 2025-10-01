package xyz.xomakdev.foremasks.listeners;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
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
import xyz.xomakdev.foremasks.ForeMasks;
import java.util.List;

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

public class ForeListener implements Listener {
    private static int taskId = -1;
    public static final NamespacedKey MASK_KEY = new NamespacedKey(ForeMasks.getInstance(), "mask");
    private static final String TEAM_NAME = "masked_players";
    public static void startTask() {
        var interval = ForeMasks.getInstance().getConfig().getInt("check-interval", 40);
        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                checkPlayers();
            }
        }.runTaskTimer(ForeMasks.getInstance(), 0, interval).getTaskId();
    }
    public static void stopTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
    public static void hideNametag(Player player) {
        if (ForeMasks.getInstance().isTabEnabled()) {
            hideNametagTab(player);
        } else {
            hideNametagVanilla(player);
        }
    }
    public static void showNametag(Player player) {
        if (ForeMasks.getInstance().isTabEnabled()) {
            showNametagTab(player);
        } else {
            showNametagVanilla(player);
        }
    }
    private static void hideNametagVanilla(Player player) {
        var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        var team = scoreboard.getTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.registerNewTeam(TEAM_NAME);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
        team.addEntry(player.getName());
    }
    private static void showNametagVanilla(Player player) {
        var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        var team = scoreboard.getTeam(TEAM_NAME);
        if (team != null) {
            team.removeEntry(player.getName());
        }
    }
    private static void hideNametagTab(Player player) {
        var tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer != null) {
            TabAPI.getInstance().getNameTagManager().hideNameTag(tabPlayer);
        }
    }
    private static void showNametagTab(Player player) {
        var tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer != null) {
            TabAPI.getInstance().getNameTagManager().showNameTag(tabPlayer);
        }
    }
    private static void checkPlayers() {
        var allowed = ForeMasks.getInstance().getConfig().getStringList("allowed-items");
        for (var player : Bukkit.getOnlinePlayers()) {
            var helmet = player.getInventory().getHelmet();
            var hasMask = helmet != null && allowed.contains(helmet.getType().name())
                    && helmet.getItemMeta().getPersistentDataContainer().has(MASK_KEY, PersistentDataType.BYTE);
            if (hasMask) {
                hideNametag(player);
            } else {
                showNametag(player);
            }
        }
    }
}