package vg.civcraft.mc.namelayer.mc.gui;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.chat.dialog.Dialog;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableSection;
import vg.civcraft.mc.civmodcore.inventorygui.components.ContentAligners;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.civmodcore.inventorygui.components.SlotPredicates;
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.civmodcore.inventorygui.components.impl.CommonGUIs;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitCreateRank;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitDeleteRank;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitRenameRank;

public class RankManageGUI {

	private ComponableInventory inventory;
	private StaticDisplaySection bottomBar;
	private MainGroupGUI parent;
	private Group group;
	private Player player;
	private NameLayerPermissionManager perms;

	public RankManageGUI(Group g, Player p, MainGroupGUI parent) {
		this.group = g;
		this.player = p;
		this.parent = parent;
		this.inventory = parent.getInventory();
		this.perms = NameLayerPlugin.getInstance().getNameLayerPermissionManager();
	}

	public void showScreen() {
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		List<IClickable> content = new ArrayList<>();
		for (GroupRank rank : rankHandler.getAllRanks()) {
			ItemStack is = GUIGroupOverview.getHashedItem(rank.getName().hashCode());
			ItemUtils.setDisplayName(is, ChatColor.GOLD + rank.getName());
			if (rank.getParent() != null) {
				ItemUtils.addLore(is, String.format("%sParent rank: %s", ChatColor.GOLD, rank.getParent().getName()));
			} else {
				ItemUtils.addLore(is, ChatColor.GOLD + "Owner rank with all permissions");
			}
			if (rank == rankHandler.getDefaultNonMemberRank()) {
				ItemUtils.addLore(is, ChatColor.AQUA + "The rank anyone not explicitly a member",
						ChatColor.AQUA + "  implicitly gets");
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
		inventory.clear();
		StaticDisplaySection display = new StaticDisplaySection(9);
		display.set(getManagePermsForRank(rank), 1);
		display.set(getDefaultPasswordClickable(rank), 2);
		display.set(getDefaultInvClickable(rank), 3);
		display.set(getSuperMenuClickable(), 4);
		display.set(getAddChildClickable(rank), 5);
		display.set(getRenameRankClickable(rank), 6);
		display.set(getDeleteRankClickable(rank), 7);
		inventory.addComponent(display, SlotPredicates.offsetRectangle(1, 9, 2, 0));
		inventory.show();
	}

	private StaticDisplaySection getBottomBar() {
		bottomBar = new StaticDisplaySection(9);
		bottomBar.set(getSuperMenuClickable(), 4);
		return bottomBar;
	}

	private IClickable getSuperMenuClickable() {
		return new LClickable(Material.ARROW, ChatColor.GOLD + "Return to the previous screen", p -> {
			parent.showScreen();
		});
	}

	private IClickable getAddChildClickable(GroupRank rank) {
		return permissionWrap(new LClickable(Material.COOKIE,
				String.format("%sCreate a new child rank of %s", ChatColor.GOLD, rank.getName()), p -> {
			ClickableInventory.forceCloseInventory(p);
			p.sendMessage(String.format("%sEnter the name of the rank you wish to create or type \"cancel\" to leave this prompt", ChatColor.GREEN));
			new Dialog(p, NameLayerPlugin.getInstance()) {

				@Override
				public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
					return Collections.emptyList();
				}

				@Override
				public void onReply(String[] message) {
					if (message.length > 1) {
						p.sendMessage(ChatColor.RED + "Rank names cannot have spaces");
						detailEdit(rank);
						this.end();
						return;
					}
					String cancel = message[0];
					if (cancel.equals("cancel")) {
						detailEdit(rank);
						this.end();
						return;
					}
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitCreateRank(p.getUniqueId(), group, rank, String.join(" ", message)));
					detailEdit(rank);
					this.end();
				}
			};
		}), perms.getRenameRank());
	}

	private IClickable getDefaultInvClickable(GroupRank rank) {
		return new DecorationStack(Material.STONE);
	}

	private IClickable getDefaultPasswordClickable(GroupRank rank) {
		return new DecorationStack(Material.STONE);
	}

	private IClickable getManagePermsForRank(GroupRank rank) {
		return permissionWrap(new LClickable(Material.OAK_FENCE_GATE, String.format(
				"%sView and edit permissions for %s", ChatColor.GOLD, rank.getName()), p -> {
			PermissionManageGUI permGui = new PermissionManageGUI(group, player, new AdminFunctionsGUI(player, group, parent));
			permGui.detailEdit(rank);
		}), perms.getListPerms());
	}

	private IClickable getRenameRankClickable(GroupRank rank) {
		return permissionWrap(new LClickable(Material.NAME_TAG,
				String.format("%sRename %s", ChatColor.GOLD, rank.getName()), p -> {
			ClickableInventory.forceCloseInventory(p);
			p.sendMessage(String.format("%sEnter the name you wish to change %s%s to or type \"cancel\" to leave this prompt", ChatColor.GREEN, rank.getName(), ChatColor.GREEN));
			new Dialog(p, NameLayerPlugin.getInstance()) {

				@Override
				public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
					return Collections.emptyList();
				}

				@Override
				public void onReply(String[] message) {
					if (message.length > 1) {
						p.sendMessage(ChatColor.RED + "Rank names cannot have spaces");
						detailEdit(rank);
						this.end();
						return;
					}
					String cancel = message[0];
					if (cancel.equals("cancel")) {
						detailEdit(rank);
						this.end();
						return;
					}
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitRenameRank(p.getUniqueId(), group, rank, String.join(" ", message)));
					detailEdit(rank);
					this.end();
				}
			};
		}), perms.getRenameRank());
	}

	private IClickable getDeleteRankClickable(GroupRank rank) {
		return permissionWrap(new LClickable(Material.BARRIER,
				String.format("%sDelete %s%s permanently", ChatColor.GOLD, rank.getName(), ChatColor.GOLD),
				p -> {
					ComponableSection confirm = CommonGUIs.genConfirmationGUI(6, 9, () -> {
						ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitDeleteRank(p.getUniqueId(), group, rank));
						ClickableInventory.forceCloseInventory(p);
					}, String.format("%s%sYes, delete %s%s%s permanently", ChatColor.RED, ChatColor.BOLD,
							group.getColoredName(), ChatColor.RED, ChatColor.BOLD),
							this::showScreen, ChatColor.RED + "No, go back");
					inventory.clear();
					inventory.addComponent(confirm, i -> true);
					inventory.show();
				}), perms.getDeleteRank());
	}

	private IClickable permissionWrap(IClickable click, PermissionType perm) {
		if (!GroupAPI.hasPermission(player, group, perm)) {
			ItemUtils.addLore(click.getItemStack(), ChatColor.RED + "You do not have permission to do this");
			return new DecorationStack(click.getItemStack());
		}
		return click;
	}
}
