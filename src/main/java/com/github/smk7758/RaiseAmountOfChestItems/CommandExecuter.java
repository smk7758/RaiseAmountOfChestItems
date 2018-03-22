package com.github.smk7758.RaiseAmountOfChestItems;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.smk7758.RaiseAmountOfChestItems.Main.MessageType;

public class CommandExecuter implements CommandExecutor {
	public Main main = null;
	final static Set<Material> target_block;
	static {
		target_block = new HashSet<>();
		target_block.add(Material.CHEST);
	}

	public CommandExecuter(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("RaiseAmountOfChestItems")) {
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				main.reloadConfig();
				SendLog.send("Reload has been compleated.", sender);
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				Block target_block = player.getTargetBlock(null, 10);
				MessageType result = main.raiseAmountOfChestItems(target_block);
				if (result.equals(MessageType.SUCCESSFUL)) SendLog.send("Compleate.", sender);
				else if (result.equals(MessageType.NOT_CHEST)) SendLog.error("Target block was not a chest.", sender);
				else if (result.equals(MessageType.BLOCK_NULL)) SendLog.error("Block is null.", sender);
				else SendLog.error("Something wrong happend!", sender);
			} else {
				SendLog.error("Please use this command from Player.", sender);
			}
			return true;
		}
		return false;
	}
}
