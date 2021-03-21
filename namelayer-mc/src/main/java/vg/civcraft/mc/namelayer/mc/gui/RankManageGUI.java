package vg.civcraft.mc.namelayer.mc.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ContentAligners;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.civmodcore.inventorygui.components.SlotPredicates;
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;

public class RankManageGUI {

	private ComponableInventory inventory;
	private StaticDisplaySection bottomBar;
	private MainGroupGUI parent;
	private Group group;
	private Player player;

	public RankManageGUI(Group g, Player p, MainGroupGUI parent) {
		this.group = g;
		this.player = p;
		this.parent = parent;
		this.inventory = parent.getInventory();
	}

	public void showScreen() {
		List<IClickable> content = new ArrayList<>();
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		for (GroupRank rank : rankHandler.getAllRanks()) {
			ItemStack is = GUIGroupOverview.getHashedItem(rank.getName().hashCode());
			ItemUtils.setDisplayName(is, ChatColor.GOLD + rank.getName());
			if (rank.getParent() != null) {
				ItemUtils.addLore(is, String.format("%sParent rank: %s", ChatColor.GOLD, rank.getParent().getName()));
			}
			else {
				ItemUtils.addLore(is, ChatColor.GOLD + "Owner rank with all permissions");
			}
			if (rank == rankHandler.getDefaultNonMemberRank()) {
				ItemUtils.addLore(is, ChatColor.AQUA + "The rank anyone not explicitly a member", ChatColor.AQUA + "  implicitly gets");
			}
			if (rank == rankHandler.getDefaultInvitationRank()) {
				ItemUtils.addLore(is, ChatColor.AQUA + "The rank invitations are for if no rank is specified");
			}
			if (rankHandler.isBlacklistedRank(rank)) {
				ItemUtils.addLore(is, ChatColor.DARK_AQUA + "Blacklist rank");
			}
			content.add(new LClickable(is, p -> detailEdit(rank)));
		}
		Scrollbar rankSection = new Scrollbar(content, 45, 45, ContentAligners.getLeftAligned());
		inventory.clear();
		inventory.addComponent(rankSection, i -> true);
		inventory.addComponent(getBottomBar(), SlotPredicates.offsetRectangle(1, 9, 5, 0));
		inventory.show();
	}

	private void detailEdit(GroupRank rank) {
		//add child
		//make default invitiation?
		//rename
		//delete
		//perms?
	}

	private StaticDisplaySection getBottomBar() {
		bottomBar = new StaticDisplaySection(9);
		bottomBar.set(getSuperMenuClickable(), 4);
		return bottomBar;
	}

	private IClickable getSuperMenuClickable() {
		return new LClickable(Material.DIAMOND, ChatColor.GOLD + "Return to overview for all your groups", p -> {
			parent.showScreen();
		});
	}
}
