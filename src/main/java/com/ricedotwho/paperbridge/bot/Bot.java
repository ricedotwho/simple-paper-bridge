package com.ricedotwho.paperbridge.bot;

import com.ricedotwho.paperbridge.BridgePlugin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Bot extends ListenerAdapter {
    private final BridgePlugin plugin;
    private static JDA api;

    public Bot(BridgePlugin plugin) {
        this.plugin = plugin;
    }

    public static void create(String token, BridgePlugin plugin) {
        api = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new Bot(plugin))
                .build();

        try {
            api.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        api.updateCommands().addCommands(
                Commands.slash("online", "Gets the online players")
        ).queue();
    }

    public static void sendMessage(long channelId, String content) {
        TextChannel channel = api.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(content).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("online")) {
            List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            List<MessageEmbed.Field> fields = new ArrayList<>();
            names.forEach(s -> fields.add(new MessageEmbed.Field("", s, false)));
            MessageEmbed embed = new MessageEmbed("", "Online Players", "", EmbedType.ARTICLE, null, 0, null, null, null, null, null, null, fields);
            event.replyEmbeds(embed).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getChannel().getIdLong() != this.plugin.getConfig().getLong("channel")) return;
        String sender = event.getAuthor().getEffectiveName();
        String content = event.getMessage().getContentRaw();
        BridgePlugin.onDiscordMessage(sender, content);
    }
}
