package me.oliverhesse.mobaplugin.GameEvents;

import me.oliverhesse.mobaplugin.GameInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GameStateChange extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private GameInstance.GameState previous_state;
    private GameInstance.GameState new_state;
    private UUID game_id;
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    public GameInstance.GameState get_previous_state(){
        return this.previous_state;
    }
    public GameInstance.GameState get_new_state(){
        return this.new_state;
    }
    public UUID getGame_id(){
        return this.game_id;
    }

    public GameStateChange(GameInstance.GameState previous_state,GameInstance.GameState new_state,UUID game_id){
        this.game_id = game_id;
        this.previous_state = previous_state;
        this.new_state = new_state;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
