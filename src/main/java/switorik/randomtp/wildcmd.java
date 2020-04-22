package switorik.randomtp;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

//reloadcommand
//language file

public class wildcmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Main p = Main.plugin;
        YamlConfiguration message = Main.message;

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

                            player.sendMessage(message.getString("times"));
                            return true;

                        }

                        if(worlds.contains(player.getWorld().getName())) {

                            WorldBorder wb = player.getWorld().getWorldBorder();
                            int wbSize = (int) wb.getSize() / 2;
                            Location loc = wb.getCenter();

                            Random num = new Random();
                            Block b;
                            Location dest;
                            boolean waterlogged;
                            List<String> badBlocks = p.getConfig().getStringList("disallowedblocks");

                            do {
                                double x = num.nextInt(wbSize);
                                double z = num.nextInt(wbSize);
                                int negX = num.nextInt(2);
                                int negZ = num.nextInt(2);


                                if (negX == 1) {

                                    x *= -1;

                                }

                                if (negZ == 1) {

                                    z *= -1;

                                }

                                dest = new Location(player.getWorld(),
                                        loc.getX() + x,
                                        255,
                                        //player.getWorld().getHighestBlockYAt(loc.getBlockX() + (int) x, loc.getBlockZ() + (int) z),
                                        loc.getZ() + z);

                                    while(dest.getBlock().getType().equals(Material.AIR)) {

                                        dest.setY(dest.getY() - 1);

                                    }

                                b = dest.getBlock();

                                    if(p.getConfig().getBoolean("allowwaterloggedblocks") == false) {

                                        waterlogged = b.getBlockData() instanceof Waterlogged;

                                } else {

                                        waterlogged = false;

                                    }

                            } while (badBlocks.contains(b.getType().toString()) || waterlogged);

                            player.teleport(dest.add(0, 1.5, 0));
                            player.sendMessage(message.getString("teleport").replace("%n", "\n"));
                            //player.sendMessage(colorize("&eYou can set your home with &f/sethome&e."));
                            yml.set("times", yml.getInt("times") + 1);
                            yml.set("location", dest);
                            try {
                                yml.save(ymlFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {

                            player.sendMessage(message.getString("world"));

                        }

                    } else {

                        player.sendMessage(message.getString("noperm"));

                    }

                } else {

                    if(args[0].equalsIgnoreCase("reset")) {

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

                                    player.sendMessage(message.getString("reset").replace("%p", args[1]));

                                } else {

                                    player.sendMessage(message.getString("noplayer"));

                                }

                            } else {

                                player.sendMessage(message.getString("addplayer"));

                            }

                        } else {

                            player.sendMessage(message.getString("noperm"));

                        }

                    } else {

                        if(args[0].equalsIgnoreCase( "reload")) {

                            if(player.hasPermission("randomtp.reload")) {

                                p.reloadConfig();
                                player.sendMessage(message.getString("reload"));

                            } else {

                                player.sendMessage(message.getString("noperm"));

                            }

                        }

                    }

                }

            } else {

                //if no arguments exist, add warning message saying /wild confirm to run command.
                player.sendMessage(message.getString("info").replace("%n", "\n"));
                //player.sendMessage(colorize("&eType /wild confirm to teleport"));

            }





        } else {

            sender.sendMessage(message.getString("ingame"));

        }
        return true;
    }


}
