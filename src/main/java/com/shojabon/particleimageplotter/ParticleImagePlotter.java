package com.shojabon.particleimageplotter;

import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ParticleImagePlotter extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("draw")){
            File imageFile = new File(getDataFolder() + File.separator + "chicken.png");
            Location loc = ((Player) sender).getLocation();
            loc.setX(500);
            loc.setY(120);
            loc.setZ(-1200);
            new BukkitRunnable(){

                @Override
                public void run() {
                    drawImage(imageFile, 0.2, 3, loc);
                }
            }.runTaskTimerAsynchronously(this, 10, 10);
        }
        return false;
    }

    public void playParticle(Location l, int r, int g, int b){
        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            p.spawnParticle(Particle.REDSTONE, l.getX(), l.getY(), l.getZ(), 0, r/255D, g/255D, b/255D,1);
        }
    }


    public void drawImage(File file, double quality, int size, Location l){
        new Thread(() -> {
            try {
                BufferedImage image = ImageIO.read(file);
                double xCenterMargin = image.getWidth()/2D;
                double yCenterMargin = image.getHeight()/2D;

                for(int i =0; i < image.getWidth()*quality; i++){
                    for(int ii =0; ii < image.getHeight()*quality; ii++){
                        Location lock = l.clone();
                        int getX = (int) (i/quality);
                        int getY = (int) (ii/quality);
                        int col = image.getRGB(getX, getY);
                        if(col != 0){
                            Color color = new Color(col);
                            playParticle(lock.add(((double)i/(image.getWidth() - xCenterMargin)*size/quality), ((double)ii/(image.getHeight()-yCenterMargin)*size/quality), 0), color.getRed(), color.getGreen(), color.getBlue());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
