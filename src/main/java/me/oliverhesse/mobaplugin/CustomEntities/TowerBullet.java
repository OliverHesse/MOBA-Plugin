package me.oliverhesse.mobaplugin.CustomEntities;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;

public class TowerBullet implements Runnable{

    private BukkitTask BULLET_TASK;
    private Location current_location;
    private Entity target;
    private final double BULLET_SPEED = 0.6f;
    private final double BULLET_DAMAGE = 30;
    private final int NUMBER_OF_PARTICLES = 100;
    private final double MAX_RADIUS = 0.2;

    public TowerBullet(Plugin plugin, Location location, Entity target){
        this.current_location = location;
        this.target = target;
        BULLET_TASK = Bukkit.getScheduler().runTaskTimer(plugin,this,0L,1);
    }

    @Override
    public void run() {
        move_center();
        draw_particles();
        check_collision();
    }
    private void check_collision(){
        if(target.getBoundingBox().contains(current_location.toVector())){
            //damage target
            if(target instanceof LivingEntity victim){
                victim.damage(BULLET_DAMAGE);
            }

            BULLET_TASK.cancel();
        }
    }
    private void move_center(){
        Vector direction = target.getLocation().subtract(current_location).toVector();
        direction.normalize();
        direction.multiply(BULLET_SPEED);
        current_location.add(direction);
    }
    private void draw_particles(){
        for(int i =0; i<NUMBER_OF_PARTICLES;i+=1){
            Location particle_location = current_location.clone();
            particle_location.setYaw(0f);
            particle_location.setPitch(0f);
            double minValue = -MAX_RADIUS, maxValue=MAX_RADIUS;
            Random theRandom = new Random();
            double randY = minValue + (maxValue - minValue) * theRandom.nextDouble();
            theRandom.nextDouble();
            double randX = minValue + (maxValue - minValue) * theRandom.nextDouble();
            theRandom.nextDouble();
            double randZ = minValue + (maxValue - minValue) * theRandom.nextDouble();

            particle_location.add(randX,randY,randZ);
            Particle.DustOptions dust = new Particle.DustOptions(Color.BLUE,0.6f);
            particle_location.getWorld().spawnParticle(Particle.REDSTONE,particle_location,0,dust);
        }

    }
}
