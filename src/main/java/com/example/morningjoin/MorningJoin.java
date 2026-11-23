package com.example.morningjoin;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class MorningJoin extends JavaPlugin implements Listener {

    private UUID lastPlayerUuid = null;
    private long lastQuitTime = 0L;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Bukkit.getOnlinePlayers().size() == 1) {
            lastPlayerUuid = event.getPlayer().getUniqueId();
            lastQuitTime = System.currentTimeMillis();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Bukkit.getOnlinePlayers().size() != 1) {
            return;
        }

        Player player = event.getPlayer();
        long cooldownMinutes = getConfig().getLong("cooldown-minutes", 10L);
        long cooldownMillis = cooldownMinutes * 60 * 1000L;

        // Check anti-abuse cooldown unless player has bypass permission
        if (cooldownMillis > 0 && lastQuitTime > 0 && !player.hasPermission("morningjoin.bypass")) {
            long timeSinceQuit = System.currentTimeMillis() - lastQuitTime;
            boolean globalCooldown = getConfig().getBoolean("global-cooldown", false);
            boolean isSamePlayer = player.getUniqueId().equals(lastPlayerUuid);

            if ((globalCooldown || isSamePlayer) && timeSinceQuit < cooldownMillis) {
                return;
            }
        }

        boolean nightOnly = getConfig().getBoolean("night-only", true);
        long nightStartTime = getConfig().getLong("night-start-time", 10500L);
        long targetTime = getConfig().getLong("target-time", 1000L);
        boolean clearWeather = getConfig().getBoolean("clear-weather", true);
        List<String> worldNames = getConfig().getStringList("worlds");

        boolean changedAnyWorld = false;
        for (String worldName : worldNames) {
            World world = Bukkit.getWorld(worldName);
            if (world != null && world.getEnvironment() == World.Environment.NORMAL) {
                long currentTime = world.getTime(); // Always 0 - 23999 in Paper

                // Skip if night-only is enabled and it's earlier than the configured threshold
                if (nightOnly && currentTime < nightStartTime) {
                    continue;
                }

                world.setTime(targetTime);
                if (clearWeather) {
                    world.setStorm(false);
                    world.setThundering(false);
                }
                changedAnyWorld = true;
            }
        }

        String rawMessage = getConfig().getString("message", "");
        if (changedAnyWorld && rawMessage != null && !rawMessage.isBlank()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(rawMessage));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>MorningJoin configuration reloaded!"));
            return true;
        }
        return false;
    }
}
