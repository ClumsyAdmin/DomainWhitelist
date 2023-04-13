package org.clumsy.domainwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class DomainWhitelist extends JavaPlugin implements Listener {
    private List<String> allowedDomains;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        allowedDomains = config.getStringList("allowed-domains");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        try {
            String address = event.getHostname();

            if (!address.isEmpty()) {
                address = address.substring(0, address.lastIndexOf(':'));
                if (address.contains("\u0000")) {
                    address = address.substring(0, address.indexOf('\u0000'));
                }
                if (address.contains("fml")) {
                    address = address.substring(0, address.lastIndexOf("fml"));
                }

                getLogger().info("ServerAddress: " + address);

                if (!allowedDomains.contains(address.toLowerCase())) {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You can only connect using the allowed addresses.");
                } else {
                    getLogger().info("Player allowed to join");
                }
            }
        } catch (Exception e) {
            getLogger().warning("Failed to process player login event.");
        }
    }
}
