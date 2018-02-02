package net.yzimroni.buildsomething2.game.blocks;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.BPlayer;
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
		return blocks.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
	}
		
	public List<BSBlock> getBlocks() {
		return blocks;
	}

	public boolean hasBlock(BPlayer p, BSBlock b) {
		return p.getData().getBlocks().contains(b.getId());
	}
	
	public boolean hasBlockOrFree(BPlayer p, BSBlock b) {
		if (b.getPrice() == 0) {
			return true;
		}
		return hasBlock(p, b);
	}
	
	public BSBlock getByType(Material type, byte data) {
		return blocks.stream().filter(b -> b.getType().equals(type) && b.getData() == data).findFirst().orElse(null);
	}
	
	public List<BSBlock> getBlocks(BPlayer p) {
		return blocks.stream().filter(b -> hasBlockOrFree(p, b)).collect(Collectors.toList());
	}
	
	public String serializeHotbar(HashMap<Integer, ItemStack> items) {
		if (items.size() != 9) {
			return null;
		}
		String result = "";
		
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
		
		HashMap<Integer, BSBlock> blocks = new HashMap<Integer, BSBlock>();		
		String[] blocksitems = hotbar.split(";");
		for (String block : blocksitems) {
			String[] parts = block.split(":");
			if (parts.length == 2) {
				int slot = Utils.getInt(parts[0]);
				int block_id = Utils.getInt(parts[1]);
				blocks.put(slot, getBlockById(block_id));
			}
		}
		return blocks;
	}
	
	protected void initBlocks() {
		blocks.clear();
		try {
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
	
	public int countBlocks(BPlayer p) {
		return (int) blocks.stream().filter(b -> hasBlock(p, b)).count();	
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
