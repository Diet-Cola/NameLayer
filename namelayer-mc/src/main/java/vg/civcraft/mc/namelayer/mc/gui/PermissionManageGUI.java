package vg.civcraft.mc.namelayer.mc.gui;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ContentAligners;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitEditPermission;

public class PermissionManageGUI {

	private AdminFunctionsGUI parent;
	private ComponableInventory inventory;
	private Scrollbar rankSection;
	private Group group;
	private Player player;

	public PermissionManageGUI(Group g, Player p, AdminFunctionsGUI parent) {
		this.group = g;
		this.player = p;
		this.parent = parent;
		this.inventory = parent.getInventory();
	}

	public void reconstruct() {
		if (rankSection == null) {
			List<IClickable> content = new ArrayList<>();
			GroupRankHandler rankHandler = group.getGroupRankHandler();
			for (GroupRank rank : rankHandler.getAllRanks()) {
				if (rank == rankHandler.getOwnerRank()) {
					continue; // always has all perms
				}
				ItemStack is = GUIGroupOverview.getHashedItem(rank.getName().hashCode());
				ItemUtils.setDisplayName(is, ChatColor.GOLD + rank.getName());
				if (GroupAPI.hasPermission(player, group, NameLayerPlugin.getInstance().getNameLayerPermissionManager().getModifyPerm())) {
					ItemUtils.addLore(is, ChatColor.AQUA + "Click to view and edit permissions");
				} else {
					ItemUtils.addLore(is, ChatColor.AQUA + "Click to view permissions");
				}
				content.add(new LClickable(is, p -> detailEdit(rank)));
			}
			rankSection = new Scrollbar(content, 45, 45, ContentAligners.getCenteredInOrder(content.size(), 45));
		}
		inventory.clear();
		inventory.addComponent(rankSection, i -> true);
		inventory.show();
	}

	private void detailEdit(GroupRank rank) {
		List<IClickable> content = new ArrayList<>();
		List<PermissionType> perms = new ArrayList<>(NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker().getAllPermissions());
		boolean canEdit = GroupAPI.hasPermission(player, group, NameLayerPlugin.getInstance().getNameLayerPermissionManager().getModifyPerm());
		for (PermissionType perm : perms) {
			boolean hasPerm = GroupAPI.hasPermission(player, group, perm);
			Material mat = hasPerm ? Material.GREEN_DYE : Material.RED_DYE;
			ItemStack is = new ItemStack(mat);
			ItemUtils.setDisplayName(is, perm.getName());
			ItemUtils.addLore(is, perm.getDescription());
			if (canEdit) {
				content.add(new LClickable(is, p -> {
					//Fix adding/remove
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitEditPermission(p.getUniqueId(), group, true, rank, perm));
					reconstruct();
					detailEdit(rank);
				}));
			} else {
				content.add(new DecorationStack(is));
			}
		}
		Scrollbar scroll = new Scrollbar(content, 45, 45, ContentAligners.getLeftAligned());
		inventory.clear();
		inventory.addComponent(scroll, i -> true);
		inventory.show();
	}

}
