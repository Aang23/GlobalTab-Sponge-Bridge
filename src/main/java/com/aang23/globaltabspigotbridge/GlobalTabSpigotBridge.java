package com.aang23.globaltabspigotbridge;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class GlobalTabSpigotBridge extends JavaPlugin implements Listener {

    private Economy economy;

    @Override
    public void onEnable() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) {
            economy = rsp.getProvider();
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeecord:globaltab");

        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                sendBalanceToVelocity(online);
            }
        }, 20, 20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (player.isOnline()) {
                sendBalanceToVelocity(player);
            }
        }, 3);
    }

    private double getPlayerBalance(Player player) {
        return economy.getBalance(player);
    }

    private void sendBalanceToVelocity(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Balance");
        out.writeUTF(player.getName() + ":" + getPlayerBalance(player));

        player.sendPluginMessage(this, "bungeecord:globaltab", out.toByteArray());
    }
}
