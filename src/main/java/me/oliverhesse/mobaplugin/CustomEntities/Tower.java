package me.oliverhesse.mobaplugin.CustomEntities;

import me.oliverhesse.mobaplugin.GameEvents.TowerDestroyedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Wall;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Tower implements Listener {

    private String TOWER_TEAM;
    private Double TOWER_MAX_HEALTH;
    private Double Health;

    //data relevant to the game it is in
    private UUID GAME_ID;
    private Integer TOWER_LANE;
    private Integer TOWER_NUMBER;


    private final Plugin plugin;

    private  Location towerLocation;
    private final List<BlockDisplay> displayBlocks = new ArrayList<>();
    private final List<Entity> hurtBoxEntities = new ArrayList<>();

    public Tower(Plugin plugin){
        this.plugin = plugin;
    }

    public void destroy(){
        for(BlockDisplay displayBlock : displayBlocks){
            displayBlock.remove();
        }
        for(Entity hurtBox :hurtBoxEntities){
            hurtBox.remove();
        }
        displayBlocks.clear();
        hurtBoxEntities.clear();
    }

    public void damageTower(Double damage,Entity attacker){
        this.Health -= damage;
        if(this.Health <= 0){
            TowerDestroyedEvent newEvent = new TowerDestroyedEvent(this,attacker);
            newEvent.callEvent();
        }

    }
    public Double getHealth(){
        return this.Health;
    }
    public UUID getGAME_ID(){return this.GAME_ID;}
    public String getTOWER_TEAM(){return this.TOWER_TEAM;}
    public Integer getTOWER_LANE(){return this.TOWER_LANE;}
    public Integer getTOWER_NUMBER(){return this.TOWER_NUMBER;}
    @EventHandler
    public void EntityDamaged(EntityDamageByEntityEvent event){
        Entity target = event.getEntity();
        Entity damages = event.getDamager();
        PersistentDataContainer targetContainer = target.getPersistentDataContainer();
        PersistentDataContainer damagesContainer = damages.getPersistentDataContainer();
        if(!Objects.equals(targetContainer.get(new NamespacedKey(plugin, "GameTeam"), PersistentDataType.STRING), TOWER_TEAM)){
            //target is not one of this tower's blocks
            return;
        }
        if(Objects.equals(damagesContainer.get(new NamespacedKey(plugin,"GameTeam"),PersistentDataType.STRING),TOWER_TEAM)){
            //the attacker is of this towers team, so ignore
            event.setCancelled(true);
            return;
        }
        //target is of this tower
        //attacker is of another team
        damageTower(event.getDamage(),damages);
        event.setCancelled(true);

    }




    public void build(){

        //north is -z south +z west -x east +x
        //this code constructs me a tower
        Location blockLocation = this.towerLocation.clone();
        Location shulkerLocation = this.towerLocation.clone();
        shulkerLocation.setX(shulkerLocation.getX()+0.5f);
        shulkerLocation.setZ(shulkerLocation.getZ()+0.5f);


        //create stairs for base
        placeSlime(shulkerLocation,0,0,-2);
        addStair(blockLocation,0,0,-2,Stairs.Shape.STRAIGHT,BlockFace.SOUTH, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,0,0,4);
        addStair(blockLocation,0,0,4,Stairs.Shape.STRAIGHT,BlockFace.NORTH, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,2,0,-2);
        addStair(blockLocation,2,0,-2,Stairs.Shape.STRAIGHT,BlockFace.WEST, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,-4,0,0);
        addStair(blockLocation,-4,0,0,Stairs.Shape.STRAIGHT,BlockFace.EAST, Bisected.Half.BOTTOM);

        //reset location
        shulkerLocation.setX(shulkerLocation.getX()+2);
        blockLocation.setX(blockLocation.getX()+2);

        //create interlocking sections
        placeSlime(shulkerLocation,1,0,1);
        addStair(blockLocation,1,0,1,Stairs.Shape.INNER_RIGHT,BlockFace.WEST, Bisected.Half.BOTTOM);
        placeSlime(shulkerLocation,0,0,1);
        addStair(blockLocation,0,0,1,Stairs.Shape.OUTER_RIGHT,BlockFace.WEST, Bisected.Half.BOTTOM);
        placeSlime(shulkerLocation,1,0,-1);
        addStair(blockLocation,1,0,-1,Stairs.Shape.OUTER_RIGHT,BlockFace.WEST, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,-3,0,-2);
        addStair(blockLocation,-3,0,-2,Stairs.Shape.INNER_RIGHT,BlockFace.EAST, Bisected.Half.BOTTOM);
        placeSlime(shulkerLocation,-1,0,0);
        addStair(blockLocation,-1,0,0,Stairs.Shape.OUTER_RIGHT,BlockFace.EAST, Bisected.Half.BOTTOM);
        placeSlime(shulkerLocation,1,0,-1);
        addStair(blockLocation,1,0,-1,Stairs.Shape.OUTER_RIGHT,BlockFace.EAST, Bisected.Half.BOTTOM);


        placeSlime(shulkerLocation,2,0,1);
        addStair(blockLocation,2,0,1,Stairs.Shape.INNER_RIGHT,BlockFace.SOUTH, Bisected.Half.BOTTOM);
        placeSlime(shulkerLocation,1,0,0);
        addStair(blockLocation,1,0,0,Stairs.Shape.OUTER_RIGHT,BlockFace.SOUTH, Bisected.Half.BOTTOM);
        placeSlime(shulkerLocation,-1,0,-1);
        addStair(blockLocation,-1,0,-1,Stairs.Shape.OUTER_RIGHT,BlockFace.SOUTH, Bisected.Half.BOTTOM);


        placeSlime(shulkerLocation,-2,0,3);
        addStair(blockLocation,-2,0,3,Stairs.Shape.INNER_RIGHT,BlockFace.NORTH, Bisected.Half.BOTTOM);
        placeSlime(shulkerLocation,-1,0,0);
        addStair(blockLocation,-1,0,0,Stairs.Shape.OUTER_RIGHT,BlockFace.NORTH, Bisected.Half.BOTTOM);
        placeSlime(shulkerLocation,1,0,1);
        addStair(blockLocation,1,0,1,Stairs.Shape.OUTER_RIGHT,BlockFace.NORTH, Bisected.Half.BOTTOM);

        //reset location
        shulkerLocation.setX(shulkerLocation.getX()+1);
        blockLocation.setX(blockLocation.getX()+1);
        shulkerLocation.setZ(shulkerLocation.getZ()-2);
        blockLocation.setZ(blockLocation.getZ()-2);



        //place body layers
        for(int i = 0;i<7;i++){

            //create the base of the tower
            placeShulker(shulkerLocation,0,i,0);
            addBlock(blockLocation,0,i,0,Material.STONE_BRICKS);


            //add one block around it
            placeShulker(shulkerLocation,1,0,0);
            addBlock(blockLocation,1,0,0,Material.STONE_BRICKS);

            placeShulker(shulkerLocation,-2,0,0);
            addBlock(blockLocation,-2,0,0,Material.STONE_BRICKS);

            placeShulker(shulkerLocation,1,0,1);
            addBlock(blockLocation,1,0,1,Material.STONE_BRICKS);

            placeShulker(shulkerLocation,0,0,-2);
            addBlock(blockLocation,0,0,-2,Material.STONE_BRICKS);
            //reset location
            shulkerLocation.setZ(shulkerLocation.getZ()+1);
            blockLocation.setZ(blockLocation.getZ()+1);
            shulkerLocation.setY(shulkerLocation.getY()-i);
            blockLocation.setY(blockLocation.getY()-i);

        }
        for(int i=1;i<6;i++){
            placeShulker(shulkerLocation,1,i,1);
            addWall(blockLocation,1,i,1, new BlockFace[]{BlockFace.NORTH, BlockFace.WEST});

            placeShulker(shulkerLocation,-2,0,-2);
            addWall(blockLocation,-2,0,-2, new BlockFace[]{BlockFace.SOUTH, BlockFace.EAST});

            placeShulker(shulkerLocation,0,0,2);
            addWall(blockLocation,0,0,2 ,new BlockFace[]{BlockFace.NORTH, BlockFace.EAST});

            placeShulker(shulkerLocation,2,0,-2);
            addWall(blockLocation,2,0,-2,new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST});


            shulkerLocation.setZ(shulkerLocation.getZ()+1);
            blockLocation.setZ(blockLocation.getZ()+1);
            shulkerLocation.setX(shulkerLocation.getX()-1);
            blockLocation.setX(blockLocation.getX()-1);
            shulkerLocation.setY(shulkerLocation.getY()-i);
            blockLocation.setY(blockLocation.getY()-i);
        }
        shulkerLocation.setY(shulkerLocation.getY()+7);
        blockLocation.setY(blockLocation.getY()+7);
        for(int x =-2;x<3;x++){
            for(int z=-2;z<3;z++){
                placeShulker(shulkerLocation,x,0,z);
                addBlock(blockLocation,x,0,z,Material.STONE_BRICKS);

                shulkerLocation.setZ(shulkerLocation.getZ()-z);
                blockLocation.setZ(blockLocation.getZ()-z);

                shulkerLocation.setX(shulkerLocation.getX()-x);
                blockLocation.setX(blockLocation.getX()-x);
            }
        }

        //create stairs for top
        placeSlime(shulkerLocation,0,-1,-2);
        addStair(blockLocation,0,-1,-2,Stairs.Shape.STRAIGHT,BlockFace.SOUTH, Bisected.Half.TOP);

        placeSlime(shulkerLocation,0,0,4);
        addStair(blockLocation,0,0,4,Stairs.Shape.STRAIGHT,BlockFace.NORTH, Bisected.Half.TOP);

        placeSlime(shulkerLocation,2,0,-2);
        addStair(blockLocation,2,0,-2,Stairs.Shape.STRAIGHT,BlockFace.WEST, Bisected.Half.TOP);

        placeSlime(shulkerLocation,-4,0,0);
        addStair(blockLocation,-4,0,0,Stairs.Shape.STRAIGHT,BlockFace.EAST, Bisected.Half.TOP);

        //reset location
        shulkerLocation.setX(shulkerLocation.getX()+2);
        blockLocation.setX(blockLocation.getX()+2);

        //create interlocking sections
        placeSlime(shulkerLocation,1,0,1);
        addStair(blockLocation,1,0,1,Stairs.Shape.INNER_RIGHT,BlockFace.WEST, Bisected.Half.TOP);
        placeSlime(shulkerLocation,0,0,1);
        addStair(blockLocation,0,0,1,Stairs.Shape.OUTER_RIGHT,BlockFace.WEST, Bisected.Half.TOP);
        placeSlime(shulkerLocation,1,0,-1);
        addStair(blockLocation,1,0,-1,Stairs.Shape.OUTER_RIGHT,BlockFace.WEST, Bisected.Half.TOP);

        placeSlime(shulkerLocation,-3,0,-2);
        addStair(blockLocation,-3,0,-2,Stairs.Shape.INNER_RIGHT,BlockFace.EAST, Bisected.Half.TOP);
        placeSlime(shulkerLocation,-1,0,0);
        addStair(blockLocation,-1,0,0,Stairs.Shape.OUTER_RIGHT,BlockFace.EAST, Bisected.Half.TOP);
        placeSlime(shulkerLocation,1,0,-1);
        addStair(blockLocation,1,0,-1,Stairs.Shape.OUTER_RIGHT,BlockFace.EAST, Bisected.Half.TOP);


        placeSlime(shulkerLocation,2,0,1);
        addStair(blockLocation,2,0,1,Stairs.Shape.INNER_RIGHT,BlockFace.SOUTH, Bisected.Half.TOP);
        placeSlime(shulkerLocation,1,0,0);
        addStair(blockLocation,1,0,0,Stairs.Shape.OUTER_RIGHT,BlockFace.SOUTH, Bisected.Half.TOP);
        placeSlime(shulkerLocation,-1,0,-1);
        addStair(blockLocation,-1,0,-1,Stairs.Shape.OUTER_RIGHT,BlockFace.SOUTH, Bisected.Half.TOP);


        placeSlime(shulkerLocation,-2,0,3);
        addStair(blockLocation,-2,0,3,Stairs.Shape.INNER_RIGHT,BlockFace.NORTH, Bisected.Half.TOP);
        placeSlime(shulkerLocation,-1,0,0);
        addStair(blockLocation,-1,0,0,Stairs.Shape.OUTER_RIGHT,BlockFace.NORTH, Bisected.Half.TOP);
        placeSlime(shulkerLocation,1,0,1);
        addStair(blockLocation,1,0,1,Stairs.Shape.OUTER_RIGHT,BlockFace.NORTH, Bisected.Half.TOP);

        //reset location
        shulkerLocation.setX(shulkerLocation.getX()+1);
        blockLocation.setX(blockLocation.getX()+1);
        shulkerLocation.setZ(shulkerLocation.getZ()-2);
        blockLocation.setZ(blockLocation.getZ()-2);
        shulkerLocation.setY(shulkerLocation.getY()+2);
        blockLocation.setY(blockLocation.getY()+2);

        //build top piece
        placeShulker(shulkerLocation,0,0,0);
        addBlock(blockLocation,0,0,0,Material.STONE_BRICKS);

        placeSlime(shulkerLocation,0,1,0);
        addBlock(blockLocation,0,1,0,Material.STONE_BRICK_SLAB);

        placeSlime(shulkerLocation,2,-1,0);
        addBlock(blockLocation,2,-1,0,Material.STONE_BRICK_SLAB);

        placeSlime(shulkerLocation,-4,0,0);
        addBlock(blockLocation,-4,0,0,Material.STONE_BRICK_SLAB);

        placeSlime(shulkerLocation,2,0,2);
        addBlock(blockLocation,2,0,2,Material.STONE_BRICK_SLAB);

        placeSlime(shulkerLocation,0,0,-4);
        addBlock(blockLocation,0,0,-4,Material.STONE_BRICK_SLAB);

        //reset position
        shulkerLocation.setZ(shulkerLocation.getZ()+2);
        blockLocation.setZ(blockLocation.getZ()+2);

        //place stairs
        placeSlime(shulkerLocation,2,0,-2);
        addStair(blockLocation,2,0,-2,Stairs.Shape.INNER_RIGHT,BlockFace.NORTH, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,0,0,1);
        addStair(blockLocation,0,0,1,Stairs.Shape.STRAIGHT,BlockFace.EAST, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,-1,0,-1);
        addStair(blockLocation,-1,0,-1,Stairs.Shape.STRAIGHT,BlockFace.NORTH, Bisected.Half.BOTTOM);
        //reset position
        shulkerLocation.setZ(shulkerLocation.getZ()+2);
        blockLocation.setZ(blockLocation.getZ()+2);
        shulkerLocation.setX(shulkerLocation.getX()-1);
        blockLocation.setX(blockLocation.getX()-1);

        placeSlime(shulkerLocation,2,0,2);
        addStair(blockLocation,2,0,2,Stairs.Shape.INNER_RIGHT,BlockFace.EAST, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,0,0,-1);
        addStair(blockLocation,0,0,-1,Stairs.Shape.STRAIGHT,BlockFace.EAST, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,-1,0,1);
        addStair(blockLocation,-1,0,1,Stairs.Shape.STRAIGHT,BlockFace.SOUTH, Bisected.Half.BOTTOM);

        //reset position
        shulkerLocation.setZ(shulkerLocation.getZ()-2);
        blockLocation.setZ(blockLocation.getZ()-2);
        shulkerLocation.setX(shulkerLocation.getX()-1);
        blockLocation.setX(blockLocation.getX()-1);

        placeSlime(shulkerLocation,-2,0,-2);
        addStair(blockLocation,-2,0,-2,Stairs.Shape.INNER_RIGHT,BlockFace.WEST, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,0,0,1);
        addStair(blockLocation,0,0,1,Stairs.Shape.STRAIGHT,BlockFace.WEST, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,1,0,-1);
        addStair(blockLocation,1,0,-1,Stairs.Shape.STRAIGHT,BlockFace.NORTH, Bisected.Half.BOTTOM);

        //reset position
        shulkerLocation.setZ(shulkerLocation.getZ()+2);
        blockLocation.setZ(blockLocation.getZ()+2);
        shulkerLocation.setX(shulkerLocation.getX()+1);
        blockLocation.setX(blockLocation.getX()+1);

        placeSlime(shulkerLocation,-2,0,2);
        addStair(blockLocation,-2,0,2,Stairs.Shape.INNER_RIGHT,BlockFace.SOUTH, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,1,0,0);
        addStair(blockLocation,1,0,0,Stairs.Shape.STRAIGHT,BlockFace.SOUTH, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,-1,0,-1);
        addStair(blockLocation,-1,0,-1,Stairs.Shape.STRAIGHT,BlockFace.WEST, Bisected.Half.BOTTOM);

        //reset position
        shulkerLocation.setZ(shulkerLocation.getZ()-1);
        blockLocation.setZ(blockLocation.getZ()-1);
        shulkerLocation.setX(shulkerLocation.getX()+2);
        blockLocation.setX(blockLocation.getX()+2);

        //place stairs around center block
        placeSlime(shulkerLocation,0,0,1);
        addStair(blockLocation,0,0,1,Stairs.Shape.STRAIGHT,BlockFace.NORTH, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,0,0,-2);
        addStair(blockLocation,0,0,-2,Stairs.Shape.STRAIGHT,BlockFace.SOUTH, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,1,0,1);
        addStair(blockLocation,1,0,1,Stairs.Shape.STRAIGHT,BlockFace.WEST, Bisected.Half.BOTTOM);

        placeSlime(shulkerLocation,-2,0,0);
        addStair(blockLocation,-2,0,0,Stairs.Shape.STRAIGHT,BlockFace.EAST, Bisected.Half.BOTTOM);



    }

    public void placeShulker(Location location,float XChange,float YChange,float ZChange){
        location.setZ(location.getZ()+ZChange);
        location.setX(location.getX()+XChange);
        location.setY(location.getY()+YChange);

        Shulker newShulker = (Shulker) location.getWorld().spawnEntity(location, EntityType.SHULKER);

        newShulker.setAI(false);
        newShulker.setCollidable(true);
        newShulker.setInvisible(true);
        this.hurtBoxEntities.add(newShulker);
    }
    public void addBlock(Location location, float XChange, float YChange, float ZChange, Material type){
        location.setZ(location.getZ()+ZChange);
        location.setX(location.getX()+XChange);
        location.setY(location.getY()+YChange);
        BlockDisplay newBlock = location.getWorld().spawn(location,BlockDisplay.class);
        newBlock.setBlock(Bukkit.createBlockData(type));
        this.displayBlocks.add(newBlock);
    }
    public void addWall(Location location, float XChange, float YChange, float ZChange, BlockFace[] direction){
        location.setZ(location.getZ()+ZChange);
        location.setX(location.getX()+XChange);
        location.setY(location.getY()+YChange);
        BlockDisplay newBlock = location.getWorld().spawn(location,BlockDisplay.class);

        Wall wallMeta = (Wall) Bukkit.createBlockData(Material.STONE_BRICK_WALL);
        wallMeta.setHeight(direction[0],Wall.Height.TALL);
        wallMeta.setHeight(direction[1],Wall.Height.TALL);
        newBlock.setBlock(wallMeta);
        this.displayBlocks.add(newBlock);
    }
    public void placeSlime(Location location,float XChange,float YChange,float ZChange){
        location.setZ(location.getZ()+ZChange);
        location.setX(location.getX()+XChange);
        location.setY(location.getY()+YChange);

        Slime newSlime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);

        newSlime.setAI(false);
        newSlime.setCollidable(true);
        newSlime.setInvisible(true);
        newSlime.setSize(2);
        this.hurtBoxEntities.add(newSlime);

    }
    public void addStair(Location location, float XChange, float YChange, float ZChange, Stairs.Shape shape, BlockFace direction, Bisected.Half half){
        //stairs are slimes cus fuck this game and their damn shulker display logic
        location.setZ(location.getZ()+ZChange);
        location.setX(location.getX()+XChange);
        location.setY(location.getY()+YChange);

        BlockDisplay newStair3 = location.getWorld().spawn(location,BlockDisplay.class);
        Stairs newStairs = (Stairs) Material.STONE_BRICK_STAIRS.createBlockData();
        newStairs.setShape(shape);
        newStairs.setFacing(direction);
        newStairs.setHalf(half);
        newStair3.setBlock(newStairs);
        this.displayBlocks.add(newStair3);
    }

}
