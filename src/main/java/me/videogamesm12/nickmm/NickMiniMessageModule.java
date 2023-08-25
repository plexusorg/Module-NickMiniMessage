package me.videogamesm12.nickmm;

import com.earth2me.essentials.Essentials;
import dev.plex.module.PlexModule;
import me.videogamesm12.nickmm.command.NickMMCommand;
import org.bukkit.Bukkit;

public class NickMiniMessageModule extends PlexModule
{
    @Override
    public void enable()
    {
        if (!Bukkit.getPluginManager().isPluginEnabled("Essentials"))
        {
            throw new IllegalStateException("We need Essentials for this module to work!");
        }

        registerCommand(new NickMMCommand());
    }

    public static Essentials getEssentials()
    {
        return (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }
}
