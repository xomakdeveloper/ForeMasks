package xyz.xomakdev.foremasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xomakdev.foremasks.commands.ForeCommand;
import xyz.xomakdev.foremasks.listeners.ForeListener;
import xyz.xomakdev.foremasks.other.PluginExpansion;
import xyz.xomakdev.foremasks.other.Utils;
import static org.bukkit.Bukkit.getPluginManager;
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

public final class ForeMasks extends JavaPlugin {
    private static ForeMasks INSTANCE;
    private PluginExpansion expansion;
    private boolean tabEnabled;
    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        var cmd = getCommand("foremasks");
        cmd.setExecutor(new ForeCommand(this));
        cmd.setTabCompleter(new ForeCommand(this));
        getPluginManager().registerEvents(new ForeListener(), this);
        ForeListener.startTask();
        tabEnabled = getPluginManager().getPlugin("TAB") != null;
        getLogger().info(color(tabEnabled ? "&aTAB detected." : "&cTAB not detected."));
        if (getPluginManager().getPlugin("PlaceholderAPI") != null) {
            expansion = new PluginExpansion(this);
            expansion.register();
        }
        Metrics metrics = new Metrics(this, 27427);
        getLogger().info(color(" &2Fore Masks &7| &2v" + getDescription().getVersion() + " &fby @xomakdev"));
    }
    @Override
    public void onDisable() {
        ForeListener.stopTask();
        if (expansion != null) {
            expansion.unregister();
        }
        getLogger().info(color(" &2Fore Masks &7| &2v" + getDescription().getVersion() + " &fby @xomakdev"));
    }
    public boolean isTabEnabled() {
        return tabEnabled;
    }
    public static ForeMasks getInstance() {
        return INSTANCE;
    }
}