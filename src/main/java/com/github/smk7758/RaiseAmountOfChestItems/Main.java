package com.github.smk7758.RaiseAmountOfChestItems;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static final String plugin_name = "RaiseAmountOfChestItems";
	public static boolean debug_mode = false;
	public CommandExecuter command_executer = new CommandExecuter(this);

	@Override
	public void onEnable() {
		if (!Main.plugin_name.equals(getDescription().getName())) getPluginLoader().disablePlugin(this);
		// getServer().getPluginManager().registerEvents(new ChestListner(), this);
		getCommand(plugin_name).setExecutor(command_executer);
		saveDefaultConfig();
		reloadConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		loadConfigValues();
	}

	public void loadConfigValues() {
		debug_mode = getConfig().getBoolean("DebugMode");
	}

	public enum MessageType {
		NOT_CHEST, BLOCK_NULL, SUCCESSFUL;
	}

	public MessageType raiseAmountOfChestItems(Block block) {
		if (block == null) return MessageType.BLOCK_NULL;
		SendLog.debug("BlockType: " + block.getType().toString());
		BlockFace blockface;
		int count = 0;
		if (block.getState() instanceof Chest) {
			// block.getType().equals(Material.CHEST) && block.getState() instanceof Chest
			SendLog.debug("Chest");
			SendLog.debug("ChestLoc: " + block.getLocation().toVector());
			Chest chest = (Chest) block.getState();
			count += raiseAmountOfInventoryItems(chest.getBlockInventory());
			if (!(blockface = isDoubleChest(block)).equals(BlockFace.SELF)) {
				SendLog.debug("DoubleChest");
				SendLog.debug("FaceTo: " + blockface.toString());
				Chest chest_relative = (Chest) chest.getBlock().getRelative(blockface).getState();
				count += raiseAmountOfInventoryItems(chest_relative.getBlockInventory());
			}
			SendLog.debug("Raised item count: " + count);
			return MessageType.SUCCESSFUL;
		} else {
			return MessageType.NOT_CHEST;
		}
	}

	public int raiseAmountOfInventoryItems(Inventory inventory) {
		ItemStack[] chest_inv_contents = inventory.getContents(),
				contents_new = new ItemStack[chest_inv_contents.length];
		int count = 0;
		final int amount = 64;
		for (int i = 0; i < chest_inv_contents.length; i++) {
			if (chest_inv_contents[i] != null) {
				contents_new[i] = chest_inv_contents[i].clone();
				count += 1;
			} else {
				contents_new[i] = null;
			}
			if (contents_new[i] != null) contents_new[i].setAmount(amount);
			inventory.setItem(i, contents_new[i]);
		}
		return count;
	}

	private BlockFace isDoubleChest(Block block) {
		if (block.getType().equals(Material.CHEST)) {
			return isFaced(block, Material.CHEST);
		} else if (block.getType().equals(Material.TRAPPED_CHEST)) {
			return isFaced(block, Material.TRAPPED_CHEST);
		} else {
			return BlockFace.SELF;
		}
	}

	/**
	 * checks is the block a double chest. if it is not a double chest, returns BlockFace.SELF.
	 *
	 * @param chest 対象のブロック
	 * @return ラージチェストならtrue、違えばfalse
	 * @author HimaJyun edited with smk7758
	 */
	private BlockFace isFaced(Block block, Material block_type) {
		if (block.getType() != block_type) { // そもそもチェストじゃない
			return BlockFace.SELF;
		}

		// 東西南北でテスト
		for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST }) {
			// その位置にあるブロックを取得
			Block relative = block.getRelative(face);
			if (relative.getType() == block_type) { // それが2個横並び(ラージチェスト)なら
				// true
				return face;
			}
		}

		// ラージチェストじゃない
		return BlockFace.SELF;
	}
}
