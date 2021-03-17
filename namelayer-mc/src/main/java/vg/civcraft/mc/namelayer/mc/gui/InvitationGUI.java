package vg.civcraft.mc.namelayer.mc.gui;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.chat.dialog.Dialog;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ContentAligners;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.commands.NameLayerTabCompletion;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitBlacklistPlayer;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitInvitePlayer;

public class InvitationGUI {

	private GroupRank selectedType;
	private MainGroupGUI parent;
	private ComponableInventory inventory;
	private boolean blacklist;
	private Group group;
	private Player player;

	public InvitationGUI(Group g, Player p, MainGroupGUI parent, boolean blacklist) {
		this.group = g;
		this.player = p;
		this.blacklist = blacklist;
		this.parent = parent;
		this.inventory = parent.getInventory();
	}

	public void showScreen() {
		List<IClickable> content = new ArrayList<>();
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		for (GroupRank rank : rankHandler.getAllRanks()) {
			if (blacklist != rankHandler.isBlacklistedRank(rank)) {
				continue;
			}
			if (!GroupAPI.hasPermission(player, group, NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker().getInvitePermission(rank.getId()))) {
				continue;
			}
			ItemStack is = GUIGroupOverview.getHashedItem(rank.getName().hashCode());
			ItemUtils.setDisplayName(is, ChatColor.GOLD + rank.getName());
			content.add(new LClickable(is, p -> inviteTo(rank)));
		}
		Scrollbar rankSection = new Scrollbar(content, 45, 45, ContentAligners.getLeftAligned());
		inventory.clear();
		inventory.addComponent(rankSection, i -> true);
		inventory.show();
	}

	private void inviteTo(GroupRank rank) {
		String action = blacklist ? "blacklist" : "invite";
		player.sendMessage(String.format(
				"%sEnter the name of the player to %s as %s%s%s or \"cancel\" to exit this prompt. You may also enter the names "
						+ "of multiple players, separated with spaces to %s all of them.",
				ChatColor.GOLD, action, ChatColor.AQUA, rank.getName(), ChatColor.GOLD, action));
		new Dialog(player, NameLayerPlugin.getInstance()) {
			public void onReply(String[] message) {
				for (String s : message) {
					if (s.equalsIgnoreCase("cancel")) {
						this.end();
						parent.showScreen();
						return;
					}
					if (blacklist) {
						ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitBlacklistPlayer(player.getUniqueId(), group, s, rank));
						this.end();
					}
					else {
						ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitInvitePlayer(player.getUniqueId(), group, s, rank));
						this.end();
					}
				}
				parent.showScreen();
			}

			public List<String> onTabComplete(String word, String[] msg) {
				return NameLayerTabCompletion.completePlayer("");
			}

		};
	}
}
