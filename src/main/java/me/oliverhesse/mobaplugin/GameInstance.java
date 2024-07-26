package me.oliverhesse.mobaplugin;

import me.oliverhesse.mobaplugin.CustomEntities.Minion;
import me.oliverhesse.mobaplugin.CustomEntities.Tower;
import me.oliverhesse.mobaplugin.GameEvents.GameStateChange;
import me.oliverhesse.mobaplugin.GameEvents.TowerDestroyedEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;

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
    private Integer RESPAWN_TIME = 10;
    private BukkitTask GAME_TIMER;
    private final Plugin plugin;
    private final Location map_center;
    private final UUID GAME_ID;
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    private Team BlueTeam;
    private Team RedTeam;

    /*
    important data for relative map cords
    most likely create some sort of static relativeMap type to hold this data
    Map Center:

    World cords(Block) =[0, 0, 0]

    Red Player Base:

    Base cords(Block) =[94, 1, -94]
    Lane 1 Minion spawn(Block) =[68, 0, -80]
    Lane 2 Minion spawn(Block) =[70, 0, -70]
    Lane 3 Minion spawn(Block) =[80, 0, -68]
    Lane 1 Goal 1 (Block) =[-62, 0, -80]
    Lane 3 Goal 1 (Block) =[80, 0, 62]
    Lane 1 Tower 1 (Block) =[32, 0, -80]
    Lane 1 Tower 2 (Block) =[-45, 0, -80]
    Lane 2 Tower 1 (Block) =[46, 0, -46]
    Lane 2 Tower 2 (Block) =[18, 0, -18]
    Lane 3 Tower 1 (Block) =[80, 0, -32]
    Lane 3 Tower 2 (Block) =[80, 0, 45]

    Blue Player Base:

    Base cords(Block) =[-94, 1, 94]
    Lane 1 Minion spawn(Block) =[-80, 0, 68]
    Lane 2 Minion spawn(Block) =[-70, 0, 70]
    Lane 3 Minion spawn(Block) =[-68, 0, 80]
    Lane 1 Tower 1 (Block) =[-80, 0, 23]
    Lane 1 Tower 2 (Block) =[-80, 0, -45]
    Lane 2 Tower 1 (Block) =[-46, 0, 46]
    Lane 2 Tower 2 (Block) =[-18, 0, 18]
    Lane 3 Tower 1 (Block) =[-23, 0, 80]
    Lane 3 Tower 2 (Block) =[45, 0, 80]
    Lane 1 Goal 1 (Block) =[-80, 0, -62]
    Lane 3 Goal 1 (Block) =[62, 0, 80]

    */
    private HashMap<String, List<Minion>> mobs = new HashMap<>();

    /*
    towers will be in the format
    [(teamName)(LaneNumber)][numberInLane]

     */
    private HashMap<String, Tower[]> lanes = new HashMap<>();

    public void build_towers(){
        //need all Locations for towers that i will work out later
        Tower[] currentLane = new Tower[]{new Tower(plugin,this.GAME_ID,1,1,this.map_center.clone().add(new Vector(-80,0,23)),"Blue"),new Tower(plugin,this.GAME_ID,1,2,this.map_center.clone().add(new Vector(-80,0,-45)),"Blue")};
        lanes.put("BlueLane1",currentLane);
        currentLane = new Tower[]{new Tower(plugin,this.GAME_ID,2,1,this.map_center.clone().add(new Vector(-46,0,46)),"Blue"),new Tower(plugin,this.GAME_ID,2,2,this.map_center.clone().add(new Vector(-18,0,18)),"Blue")};

        lanes.put("BlueLane2",currentLane);

        currentLane = new Tower[]{ new Tower(plugin,this.GAME_ID,3,1,this.map_center.clone().add(new Vector(-23,0,80)),"Blue"),new Tower(plugin,this.GAME_ID,3,2,this.map_center.clone().add(new Vector(45,0,80)),"Blue")};
        lanes.put("BlueLane3",currentLane);
    /*
        Lane 1 Tower 1 (Block) =[32, 0, -80]
        Lane 1 Tower 2 (Block) =[-45, 0, -80]
        Lane 2 Tower 1 (Block) =[46, 0, -46]
        Lane 2 Tower 2 (Block) =[18, 0, -18]
        Lane 3 Tower 1 (Block) =[80, 0, -32]
        Lane 3 Tower 2 (Block) =[80, 0, 45] */
        currentLane = new Tower[]{new Tower(plugin,this.GAME_ID,1,1,this.map_center.clone().add(new Vector(32,0,-80)),"Red"),new Tower(plugin,this.GAME_ID,1,2,this.map_center.clone().add(new Vector(-45,0,-80)),"Red")};
        lanes.put("RedLane1",currentLane);
        currentLane = new Tower[]{ new Tower(plugin,this.GAME_ID,2,1,this.map_center.clone().add(new Vector(46,0,-46)),"Red"),new Tower(plugin,this.GAME_ID,2,2,this.map_center.clone().add(new Vector(18,0,-18)),"Red")};
        lanes.put("RedLane2",currentLane);
        currentLane = new Tower[]{ new Tower(plugin,this.GAME_ID,3,1,this.map_center.clone().add(new Vector(80,0,-32)),"Red"), new Tower(plugin,this.GAME_ID,3,2,this.map_center.clone().add(new Vector(80,0,45)),"Red")};
        lanes.put("RedLane3",currentLane);

    }
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

        for (String key : lanes.keySet()) {
            if (lanes.get(key) == null) {
                Bukkit.broadcast(Component.text("Initialization Error: " + key + " is null!"));
            } else {
                Bukkit.broadcast(Component.text(key + " initialized successfully."));
            }
        }
        //register my teams
        BlueTeam = scoreboard.registerNewTeam(game_id.toString()+"?Blue");
        RedTeam = scoreboard.registerNewTeam(game_id.toString()+"?Red");


    }

    public void clear_maps(){
        for(Tower[] lane : lanes.values()){
            for(Tower tower : lane){
                if(tower != null){
                    tower.destroy();
                }
            }

        }
        lanes = new HashMap<String,Tower[]>();
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


    public void spanw_minions(){
        Bukkit.broadcast(Component.text("Minions Where Spawned"));
    }
    public void respawn_player(Player player){
        if(BlueTeam.hasPlayer(player)){
            //respawn at blue base
            tp_blue_team(player);
        }else{
            tp_red_team(player);
        }
    }
    public void tp_blue_team(Player player){}
    public void tp_red_team(Player player){}
    public void Start_Game(Player[] BlueTeam, Player[] RedTeam){
        if(CURRENT_GAME_STATE != GameState.PreGame){
            return;
        }
  
        clear_maps();

        build_towers();

        for(Player player :BlueTeam){
            //setup nbt data
            if(player != null){
                this.BlueTeam.addPlayer(player);
                tp_blue_team(player);
            }
        }
        for(Player player :RedTeam){
            //setup nbt data
            if(player != null){
                this.RedTeam.addPlayer(player);
                tp_blue_team(player);

            }
        }
        CURRENT_GAME_STATE = GameState.InGame;
        GameStateChange change_event = new GameStateChange(GameState.PreGame,GameState.InGame,GAME_ID);
        change_event.callEvent();

        Bukkit.broadcast(Component.text("Game Started"));
        GAME_TIMER = Bukkit.getScheduler().runTaskTimer(plugin,this,0L,20L);
    }

    public void end_game(){

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
        }, RESPAWN_TIME*20);
        event.setCancelled(true);
    }

    @EventHandler
    public void onTowerDestroyed(TowerDestroyedEvent event){
        if(event.getTower().getGAME_ID()==this.GAME_ID){
            //tower is from this game
            Bukkit.broadcast(Component.text(event.getAttacker().getName() +" Has destroyed a tower"));
            event.getTower().destroy();
            this.lanes.get(event.getTower().getTOWER_TEAM()+event.getTower().getTOWER_LANE().toString())[event.getTower().getTOWER_NUMBER()] = null;
        }
    }

}
