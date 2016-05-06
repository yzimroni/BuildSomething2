package net.yzimroni.buildsomething2.game.blocks;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.ItemNames;
import net.yzimroni.buildsomething2.utils.Utils;

public class BlockManager {
	private BuildSomethingPlugin plugin;
	private BlockShop blockshop;
	private List<BSBlock> blocks = new ArrayList<BSBlock>();
	
	public BlockManager(BuildSomethingPlugin p) {
		plugin = p;
		blockshop = new BlockShop(plugin);
		initBlocks();
	}
	
	public BSBlock getBlockById(int id) {
		for (BSBlock b : blocks) {
			if (b.getId() == id) {
				return b;
			}
		}
		return null;
	}
		
	public List<BSBlock> getBlocks() {
		return blocks;
	}

	public boolean hasBlock(BPlayer p, BSBlock b) {
		for (int id : p.getData().getBlocks()) {
			if (b.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasBlockAndFree(BPlayer p, BSBlock b) {
		if (b.getPrice() == 0) {
			return true;
		}
		return hasBlock(p, b);
	}
	
	public BSBlock getByType(Material type, byte data) {
		for (BSBlock b : blocks) {
			if (b.getType().equals(type) && b.getData() == data) {
				return b;
			}
		}
		return null;
	}
	
	public List<BSBlock> getBlocks(BPlayer p) {
		List<BSBlock> pblocks = new ArrayList<BSBlock>();
		
		/*for (int id : p.getData().getBlocks()) {
			BSBlock b = getById(id);
			if (b != null) {
				pblocks.add(b);
			} else {
				plugin.log.warning("BSBlock null in getBlocks(" + p.getUUID() + "): block id " + id);
			}
		}*/
		for (BSBlock b : blocks) {
			if (hasBlockAndFree(p, b)) {
				pblocks.add(b);
			}
		}
		
		return pblocks;
	}
	
	public String getBlockName(BSBlock b) {
		if (b.hasName()) {
			return b.getName();
		}
		return ItemNames.lookup(b.toItemStack());
	}
	
	public String serializeHotbar(HashMap<Integer, ItemStack> items) {
		if (items.size() != 9) {
			return null;
		}
		String result = "";
		
		//for (int i = 0; i<blocks.si; i++) {
		for (Entry<Integer, ItemStack> i : items.entrySet()) {
			ItemStack item = i.getValue();
			if (item == null || item.getType() == null || item.getType() == Material.AIR || item.getAmount() == 0) {
				continue;
			}
			@SuppressWarnings("deprecation")
			BSBlock b = getByType(item.getType(), item.getData().getData());
			if (b == null) {
				continue;
			}
			
			//now we are starting to add the item into the result
			if (!result.isEmpty()) {
				result += ";";
			}
			result += "" + i.getKey() + ":" + b.getId();
		}
		
		return result;
	}
	
	public HashMap<Integer, BSBlock> deserializeHotbar(String hotbar) {
		
		if (hotbar == null || hotbar.isEmpty()) {
			return null;
		}
		
		//BSBlock[] blocks = new BSBlock[]{};
		HashMap<Integer, BSBlock> blocks = new HashMap<Integer, BSBlock>();		
		String[] blocksitems = hotbar.split(";");
		for (String block : blocksitems) {
			//System.out.println("1");
			String[] parts = block.split(":");
			if (parts.length == 2) {
				//System.out.println("2");
				int slot = Utils.getInt(parts[0]);
				int block_id = Utils.getInt(parts[1]);
				blocks.put(slot, getBlockById(block_id));
			}
		}
		//System.out.println("size: " + blocks.size());
		return blocks;
	}
	
	protected void initBlocks() {
		blocks.clear();
		try {
			//TO DO order by
			ResultSet rs = plugin.getDB().get("SELECT * FROM blocks ORDER BY order_i DESC");
			while (rs.next()) {
				BSBlock b = new BSBlock(rs.getInt("ID"), Material.valueOf(rs.getString("type")), (byte) rs.getInt("data"), rs.getString("displayname"), rs.getDouble("price"));
				blocks.add(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		plugin.log.info("loaded " + blocks.size() + " blocks");
	}
	
	public void onDisable() {
		blocks.clear();
		blocks = null;
	}

	public int countBlocks(BPlayer p) {
		int count = 0;
		for (BSBlock b : blocks) {
			if (hasBlock(p, b)) {
				count++;
			}
		}
		
		return count;
	
	}
	
	/**
	 * @return the blockshop
	 */
	public BlockShop getBlockShop() {
		return blockshop;
	}

	/**
	 * @param blockshop the blockshop to set
	 */
	public void setBlockShop(BlockShop blockshop) {
		this.blockshop = blockshop;
	}
}
