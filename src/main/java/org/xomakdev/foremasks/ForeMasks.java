package org.xomakdev.foremasks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

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

public final class ForeMasks extends JavaPlugin {

    private static ForeMasks instance;
    private ForeMasksPlaceholder placeholderExpansion;

    // Copyright (c) 2025 XomakDeveloper.
    //
    // This software is released under the AGPLv3 License with an additional non-commercial clause.
    // You are free to use, modify, and distribute it as long as it is not for commercial purposes.
    //
    // See the LICENSE (https://github.com/xomakdeveloper/ForeMasks-v1.0/blob/main/LICENSE) file for more details on the AGPLv3 and the additional clause.
    //-----------------------------------------------------------------------------------------------------------------------------------------------------
    // Авторские права (c) 2025 XomakDeveloper.
    //
    // Это программное обеспечение распространяется под лицензией AGPLv3 с дополнительным условием о запрете коммерческого использования.
    // Вы можете свободно использовать, изменять и распространять его, при условии, что это не будет происходить в коммерческих целях.
    //
    // Подробнее читайте в файле LICENSE (https://github.com/xomakdeveloper/ForeMasks-v1.0/blob/main/LICENSE) об условиях AGPLv3 и дополнительном условии.

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        Bukkit.getConsoleSender().sendMessage((cr.color("&2███████╗ ██████╗ ██████╗ ███████╗    ███╗   ███╗ █████╗ ███████╗██╗  ██╗███████╗")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2██╔════╝██╔═══██╗██╔══██╗██╔════╝    ████╗ ████║██╔══██╗██╔════╝██║ ██╔╝██╔════╝")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2█████╗  ██║   ██║██████╔╝█████╗      ██╔████╔██║███████║███████╗█████╔╝ ███████╗")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2██╔══╝  ██║   ██║██╔══██╗██╔══╝      ██║╚██╔╝██║██╔══██║╚════██║██╔═██╗ ╚════██║")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2██║     ╚██████╔╝██║  ██║███████╗    ██║ ╚═╝ ██║██║  ██║███████║██║  ██╗███████║")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2╚═╝      ╚═════╝ ╚═╝  ╚═╝╚══════╝    ╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝")));
        Bukkit.getConsoleSender().sendMessage((cr.color(" &2Fore Masks &7& &2v" + getDescription().getVersion() + " &fby xomakdev.exe")));
        Bukkit.getConsoleSender().sendMessage((cr.color(" &fSupport? - &9Discord: xomakdeveloperr &7& &bTelegram: @kofead")));

        getCommand("foremasks").setExecutor(new ForeMasksCommand(this));
        getCommand("foremasks").setTabCompleter(new ForeMasksCommand(this));

        getPluginManager().registerEvents(new ForeMasksListener(), this);

        ForeMasksListener.startTask();

        if (getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderExpansion = new ForeMasksPlaceholder(this);
            placeholderExpansion.register();
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage((cr.color("&2███████╗ ██████╗ ██████╗ ███████╗    ███╗   ███╗ █████╗ ███████╗██╗  ██╗███████╗")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2██╔════╝██╔═══██╗██╔══██╗██╔════╝    ████╗ ████║██╔══██╗██╔════╝██║ ██╔╝██╔════╝")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2█████╗  ██║   ██║██████╔╝█████╗      ██╔████╔██║███████║███████╗█████╔╝ ███████╗")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2██╔══╝  ██║   ██║██╔══██╗██╔══╝      ██║╚██╔╝██║██╔══██║╚════██║██╔═██╗ ╚════██║")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2██║     ╚██████╔╝██║  ██║███████╗    ██║ ╚═╝ ██║██║  ██║███████║██║  ██╗███████║")));
        Bukkit.getConsoleSender().sendMessage((cr.color("&2╚═╝      ╚═════╝ ╚═╝  ╚═╝╚══════╝    ╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝")));
        Bukkit.getConsoleSender().sendMessage((cr.color(" &2Fore Masks &7& &2v" + getDescription().getVersion() + " &fby xomakdev.exe")));
        Bukkit.getConsoleSender().sendMessage((cr.color(" &fSupport? - &9Discord: xomakdeveloperr &7& &bTelegram: @kofead")));

        ForeMasksListener.stopTask();
    }

    public static ForeMasks getInstance() {
        return instance;
    }
}
