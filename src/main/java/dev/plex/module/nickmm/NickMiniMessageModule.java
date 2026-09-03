package dev.plex.module.nickmm;

import com.earth2me.essentials.Essentials;
import dev.plex.module.PlexModule;
import dev.plex.module.nickmm.command.NickMMCommand;
import org.bukkit.Bukkit;

public class NickMiniMessageModule extends PlexModule
{
    private Essentials essentials;

    @Override
    public void load()
    {
        registerCommand(new NickMMCommand(this));
    }

    @Override
    public void enable()
    {
        if (!Bukkit.getPluginManager().isPluginEnabled("Essentials")
                || !(Bukkit.getPluginManager().getPlugin("Essentials") instanceof Essentials essentialsPlugin))
        {
            throw new IllegalStateException("EssentialsX is required for this module to work!");
        }
        essentials = essentialsPlugin;
    }

    public Essentials getEssentials()
    {
        if (essentials == null)
        {
            throw new IllegalStateException("EssentialsX is not available before the module is enabled");
        }
        return essentials;
    }
}
