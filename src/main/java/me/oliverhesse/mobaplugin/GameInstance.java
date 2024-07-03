package me.oliverhesse.mobaplugin;

import me.oliverhesse.mobaplugin.CustomEntities.Tower;
import me.oliverhesse.mobaplugin.GameEvents.GameStateChange;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class GameInstance implements Listener{


    public enum GameState {
        PreGame,
        InGame,
        PostGame
    };

    private GameState CURRENT_GAME_STATE = GameState.PreGame;
    private final Plugin plugin;
    private final Location map_center;
    private final UUID GAME_ID;

    private Player[] BlueTeam = new Player[3];
    private Player[] RedTeam = new Player[3];



    private HashMap<String, Tower[]> lanes = new HashMap<>();
    public GameInstance(Plugin plugin, Location map_center,UUID game_id){
        this.plugin = plugin;
        this.map_center = map_center;
        this.GAME_ID = game_id;

        lanes.put("BlueLane1",new Tower[2]);
        lanes.put("RedLane1",new Tower[2]);

        lanes.put("BlueLane2",new Tower[2]);
        lanes.put("RedLane2",new Tower[2]);

        lanes.put("BlueLane3",new Tower[2]);
        lanes.put("RedLane3",new Tower[2]);

    }

    public void clear_maps(){
        for(Tower[] lane : lanes.values()){
            for(Tower tower : lane){
                if(tower != null){
                    tower.destroy();
                }
            }
        }
    }

    public void build_towers(){
        //need all Locations for towers that i will work out later

    }
    public void respawn_player(){}
    public void tp_blue_team(){}
    public void tp_red_team(){}
    public boolean Start_Game(Player[] BlueTeam,Player[] RedTeam){
        if(CURRENT_GAME_STATE != GameState.PreGame){
            return false;
        }
        this.BlueTeam = BlueTeam;
        this.RedTeam = RedTeam;

        clear_maps();

        build_towers();

        tp_blue_team();
        tp_red_team();
        CURRENT_GAME_STATE = GameState.InGame;
        GameStateChange change_event = new GameStateChange(GameState.PreGame,GameState.InGame,GAME_ID);
        change_event.callEvent();
        return true;
    }



}
