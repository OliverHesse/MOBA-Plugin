package me.oliverhesse.mobaplugin;

import me.oliverhesse.mobaplugin.Commands.DevCommands.StartGameCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MOBAPlugin extends JavaPlugin {
    private List<GameInstance> temp = new ArrayList<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("start_game").setExecutor(new StartGameCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void addGame(GameInstance game){
        temp.add(game);
    }
}
