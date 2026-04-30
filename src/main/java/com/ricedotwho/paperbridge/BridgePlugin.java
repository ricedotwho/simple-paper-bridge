package com.ricedotwho.paperbridge;

import com.ricedotwho.paperbridge.bot.Bot;
import com.ricedotwho.paperbridge.utils.DiscordWebhook;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BridgePlugin extends JavaPlugin {
    private static BridgePlugin INSTANCE;
    public static Logger LOGGER = LoggerFactory.getLogger("BridgePlugin");
    private static final String AVATAR_URL = "https://minotar.net/avatar/%s/100";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onEnable() {
        INSTANCE = this;
        PaperLib.suggestPaper(this);
        saveDefaultConfig();
        Bot.create(getConfig().getString("token"), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        broadcast("Server started");
    }

    @Override
    public void onDisable() {
        broadcast("Server stopped");
    }

    public static void onJoin(String player) {
        Bot.sendMessage(INSTANCE.getConfig().getLong("channel"), player + " joined the game");
    }

    public static void onLeave(String player) {
        Bot.sendMessage(INSTANCE.getConfig().getLong("channel"), player + " left the game");
    }

    public static void broadcast(String content) {
        Bot.sendMessage(INSTANCE.getConfig().getLong("channel"), content);
    }

    public static void onMinecraftMessage(String sender, String content) {
        executor.submit(() -> sendWebhookMessage(sender, content));
    }

    private static void sendWebhookMessage(String sender, String content) {
        DiscordWebhook hook = new DiscordWebhook(INSTANCE.getConfig().getString("webhook"));
        hook.setAvatarUrl(AVATAR_URL.formatted(sender));
        hook.setUsername(sender);
        hook.setContent(content.replace("@", "@/"));
        try {
            hook.execute();
        } catch (IOException e) {
            LOGGER.error("Webhook failed to execute!", e);
        }
    }

    public static void onDiscordMessage(String sender, String content) {
        Component component = Component.text("[").color(NamedTextColor.GRAY)
                .append(Component.text("Bridge").color(NamedTextColor.YELLOW))
                .append(Component.text("] ").color(NamedTextColor.GRAY))
                .append(Component.text(sender).color(NamedTextColor.WHITE))
                .append(Component.text(": ").color(NamedTextColor.WHITE))
                .append(Component.text(content).color(NamedTextColor.WHITE));
        Bukkit.broadcast(component);
    }
}
