package com.ricedotwho.paperbridge;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bruh(AsyncChatEvent event) {
        String content = PlainTextComponentSerializer.plainText().serialize(event.message());
        BridgePlugin.onMinecraftMessage(event.getPlayer().getName(), content);
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        BridgePlugin.onJoin(event.getPlayer().getName());
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        BridgePlugin.onLeave(event.getPlayer().getName());
    }
}
