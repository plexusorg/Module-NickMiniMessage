package dev.plex.module.nickmm.command;

import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;
import dev.plex.command.SimplePlexCommand;
import dev.plex.command.source.RequiredCommandSource;
import dev.plex.module.nickmm.NickMiniMessageModule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NickMMCommand extends SimplePlexCommand
{
    private final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();
    private final LegacyComponentSerializer legacyComponent = LegacyComponentSerializer.legacySection();
    private final MiniMessage miniMessage = MiniMessage.builder().tags(new NicknameTagResolver()).build();

    public NickMMCommand()
    {
        super(command("nickmm")
                .description("Change your nickname using MiniMessage formatting!")
                .usage("/<command> <nick>")
                .aliases("nickminimessage")
                .permission("plex.nickmm")
                .source(RequiredCommandSource.IN_GAME)
                .build());
    }

    @Override
    protected Component execute(@NotNull CommandSender commandSender, @Nullable Player player, @NotNull String[] args)
    {
        if (!Bukkit.getPluginManager().isPluginEnabled("Essentials"))
        {
            return Component.text("Essentials is not enabled!", NamedTextColor.RED);
        }

        if (args.length == 0)
        {
            return usage();
        }

        final Component nick = miniMessage.deserialize(args[0]).clickEvent(null).hoverEvent(null);
        final String plain = plainText.serialize(nick);

        if (plain.length() > NickMiniMessageModule.getEssentials().getSettings().getMaxNickLength()
                && !commandSender.hasPermission("plex.nickmm.ignore_length_limit"))
        {
            return mmString(I18n.tlLiteral("nickTooLong"));
        }

        if (!commandSender.hasPermission("plex.nickmm.ignore_matching"))
        {
            for (final User user : NickMiniMessageModule.getEssentials().getOnlineUsers())
            {
                final String name = user.getNickname() != null ? plainText.serialize(legacyComponent.deserialize(user.getNickname())) : user.getName();

                if (name.equalsIgnoreCase(plain) && !user.getUUID().equals(player.getUniqueId()))
                {
                    return mmString(I18n.tlLiteral("nickInUse"));
                }
            }
        }

        final String legacy = legacyComponent.serialize(nick);
        User essentialsUser = NickMiniMessageModule.getEssentials().getUser(player);
        essentialsUser.setNickname(legacy);
        essentialsUser.setDisplayNick();

        return mmString(I18n.tlLiteral("nickSet", legacy));
    }

    @Override
    protected @NotNull List<String> suggestions(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args)
    {
        return Collections.emptyList();
    }

    private static class NicknameTagResolver implements TagResolver
    {
        private static final TagResolver STANDARD_RESOLVER = TagResolver.standard();
        private static final List<String> IGNORED_TAGS = Arrays.asList(
                "click",
                "hover",
                "insertion",
                "insert",
                "obfuscated",
                "obf",
                "br",
                "newline",
                "lang",
                "key",
                "translate");

        @Override
        public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException
        {
            return IGNORED_TAGS.contains(name.toLowerCase()) ? null : STANDARD_RESOLVER.resolve(name, arguments, ctx);
        }

        @Override
        public boolean has(@NotNull String name)
        {
            return STANDARD_RESOLVER.has(name);
        }
    }
}
