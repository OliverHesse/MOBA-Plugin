package me.oliverhesse.mobaplugin;

import me.oliverhesse.mobaplugin.CustomEntities.Minion;
import me.oliverhesse.mobaplugin.CustomEntities.Tower;
import me.oliverhesse.mobaplugin.GameEvents.GameStateChange;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameInstance implements Listener,Runnable{


    public enum GameState {
        PreGame,
        InGame,
        PostGame
    };

    private GameState CURRENT_GAME_STATE = GameState.PreGame;
    private Integer GAME_TIME = 0;
    private Integer MINION_SPAWN_FREQUENCY = 60;
    private Integer TIME_SINCE_LAST_MINION_SPAWN = 20;
    private Integer RESPAWN_TIMER = 10;
    private BukkitTask GAME_TIMER;
    private final Plugin plugin;
    private final Location map_center;
    private final UUID GAME_ID;
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    private Team BlueTeamS;
    private Team RedTeamS;
    private Player[] BlueTeam = new Player[3];
    private Player[] RedTeam = new Player[3];

    private HashMap<String, List<Minion>> mobs = new HashMap<>();

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

        mobs.put("RedMinions",new ArrayList<>());
        mobs.put("BlueMinions",new ArrayList<>());


        //register my teams
        BlueTeamS = scoreboard.registerNewTeam(game_id.toString()+"?Blue");
        RedTeamS = scoreboard.registerNewTeam(game_id.toString()+"?Red");

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
    @Override
    public void run(){
        main_loop();
    }
    public void main_loop(){
        //this function should be run every tick
        GAME_TIME += 1;
        if(TIME_SINCE_LAST_MINION_SPAWN == 0){
            spanw_minions();
            TIME_SINCE_LAST_MINION_SPAWN = MINION_SPAWN_FREQUENCY;
        }else{
            TIME_SINCE_LAST_MINION_SPAWN -= 1;
        }
    }
    public void build_towers(){
        //need all Locations for towers that i will work out later

    }
    public void spanw_minions(){
        Bukkit.broadcast(Component.text("Minions Where Spawned"));
    }
    public void respawn_player(Player player){
        if(BlueTeamS.hasPlayer(player)){
            //respawn at blue base
            tp_blue_team(player);
        }else{
            tp_red_team(player);
        }
    }
    public void tp_blue_team(Player player){}
    public void tp_red_team(Player player){}
    public boolean Start_Game(Player[] BlueTeam,Player[] RedTeam){
        if(CURRENT_GAME_STATE != GameState.PreGame){
            return false;
        }
        this.BlueTeam = BlueTeam;
        this.RedTeam = RedTeam;

        clear_maps();

        build_towers();

        for(Player player :BlueTeam){
            BlueTeamS.addPlayer(player);
            tp_blue_team(player);
        }
        for(Player player :RedTeam){
            RedTeamS.addPlayer(player);
            tp_blue_team(player);
        }
        CURRENT_GAME_STATE = GameState.InGame;
        GameStateChange change_event = new GameStateChange(GameState.PreGame,GameState.InGame,GAME_ID);
        change_event.callEvent();

        Bukkit.broadcast(Component.text("Game Started"));
        GAME_TIMER = Bukkit.getScheduler().runTaskTimer(plugin,this,0L,20L);
        return true;
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        String UUIDString = container.get(new NamespacedKey(plugin,"GameID"),PersistentDataType.STRING);
        String player_team = container.get(new NamespacedKey(plugin,"GameTeam"),PersistentDataType.STRING);
        if(UUIDString == null){
            return;
        }
        UUID gameID = UUID.fromString(UUIDString);
        if(gameID != GAME_ID){
            return;
        }
        Location respawn_location;
        //this player is from our game and died

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                // Your one-time task code here
               respawn_player(player);
            }
        }, RESPAWN_TIMER*20);
        event.setCancelled(true);
    }

}
