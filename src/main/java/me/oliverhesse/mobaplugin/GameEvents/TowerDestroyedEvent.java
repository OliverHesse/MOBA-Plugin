package me.oliverhesse.mobaplugin.GameEvents;

import me.oliverhesse.mobaplugin.CustomEntities.Tower;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TowerDestroyedEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Tower tower;
    private Entity attacker;

    public TowerDestroyedEvent(Tower tower,Entity attacker){
        this.tower = tower;
        this.attacker = attacker;
    }
    public Tower getTower(){
        return this.tower;
    }
    public Entity getAttacker(){
        return this.attacker;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
