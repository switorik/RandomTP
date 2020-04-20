package switorik.randtp;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class wildcmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Main p = Main.plugin;

        if(sender instanceof Player) {

            Player player = (Player) sender;

            if(args.length > 0) {

                if(args[0].equalsIgnoreCase("confirm")) {


                    if(player.hasPermission("randomtp.use")) {

                        //get allowed worlds in config
                        //get allowed teleport times in config
                        List<String> worlds = p.getConfig().getStringList("allowedworlds");
                        int times = p.getConfig().getInt("times");

                        File ymlFile = new File(p.getDataFolder() + File.separator + "data" + File.separator + player.getUniqueId() + ".yml");
                        YamlConfiguration yml = new YamlConfiguration().loadConfiguration(ymlFile);

                        if(yml.getInt("times") >= times && times != -1) {

                            player.sendMessage(colorize("&4You can not teleport anymore."));
                            return true;

                        }

                        if(worlds.contains(player.getWorld().getName())) {

                            WorldBorder wb = player.getWorld().getWorldBorder();
                            int wbSize = (int) wb.getSize() / 2;
                            Location loc = wb.getCenter();

                            Random num = new Random();
                            Block b;
                            Location dest;
                            do {
                                double x = num.nextInt(wbSize);
                                double z = num.nextInt(wbSize);
                                int negX = num.nextInt(1);
                                int negZ = num.nextInt(1);

                                if (negX == 1) {

                                    x *= -1;
                                    x += .5;

                                } else {
                                    x -= .5;
                                }

                                if (negZ == 1) {

                                    z *= -1;
                                    z += .5;

                                } else {
                                    z -= .5;
                                }


                                dest = new Location(player.getWorld(),
                                        loc.getX() + x,
                                        player.getWorld().getHighestBlockYAt(loc.getBlockX() + (int) x, loc.getBlockZ() + (int) z),
                                        loc.getZ() + z);

                                //Location check = dest.subtract(0,1,0);
                                b = dest.getBlock();
                            } while (b.getType() == Material.WATER || b.getType() == Material.LAVA);

                            player.teleport(dest.add(0, 1, 0));
                            player.sendMessage(colorize("&eYou have teleported to a random location."));
                            player.sendMessage(colorize("&eYou can set your home with &f/sethome&e."));
                            yml.set("times", yml.getInt("times") + 1);
                            yml.set("location", dest);
                            try {
                                yml.save(ymlFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {

                            player.sendMessage(colorize("&4You can not use that in this world"));

                        }

                    } else {

                        player.sendMessage(colorize("&4You do not have permission to use this command."));

                    }

                } else {

                    if(args[0].equals("reset")) {

                        //sets a players random teleport uses to 0
                        if(player.hasPermission("randomtp.reset")) {

                            if(args.length > 1) {

                                if(Bukkit.getServer().getPlayer(args[1]) instanceof Player) {

                                    File ymlFile = new File(p.getDataFolder() + File.separator + "data" + File.separator + Bukkit.getServer().getPlayer(args[1]).getUniqueId() + ".yml");
                                    YamlConfiguration yml = new YamlConfiguration().loadConfiguration(ymlFile);

                                    yml.set("times", 0);
                                    try {
                                        yml.save(ymlFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    player.sendMessage(colorize("&eYou have reset &f" + args[1] + "&es random teleport."));

                                } else {

                                    player.sendMessage(colorize("&4A player by that name does not exist."));

                                }

                            } else {

                                player.sendMessage(colorize("&4You must add a player name to reset."));

                            }

                        } else {

                            player.sendMessage(colorize("&4You do not have permission to use this command."));

                        }

                    }

                }

            } else {

                //if no arguments exist, add warning message saying /wild confirm to run command.
                player.sendMessage(colorize("&eThis will teleport you to a random destination within the world border."));
                player.sendMessage(colorize("&eType /wild confirm to teleport"));

            }





        } else {

            sender.sendMessage("You must be in game to run this command.");

        }
        return true;
    }

    public String colorize(String msg) {

        return ChatColor.translateAlternateColorCodes('&', msg);

    }

}
