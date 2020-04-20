package switorik.randomtp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;



public class tabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        Main plugin = Main.plugin;
        List<String> output = new ArrayList<>();

        switch (args.length) {
            case 0: {
                output.add("reset");
                output.add("confirm");
                return output;
            }
            case 1: {
                if ("confirm".startsWith(args[0]) && sender.hasPermission("randomtp.use")) { output.add("confirm"); }
                if ("reset".startsWith(args[0]) && sender.hasPermission("randomtp.reset")) { output.add("reset"); }
                return output;
            }
            case 2: {
                if(args[0].equals("reset") && sender.hasPermission("randomtp.reset")) {

                    for(Player p : plugin.getServer().getOnlinePlayers()) {
                        if (p.getName().startsWith(args[1])) { output.add(p.getName()); }
                    }
                    return output;

                }

            }
        }
        return output;
    }
}
