package Banished.blockynights;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;




public class main extends JavaPlugin implements Listener {
	
	private Map<Player, Location> tppvp = new HashMap<Player, Location>();
	private Map<Player, Boolean> pvpproced = new HashMap<Player, Boolean>();
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
	}
	
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((cmd.getName().equalsIgnoreCase("continue") && sender instanceof Player) && (args.length == 0)) {
			Player player = (Player) sender;
			sender.sendMessage("wee lets go!");
			if (tppvp.get(player) != null) {
				pvpproced.put(player, true);
				player.teleport(tppvp.get(player));
				tppvp.remove(player);
				return true;
			}
		}
		if (sender.hasPermission("banished.admin")) {
			if ((cmd.getName().equalsIgnoreCase("banish") && sender instanceof Player) && (args.length == 0)) {
				sender.sendMessage("§e» §3Command is: §b/Banish <Town> <Playername> §e«");
			}
			if ((cmd.getName().equalsIgnoreCase("banish") && sender instanceof Player) && (args.length == 1)) {
				sender.sendMessage("§e» §3Command is: §b/Banish <Town> <Playername> §e«");
			}
			if ((cmd.getName().equalsIgnoreCase("banish") && sender instanceof Player) && (args.length == 2)) {
				adminBanish(args[0],args[1]);
				sender.sendMessage("§e» §3Banished §b"+args[1]+"§3 from §b"+args[0]+" §e«");
			}
			// unbanish
			if ((cmd.getName().equalsIgnoreCase("unbanish") && sender instanceof Player) && (args.length == 0)) {
				sender.sendMessage("§e» §3Command is: §b/Unbanish <Town> <Playername> §e«");
			}
			if ((cmd.getName().equalsIgnoreCase("unbanish") && sender instanceof Player) && (args.length == 1)) {
				sender.sendMessage("§e» §3Command is: §b/Unbanish <Town> <Playername> §e«");
			}
			if ((cmd.getName().equalsIgnoreCase("unbanish") && sender instanceof Player) && (args.length == 2)) {
				adminUnbanish(args[0],args[1]);
				sender.sendMessage("§e» §3Unbanished §b"+args[1]+"§3 from §b"+args[0]+" §e«");
			}
			// banished
			if ((cmd.getName().equalsIgnoreCase("banished") && sender instanceof Player) && (args.length == 0)) {
				sender.sendMessage("§e» §3Command is: §b/Banished <Town> §e«");
			}
			if ((cmd.getName().equalsIgnoreCase("banished") && sender instanceof Player) && (args.length == 1)) {
				Player p = (Player) sender;
				adminBanished(p,args[0]);
			}
			return true;
		}
        Resident resident = null;
        try {
        resident = TownyUniverse.getDataSource().getResident(sender.getName());
        resident.isMayor();
        } catch (NotRegisteredException e) {
        //do whatever here
        }finally {
                if(resident == null){
                        System.out.print("Sender is not a resident");
                        return true;
                }else{
                       try {
                    	if (resident.isMayor()) {   
							final String town = resident.getTown().toString();
							if ((cmd.getName().equalsIgnoreCase("banished") && sender instanceof Player) && (args.length == 0)) {
								Player player = (Player) sender;
								getBanishedList(player,town);
							}
							if ((cmd.getName().equalsIgnoreCase("banish") && sender instanceof Player) && (args.length == 0)) {
								sender.sendMessage("§e» §3Command is: §b/Banish <Playername> §e«");
							}
							if ((cmd.getName().equalsIgnoreCase("banish") && sender instanceof Player) && (args.length == 1)) {
								if (Bukkit.getServer().getPlayer(args[0]) != null) {
									addBanishedPlayer(Bukkit.getServer().getPlayer(args[0]).getDisplayName(),town);
								    sender.sendMessage("§e»§3 Banishing §b"+args[0]+" §3From your town. §e«"); 
								} else {sender.sendMessage("§e»§3 "+args[0]+"§b Needs to be online before you can banish him/her.§e«"); }
							}
							if ((cmd.getName().equalsIgnoreCase("unbanish") && sender instanceof Player) && (args.length == 0)) {
								sender.sendMessage("§e» §3Command is: §b/Unbanish <Playername> §e«");
							}
							if ((cmd.getName().equalsIgnoreCase("unbanish") && sender instanceof Player) && (args.length == 1)) {
								String name = args[0];
								if (this.getConfig().getString("Banished."+town+"."+args[0]) == null) { return true; }
								if (this.getConfig().getString("Banished."+town+"."+args[0]).equalsIgnoreCase(name)) { 
									removeBanishedPlayer(args[0],town);
								    sender.sendMessage("§e»§3 Unbanishing §b"+args[0]+" §3From your town §e«"); 
								    if (Bukkit.getServer().getPlayer(args[0]) != null) { Bukkit.getServer().getPlayer(args[0]).sendMessage("§e»§3 You have been Unbanished from the town of§b "+town+"  §e«"); }
								} else {sender.sendMessage("§e»§3 "+args[0]+"§b Is not banished from your town. §e«"); }
							}
                    	} else { sender.sendMessage("§e» §3You need to be a Mayor in a town to use this command. §e«"); }
					} catch (NotRegisteredException e) {
						//
						sender.sendMessage("§e» §3You need to be a Mayor in a town to use this command. §e«");
						return true;
					}
                }
        }
		return true;
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Location loc = event.getTo();
		String name = event.getPlayer().getDisplayName();
		String townname = TownyUniverse.getTownName(loc);
		String townfrom = TownyUniverse.getTownName(event.getFrom());
		String townto = TownyUniverse.getTownName(event.getTo());
		Resident resident;
		String residenttown = null;
		try {
				resident = TownyUniverse.getDataSource().getResident(event.getPlayer().getName());
				residenttown = resident.getTown().toString();
			} catch (NotRegisteredException e1) {
			}
		if (townfrom != townto && townto != residenttown) {
			if (townname != null && pvpproced.get(event.getPlayer()) == null) {
				Town town;
				Boolean townpvp = false;
				try {
					 town = TownyUniverse.getDataSource().getTown(townname);
						townpvp = town.isPVP();
					} catch (NotRegisteredException e) {
						e.printStackTrace();
					}
					TownBlock townblock = TownyUniverse.getTownBlock(event.getTo());
					Boolean pvp = townblock.getPermissions().toString().contains("pvp");
					if (pvp || townpvp) { 
						event.setCancelled(true);
						event.getPlayer().teleport(event.getFrom());
						tppvp.put(event.getPlayer(), event.getTo());
						event.getPlayer().sendMessage("§e» §4WARNING§3 You are about to teleport into a pvp area, proceed with §b/continue §e«");
					}
			}
		}
		if (pvpproced.get(event.getPlayer()) != null) { pvpproced.remove(event.getPlayer()); }
		if (event.getPlayer().hasPermission("banished.ignore")) { return; }
		if (this.getConfig().getString("Banished."+townname+"."+event.getPlayer().getDisplayName()) == null) { return; }
		if (this.getConfig().getString("Banished."+townname+"."+event.getPlayer().getDisplayName()).equalsIgnoreCase(name)) { 
			event.setCancelled(true);
			event.getPlayer().sendMessage("§e» §3You are Banished from the town of:§b "+townname+"§3 You are unable to enter their area. §e«");
		}
	}

	@EventHandler
	public void PlayerChangePlotEvent(com.palmergames.bukkit.towny.event.PlayerChangePlotEvent event) {
		int x = event.getTo().getX()*16;
		int y = 64;
		int z = event.getTo().getZ()*16;
		Location loc = new Location(event.getPlayer().getWorld(),x,y,z);
		String townname = TownyUniverse.getTownName(loc);
		String name = event.getPlayer().getDisplayName();
		if (event.getPlayer().hasPermission("banished.ignore")) { return; }
		if (this.getConfig().getString("Banished."+townname+"."+event.getPlayer().getDisplayName()) == null) { return; }
		if (this.getConfig().getString("Banished."+townname+"."+event.getPlayer().getDisplayName()).equalsIgnoreCase(name)) {  
			int x1 = event.getFrom().getX()*16;
			int z1 = event.getFrom().getZ()*16;
			int y1 = findChunkY(x1,z1,event.getPlayer().getWorld());
			Location oldloc = new Location(event.getPlayer().getWorld(),x1+8,y1,z1+8);
			event.getPlayer().teleport(oldloc);
			event.getPlayer().sendMessage("§e» §3You are Banished from the town of:§b "+townname+"§3 You are unable to enter their area. §e«");
		}
	}
	
	public int findChunkY(int x,int z, World world) {
		int y = 60;
		while (y < 255) {
			Location location = new Location(world,x,y,z);
			Block block1 = location.getBlock();
			Block block2 = location.add(0, 1, 0).getBlock();
			Block block3 = location.add(0, 2, 0).getBlock();
			if (block1.getType() != Material.AIR && block2.getType() == Material.AIR && block3.getType() == Material.AIR) { return y+1; }
			y++;
		}
		return y;
	}
	
	private void addBanishedPlayer(String name,String town) {
		   this.getConfig().set("Banished."+town+"."+name, name);
		   this.saveConfig();
	}
	
	private void removeBanishedPlayer(String name,String town) {
		   this.getConfig().set("Banished."+town+"."+name, "unbanished");
		   this.saveConfig();
	}
	
	private void adminBanish(String town,String name) {
		   this.getConfig().set("Banished."+town+"."+name, name);
		   this.saveConfig();
	}
	private void adminUnbanish(String town,String name) {
		   this.getConfig().set("Banished."+town+"."+name, "unbanished");
		   this.saveConfig();
	}
	private void adminBanished(Player player,String town) {
		player.sendMessage("§e» §3Banish list for the town of§b "+town+"  §e«");
		player.sendMessage("");
		ConfigurationSection users = getConfig().getConfigurationSection("Banished."+town);
		if(users == null)
		{
		    player.sendMessage("no one banished");
		    return;
		}
		Set<String> players = users.getKeys(false);
		for(String p : players)
		{
		    if(!users.getString(p).equalsIgnoreCase("unbanished")) { player.sendMessage("§3-§b "+users.getString(p)); }
		    // etc
		}
		player.sendMessage("§e» §3--------------------------- §e«");
	}
	
	private void getBanishedList(Player player, String town) {
		player.sendMessage("§e» §3Banish list for the town of§b "+town+"  §e«");
		player.sendMessage("");
		ConfigurationSection users = getConfig().getConfigurationSection("Banished."+town);
		if(users == null)
		{
		    player.sendMessage("§bno one banished");
		    player.sendMessage("§e» §3--------------------------- §e«");
		    return;
		}
		Set<String> players = users.getKeys(false);
		for(String p : players)
		{
		    if(!users.getString(p).equalsIgnoreCase("unbanished")) { player.sendMessage("§3-§b "+users.getString(p)); }
		    // etc
		}
		player.sendMessage("§e» §3--------------------------- §e«");
	}
}
