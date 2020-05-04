package switorik.randomtp;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class wildcmd implements CommandExecutor {

    Main plugin = Main.plugin;


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        YamlConfiguration message = Main.message;

        if(sender instanceof Player) {

            switch(args.length) {
                case 1: {
                    if(args[0].equalsIgnoreCase("confirm")) confirm((Player) sender);
                    else if(args[0].equalsIgnoreCase("reload")) reload((Player) sender);
                    else if(args[0].equalsIgnoreCase("reset")) reset((Player) sender, args);
                    else if(args[0].equalsIgnoreCase("group")) group((Player) sender, args);
                    else if(args[0].equalsIgnoreCase("cancel")) groupCancel((Player) sender, args);
                    else sender.sendMessage(Objects.requireNonNull(message.getString("info")).replace("%n", "\n"));
                    return true;
                }
                default: {
                    if(sender.hasPermission("randomtp.use"))
                        sender.sendMessage(Objects.requireNonNull(message.getString("info")).replace("%n", "\n"));
                    else sender.sendMessage(Objects.requireNonNull(message.getString("noperm")));
                    return true;
                }

            }

        } else {

            sender.sendMessage(Objects.requireNonNull(message.getString("ingame")));
            return true;

        }

     }

    private void confirm(Player player) {
        //run the random teleport if available

        YamlConfiguration message = Main.message;
        if(player.hasPermission("randomtp.use")) {

            int times = plugin.getConfig().getInt("times"); //the amount of times a player can use /wild

            File ymlFile = new File(plugin.getDataFolder() + File.separator + "data" + File.separator +  "data.yml");
            YamlConfiguration yml = new YamlConfiguration().loadConfiguration(ymlFile);

            if (yml.getInt(player.getUniqueId().toString()) < times || times == -1) {

                if (plugin.getConfig().getStringList("allowedworlds").contains(player.getWorld().getName())) {

                    Location dest = randomTP(player.getWorld());

                    player.teleport(dest.add(0, 1.5, 0));
                    player.sendMessage(message.getString("teleport").replace("%n", "\n"));
                    yml.set(player.getUniqueId().toString(), yml.getInt(player.getUniqueId().toString()) + 1);
                    //yml.set("location", dest); unnecessary extra data
                    try {
                        yml.save(ymlFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    player.sendMessage(Objects.requireNonNull(message.getString("world")));

                }

            } else {

                player.sendMessage(Objects.requireNonNull(message.getString("times")));

            }


        } else {

            player.sendMessage(Objects.requireNonNull(message.getString("noperm")));

        }

    }

    private void reload(Player player) {
        //reload config and messages
        YamlConfiguration message = Main.message;
            if(player.hasPermission("randomtp.reload")) {

                plugin.reloadConfig();
                plugin.reloadMessages();
                player.sendMessage(Objects.requireNonNull(message.getString("reload")));

            } else {

                player.sendMessage(Objects.requireNonNull(message.getString("noperm")));

            }

    }

    private void reset(Player player, String[] args) {
        //resets a players random teleport uses
        YamlConfiguration message = Main.message;
        if(player.hasPermission("randomtp.reset")) {

            if(args.length > 1) {

                if(Bukkit.getServer().getPlayer(args[1]) instanceof Player) {

                    File ymlFile = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + "data.yml");
                    YamlConfiguration yml = new YamlConfiguration().loadConfiguration(ymlFile);

                    yml.set(Bukkit.getServer().getPlayer(args[1]).getUniqueId().toString(), 0);
                    try {
                        yml.save(ymlFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    player.sendMessage(Objects.requireNonNull(message.getString("reset")).replace("%p", args[1]));

                } else {

                    player.sendMessage(Objects.requireNonNull(message.getString("noplayer")));

                }

            } else {

                player.sendMessage(Objects.requireNonNull(message.getString("addplayer")));

            }

        } else {

            player.sendMessage(Objects.requireNonNull(message.getString("noperm")));

        }

    }
    private void groupCancel(Player player, String[] args) {

        if (player.hasPermission("randomtp.use")) {

            File ymlFile = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + "data.yml");
            YamlConfiguration yml = new YamlConfiguration().loadConfiguration(ymlFile);
            boolean pending = yml.getBoolean("pending." + player.getUniqueId());
            if (pending) {

                yml.set("cancelled." + player.getUniqueId(), true);
                player.sendMessage("You cancelled");//TODO: message

                try {
                    yml.save(ymlFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void group(Player player, String[] args) {

        YamlConfiguration message = Main.message;
        if(player.hasPermission("randomtp.use")) {

            player.setWalkSpeed(0); //make it so the player can not move.
            //player.sendMessage(Objects.requireNonNull(message.getString("group")));

            File ymlFile = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + "data.yml");
            YamlConfiguration yml = new YamlConfiguration().loadConfiguration(ymlFile);
            boolean pending = yml.getBoolean("pending." + player.getUniqueId());
            if(pending != true) {

                yml.set("pending." + player.getUniqueId(), true);

                int times = plugin.getConfig().getInt("times"); //the amount of times a player can use /wild

                if(yml.getInt(player.getUniqueId().toString()) < times || times == -1) {

                    Location dest = randomTP(player.getWorld());
                    player.sendMessage(message.getString("group"));

                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                            plugin, () ->{

                                File taskymlFile = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + "data.yml");
                                YamlConfiguration taskyml = new YamlConfiguration().loadConfiguration(taskymlFile);

                                if(!taskyml.getBoolean("cancelled." + player.getUniqueId())) {

                                    Bukkit.getLogger().info(taskyml.getBoolean("cancelled." + player.getUniqueId()) + "");

                                    player.teleport(dest.add(0, 1.5, 0));
                                    player.sendMessage(message.getString("teleport").replace("%n", "\n"));
                                    taskyml.set(player.getUniqueId().toString(), taskyml.getInt(player.getUniqueId().toString()) + 1);

                                    for(Entity e : player.getNearbyEntities(2, 2, 2)) {

                                        if(e instanceof Player) {

                                            Player p = (Player) e;
                                            if(taskyml.getInt(p.getUniqueId().toString()) < times || times == -1) {


                                                p.sendMessage(message.getString("teleport").replace("%n", "\n"));
                                                taskyml.set(p.getUniqueId().toString(), yml.getInt(p.getUniqueId().toString()) + 1);

                                            }

                                        }

                                    }



                                }

                                taskyml.set("pending." + player.getUniqueId(), null);
                                taskyml.set("cancelled." + player.getUniqueId(), null);
                                player.setWalkSpeed((float) .2);

                                try {
                                    taskyml.save(ymlFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }, 100);



                    try {
                        yml.save(ymlFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } else {

                player.sendMessage("You have a pending teleport");//TODO:messages

            }



        } else {

            player.sendMessage(Objects.requireNonNull(message.getString("noperm")));

        }

    }

    public Location randomTP(World world) {

        WorldBorder wb = world.getWorldBorder();
        int wbSize = (int) wb.getSize() / 2;
        Location loc = wb.getCenter();

        Random num = new Random();
        Block b;
        Location dest;
        boolean waterlogged;
        boolean voidBlock = false;
        List<String> badBlocks = plugin.getConfig().getStringList("disallowedblocks");

        do {
            double x = num.nextInt(wbSize);
            double z = num.nextInt(wbSize);
            int negX = num.nextInt(2);
            int negZ = num.nextInt(2);


            if (negX == 1) x *= -1;
            if (negZ == 1) z *= -1;

            dest = new Location(world,
                    loc.getX() + x,
                    255, //using this because getHighestBlockatY does not work as intended. Players can spawn inside structures or trees.
                    //player.getWorld().getHighestBlockYAt(loc.getBlockX() + (int) x, loc.getBlockZ() + (int) z),
                    loc.getZ() + z);


            while (dest.getBlock().getType().equals(Material.AIR)) {
                //getting highest block by going from top Y down
                if(dest.getY() > 0) dest.setY(dest.getY() - 1);
                else {
                    voidBlock = true;
                    break;
                }


            }

            b = dest.getBlock();

            if (!plugin.getConfig().getBoolean("allowwaterloggedblocks")) waterlogged = b.getBlockData() instanceof Waterlogged;
            else waterlogged = false;

        } while (badBlocks.contains(b.getType().toString()) || waterlogged || voidBlock);

        return dest;
    }

    public static String[] trim(String[] args){
        return Arrays.copyOfRange(args, 1, args.length);
    }

}
