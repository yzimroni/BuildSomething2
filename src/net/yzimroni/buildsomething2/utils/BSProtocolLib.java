package net.yzimroni.buildsomething2.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;

public class BSProtocolLib {
	private BuildSomethingPlugin plugin;
    //private HashMap<UUID, String> langs = new HashMap<UUID, String>();
	
	public BSProtocolLib(BuildSomethingPlugin p) {
		plugin = p;
		//initPotionsEffectRemover();
		autoCompleteCommands();
		//initPlayerLang();
		initItemGlow();
	}
	
	/*public String getLang(Player p) {
		if (langs.containsKey(p.getUniqueId())) {
			return langs.get(p.getUniqueId());
		}
		if (p.hasMetadata("lang")) {
			if (p.getMetadata("lang").size() == 0) {
				p.removeMetadata("lang", plugin);
			} else {
				//System.out.println("lang from metadata for " + p.getName());
				return p.getMetadata("lang").get(0).asString();
			}
		}
		return "en_US";
	}*/
	
	private void initItemGlow() {
		plugin.getProtocolLib().addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent e) {
				if (e.getPacketType() == PacketType.Play.Server.SET_SLOT) {
	                addGlowItem(new ItemStack[] { e.getPacket().getItemModifier().read(0) });
	            } else {
	                addGlowItem(e.getPacket().getItemArrayModifier().read(0));
	            }
			}
			
		});
		
	}
		
	private void addGlowItem(ItemStack[] stacks) {
		if (stacks == null || stacks.length == 0) return;
			for (ItemStack stack : stacks) {
				if (stack != null) {
					// Only update those stacks that have our flag enchantment
					if (stack.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 32 && stack.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 32) {
						stack.removeEnchantment(Enchantment.SILK_TOUCH);
						stack.removeEnchantment(Enchantment.PROTECTION_EXPLOSIONS);
						NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
						compound.put(NbtFactory.ofList("ench"));
					}
				}
			}
	}

	
	/*private void initPlayerLang() {
		plugin.getProtocolLib().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(PacketEvent e) {
				Player p = e.getPlayer();
				String lang = e.getPacket().getStrings().read(0);
				if (langs.containsKey(p.getUniqueId())) {
					langs.remove(p.getUniqueId());
				}
				langs.put(p.getUniqueId(), lang);
				
				if (p.hasMetadata("lang")) {
					//System.out.println("lang metadata size: " + p.getMetadata("lang").size());
					//p.getMetadata("lang").get(0).invalidate();
					p.removeMetadata("lang", plugin);
				}
				p.setMetadata("lang", new FixedMetadataValue(plugin, lang));
			}
		});
	}*/
	
	/*public void initPotionsEffectRemover() {
		plugin.getProtocolLib().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				modifyWatchable(event, 7, (int) 0);
			}
		});
	}*/
	
	public void autoCompleteCommands() {
		final List<String> allow_cmds_gen = new ArrayList<String>();
		allow_cmds_gen.add("money");
		allow_cmds_gen.add("help");
		allow_cmds_gen.add("msg");
		allow_cmds_gen.add("tell");
		allow_cmds_gen.add("w");
		allow_cmds_gen.add("p");
		allow_cmds_gen.add("party");
		allow_cmds_gen.add("stats");
		
		plugin.getProtocolLib().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Server.TAB_COMPLETE) {
			
			@Override
			public void onPacketSending(PacketEvent e) {
				if (e.getPlayer().isOp()) return;
	            PacketContainer packet = e.getPacket();
	            boolean send = true;
	            String[] cmds = packet.getStringArrays().read(0);
	            if (cmds == null || cmds.length == 0) return;
	            //TO DO check if this is a memeory leak? opps
	            List<String> allow_cmds = new ArrayList<String>(allow_cmds_gen);
	            boolean builder = false;
	            if (BSProtocolLib.this.plugin.getGameManager().isBuilder(e.getPlayer())) {
	            	builder = true;
	            }
	            if (builder) {
	            	//TODO check if this is working
	            	allow_cmds.add("clear");
	            	allow_cmds.add("savehotbar");
	            }
	            List<String> cmd_send = new ArrayList<String>();
	            for (String cmd : cmds) {
	            	if (cmd == null || cmd.isEmpty()) continue;
	            	if (cmd.startsWith("/")) {
			           	cmd = cmd.substring(1, cmd.length());
			           	if (allow_cmds.contains(cmd)) {
			           		cmd_send.add("/" + cmd);
			           	}
	            	} else {
	            		for (Player p : Bukkit.getOnlinePlayers()) {
	            			if (p.getName().toLowerCase().startsWith(cmd.toLowerCase()) && e.getPlayer().canSee(p)) {
	            				cmd_send.add(p.getName());
	            			}
	            		}
	            	}
	            }
	            if (!send && cmd_send.size() == 0) {
	            	
	            } else {
	            	if (!send) {
	            		cmd_send.clear();
	                    packet.getStringArrays().write(0, cmd_send.toArray(new String[]{}));
	            	} else {
	                    packet.getStringArrays().write(0, cmd_send.toArray(new String[]{}));
	            	}
	            }
			}
	});

	}
	
   /* private void modifyWatchable(PacketEvent event, int index, Object value) {
        if (hasIndex(getWatchable(event), index)) {
            event.setPacket(event.getPacket().deepClone());
            for (WrappedWatchableObject object : getWatchable(event)) {
                if (object.getIndex() == index) {
                    object.setValue(value);
                }
            }
        }
    }
    
    private List<WrappedWatchableObject> getWatchable(PacketEvent event) {
        return event.getPacket().getWatchableCollectionModifier().read(0);
    }
    
    private boolean hasIndex(List<WrappedWatchableObject> list, int index) {
        for (WrappedWatchableObject object : list) {
            if (object.getIndex() == index) {
                return true;
            }
        }
        return false;
    }*/
}
