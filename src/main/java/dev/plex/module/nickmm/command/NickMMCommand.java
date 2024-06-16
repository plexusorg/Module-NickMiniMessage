package dev.plex.module.nickmm.command;

import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;
import dev.plex.command.PlexCommand;
import dev.plex.command.annotation.CommandParameters;
import dev.plex.command.annotation.CommandPermissions;
import dev.plex.command.source.RequiredCommandSource;
import dev.plex.module.nickmm.NickMiniMessageModule;
import dev.plex.util.minimessage.SafeMiniMessage;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandParameters(name = "nickmm", description = "Change your nickname using MiniMessage formatting!", usage = "/<command> <nick>", aliases = "nickminimessage")
@CommandPermissions(permission = "plex.nickmm", source = RequiredCommandSource.IN_GAME)
public class NickMMCommand extends PlexCommand
{
    private final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();
    private final LegacyComponentSerializer legacyComponent = LegacyComponentSerializer.legacySection();

    @Override
    protected Component execute(@NotNull CommandSender commandSender, @Nullable Player player, @NotNull String[] strings)
    {
        if (!Bukkit.getPluginManager().isPluginEnabled("Essentials"))
        {
            return Component.text("Essentials is not enabled!", NamedTextColor.RED);
        }

        if (strings.length == 0)
        {
            return usage();
        }

        final Component nick = SafeMiniMessage.mmDeserializeWithoutEvents(strings[0]);
        final String plain = plainText.serialize(nick);

        if (plain.length() > NickMiniMessageModule.getEssentials().getSettings().getMaxNickLength()
                && !commandSender.hasPermission("plex.nickmm.ignore_length_limit"))
        {
            return legacyComponent.deserialize(I18n.tlLiteral("nickTooLong"));
        }

        if (!commandSender.hasPermission("plex.nickmm.ignore_matching"))
        {
            for (final User user : NickMiniMessageModule.getEssentials().getOnlineUsers())
            {
                final String name = user.getNickname() != null ? plainText.serialize(legacyComponent.deserialize(user.getNickname())) : user.getName();

                if (name.equalsIgnoreCase(plain) && !user.getUUID().equals(player.getUniqueId()))
                {
                    return legacyComponent.deserialize(I18n.tlLiteral("nickInUse"));
                }
            }
        }

        final String legacy = legacyComponent.serialize(nick);
        NickMiniMessageModule.getEssentials().getUser(player).setNickname(legacy);

        return legacyComponent.deserialize(I18n.tlLiteral("nickSet", legacy));
    }

    @Override
    public @NotNull List<String> smartTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        return Collections.emptyList();
    }
}
