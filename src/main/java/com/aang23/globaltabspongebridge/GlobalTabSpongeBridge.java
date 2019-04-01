package com.aang23.globaltabspongebridge;

import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.network.PlayerConnection;

import com.google.inject.Inject;
import org.slf4j.Logger;

@Plugin(id = "globaltabspongebridge", name = "GlobalTabSpongeBridge", version = "1.0", description = "GlobalTab's Sponge Bridge")
public class GlobalTabSpongeBridge {

    @Inject
    private Logger logger;

    private EconomyService economyservice;

    @Listener
    public void onServerStart(GameAboutToStartServerEvent event) {

    }

    @Listener
    public void onTransaction(EconomyTransactionEvent event) {
        for (int i = 0; i < Sponge.getServer().getOnlinePlayers().size(); i++) {
            Player player = (Player) Sponge.getServer().getOnlinePlayers().toArray()[i];
            Sponge.getChannelRegistrar().getOrCreateRaw(this, "GlobalTab").sendTo(player,
                    buf -> buf.writeUTF("Balance")
                            .writeUTF(player.getName() + ":" + economyservice.getOrCreateAccount(player.getUniqueId())
                                    .get().getBalance(economyservice.getDefaultCurrency())));
        }
    }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getService().equals(EconomyService.class)) {
            economyservice = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent event) {
        if (event.getSource() instanceof Player) {
            Player player = Sponge.getServer().getPlayer(((Player) event.getSource()).getName()).get();
            Sponge.getChannelRegistrar().getOrCreateRaw(this, "GlobalTab").sendTo(player,
                    buf -> buf.writeUTF("Balance")
                            .writeUTF(player.getName() + ":" + economyservice.getOrCreateAccount(player.getUniqueId())
                                    .get().getBalance(economyservice.getDefaultCurrency())));
        }
    }
}
