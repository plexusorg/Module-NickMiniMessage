package dev.plex.module.nickmm;

import com.earth2me.essentials.Essentials;
import dev.plex.module.PlexModule;
import dev.plex.module.nickmm.command.NickMMCommand;
import org.bukkit.Bukkit;

public class NickMiniMessageModule extends PlexModule
{
    @Override
    public void enable()
    {
        if (!Bukkit.getPluginManager().isPluginEnabled("Essentials"))
        {
            throw new IllegalStateException("EssentialsX is required for this module to work!");
        }

        registerCommand(new NickMMCommand());
    }

    public static Essentials getEssentials()
    {
        return (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }
}
