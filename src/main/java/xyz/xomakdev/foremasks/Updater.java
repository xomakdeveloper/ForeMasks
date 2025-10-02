package xyz.xomakdev.foremasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Updater implements Listener {
    private final JavaPlugin plugin;
    private final String repoOwner;
    private final String repoName;
    private final String currentVersion;
    private final List<String> allVersions = new ArrayList<>();
    private int versionLag = 0;
    private boolean checked = false;
    private final boolean updaterEnabled;

    public Updater(JavaPlugin plugin, String repoOwner, String repoName) {
        this.plugin = plugin;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.currentVersion = plugin.getDescription().getVersion();

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (!configFile.exists() || !config.contains("updater")) {
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to create config.yml: " + e.getMessage());
                }
            }

            if (!config.contains("updater")) {
                String content = "";
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content += line + "\n";
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to read config.yml: " + e.getMessage());
                    content = "";
                }

                String newContent = "updater: true\n" + content.trim() + "\n";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                    writer.write(newContent);
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to write config.yml: " + e.getMessage());
                }
                config = YamlConfiguration.loadConfiguration(configFile);
            } else {
                config.set("updater", true);
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to save config.yml: " + e.getMessage());
                }
            }
        }

        this.updaterEnabled = config.getBoolean("updater", true);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                checkForUpdates();
            }
        }.runTaskAsynchronously(plugin);
    }

    private void checkForUpdates() {
        if (!updaterEnabled) {
            checked = true;
            return;
        }

        try {
            String apiUrl = "https://api.github.com/repos/" + repoOwner + "/" + repoName + "/releases";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; SpigotPlugin/1.0)");

            if (conn.getResponseCode() != 200) {
                plugin.getLogger().warning("GitHub API error: " + conn.getResponseCode());
                checked = true;
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JsonParser parser = new JsonParser();
            JsonArray releases = parser.parse(content.toString()).getAsJsonArray();
            allVersions.clear();

            for (JsonElement release : releases) {
                JsonObject obj = release.getAsJsonObject();
                String tag = obj.get("tag_name").getAsString().replace("v", "");
                allVersions.add(tag);
            }

            Collections.sort(allVersions, new VersionComparator());
            int currentIndex = allVersions.indexOf(currentVersion);
            int latestIndex = allVersions.size() - 1;
            versionLag = latestIndex - currentIndex;

            checked = true;

            String latestVersion = allVersions.get(latestIndex);
            String downloadUrl = "https://github.com/" + repoOwner + "/" + repoName + "/releases/latest";

            if (versionLag > 0) {
                String msg = "[" + plugin.getName() + "] Доступно новое обновление! : " + latestVersion + " (Текущая версия: " + currentVersion + ", вы отстаёте на " + versionLag + " версий.). Скачать обновление: " + downloadUrl;
                plugin.getLogger().info(ChatColor.stripColor(msg));
            } else {
                String msg = "[" + plugin.getName() + "] Доступных обновлений нету. Вы используете последнюю версию.";
                plugin.getLogger().info(ChatColor.stripColor(msg));
            }
        } catch (IOException | NullPointerException e) {
            plugin.getLogger().warning("Update check failed: " + e.getMessage());
            checked = true;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!checked || !updaterEnabled) return;
        Player player = event.getPlayer();
        if (player.isOp() && versionLag > 0) {
            String latestVersion = allVersions.get(allVersions.size() - 1);
            String downloadUrl = "https://github.com/" + repoOwner + "/" + repoName + "/releases/latest";
            String msg = ChatColor.GREEN + "[" + plugin.getName() + "] Доступно новое обновление! : " + latestVersion + " (Текущая версия: " + currentVersion + ", вы отстаёте на " + versionLag + " версий.). Скачать обновление: " + downloadUrl;
            player.sendMessage(msg);
        }
    }

    static class VersionComparator implements Comparator<String> {
        @Override
        public int compare(String v1, String v2) {
            String[] parts1 = v1.split("\\.");
            String[] parts2 = v2.split("\\.");
            for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
                int p1 = Integer.parseInt(parts1[i]);
                int p2 = Integer.parseInt(parts2[i]);
                if (p1 != p2) return Integer.compare(p1, p2);
            }
            return Integer.compare(parts1.length, parts2.length);
        }
    }
}