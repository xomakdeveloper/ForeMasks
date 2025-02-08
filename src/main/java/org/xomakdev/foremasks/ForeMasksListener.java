package org.xomakdev.foremasks;

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

import java.util.List;

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
        if (ForeMasks.getInstance().isTABEnabled()) {
            hideNametagTAB_API(player);
        } else {
            hideNametagVanilla(player);
        }
    }

    public static void showNametag(Player player) {
        if (ForeMasks.getInstance().isTABEnabled()) {
            showNametagTAB_API(player);
        } else {
            showNametagVanilla(player);
        }
    }

    public static void hideNametagVanilla(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("masked_players");
        if (team == null) {
            team = scoreboard.registerNewTeam("masked_players");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
        team.addEntry(player.getName());
    }

    public static void showNametagVanilla(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("masked_players");
        if (team != null) {
            team.removeEntry(player.getName());
        }
    }

    public static void hideNametagTAB_API(Player player) {
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer != null) {
            TabAPI.getInstance().getNameTagManager().hideNameTag(tabPlayer);
        }
    }

    public static void showNametagTAB_API(Player player) {
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer != null) {
            TabAPI.getInstance().getNameTagManager().showNameTag(tabPlayer);
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
