package me.oliverhesse.mobaplugin.Commands.DevCommands;

import me.oliverhesse.mobaplugin.GameInstance;
import me.oliverhesse.mobaplugin.MOBAPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class StartGameCommand implements CommandExecutor {
    private final Plugin plugin;

    public StartGameCommand(Plugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player){
            GameInstance newGame = new GameInstance(plugin,player.getLocation(), UUID.randomUUID());
            newGame.Start_Game(new Player[3],new Player[3]);
            ((MOBAPlugin) plugin).addGame(newGame);
            return true;
        }
        return false;
    }
}
