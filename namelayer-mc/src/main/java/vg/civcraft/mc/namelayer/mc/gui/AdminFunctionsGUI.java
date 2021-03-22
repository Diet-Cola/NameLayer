package vg.civcraft.mc.namelayer.mc.gui;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
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
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.civmodcore.inventorygui.components.impl.CommonGUIs;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitChangeGroupColor;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitDeleteGroup;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitRenameGroup;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitSetGroupPassword;

public class AdminFunctionsGUI {

	private MainGroupGUI parent;
	private ComponableInventory inventory;
	private NameLayerPermissionManager permMan;
	private Group group;
	private Player player;

	public AdminFunctionsGUI(Player player, Group group, MainGroupGUI parent) {
		this.group = group;
		this.player = player;
		this.parent = parent;
		this.inventory = parent.getInventory();
		this.permMan = NameLayerPlugin.getInstance().getNameLayerPermissionManager();
	}

	private void reconstruct() {
		inventory.clear();
		StaticDisplaySection display = new StaticDisplaySection(54);
		inventory.addComponent(display, i -> true);
		display.set(getRenamingClickable(), 20);
		display.set(getChangeGroupColorClickable(), 22);
		display.set(getChangePasswordClickable(), 24);

		display.set(getPermsClickable(), 28);
		display.set(getLinkingClickable(), 30);
		display.set(getMergingClickable(), 32);
		display.set(getDeletingClickable(), 34);
		display.set(getBackButton(), 49);
	}

	public void showScreen() {
		reconstruct();
		inventory.show();
	}

	public ComponableInventory getInventory() {
		return inventory;
	}

	private IClickable getRenamingClickable() {
		return permissionWrap(new LClickable(Material.NAME_TAG,
				String.format("%sRename %s", ChatColor.GOLD, group.getColoredName()), p -> {
			ClickableInventory.forceCloseInventory(p);
			p.sendMessage(String.format("%sEnter the name you wish to change %s%s to or type \"cancel\" to leave this prompt", ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN));
			new Dialog(p, NameLayerPlugin.getInstance()) {

				@Override
				public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
					return Collections.emptyList();
				}

				@Override
				public void onReply(String[] message) {
					if (message.length > 1) {
						p.sendMessage(ChatColor.RED + "Group names cannot have spaces");
						showScreen();
						this.end();
						return;
					}
					String cancel = message[0];
					if (cancel.equals("cancel")) {
						showScreen();
						this.end();
						return;
					}
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitRenameGroup(p.getUniqueId(), group, String.join(" ", message)));
					inventory.setName(ChatColor.GOLD + String.join(" ", message));
					showScreen();
					this.end();
				}
			};
		}), permMan.getRenameGroup());
	}

	private IClickable getChangeGroupColorClickable() {
		return permissionWrap(new LClickable(Material.WHITE_DYE,
				String.format("%sChange group color of %s", ChatColor.GOLD, group.getColoredName()), p -> {
			ClickableInventory.forceCloseInventory(p);
			p.sendMessage(String.format("%sEnter the color you wish to change %s%s to or type \"cancel\" to leave this prompt", ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN));
			new Dialog(p, NameLayerPlugin.getInstance()) {

				@Override
				public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
					return Collections.emptyList();
				}

				@Override
				public void onReply(String[] message) {
					if (message.length > 1) {
						p.sendMessage(ChatColor.RED + "Colors cannot have spaces");
						showScreen();
						this.end();
						return;
					}
					try {
						ChatColor color = ChatColor.of(String.join(" ", message).toUpperCase());
					} catch (IllegalArgumentException ex) {
						p.sendMessage(ChatColor.RED + "That is not a valid color!");
						showScreen();
						this.end();
						return;
					}
					String cancel = message[0];
					if (cancel.equals("cancel")) {
						showScreen();
						this.end();
						return;
					}
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitChangeGroupColor(p.getUniqueId(), group.getName(), ChatColor.of(String.join(" ", message).toUpperCase())));
					showScreen();
					this.end();
				}
			};
		}), permMan.getChangeGroupColor());
	}

	private IClickable getChangePasswordClickable() {
		return permissionWrap(new LClickable(Material.OAK_SIGN,
				String.format("%sSet the password for %s", ChatColor.GOLD, group.getColoredName()), p -> {
			ClickableInventory.forceCloseInventory(p);
			p.sendMessage(String.format("%sEnter the password you wish to set for %s%s or type \"cancel\" to leave this prompt", ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN));
			new Dialog(p, NameLayerPlugin.getInstance()) {

				@Override
				public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
					return Collections.emptyList();
				}

				@Override
				public void onReply(String[] message) {
					if (message.length > 1) {
						p.sendMessage(ChatColor.RED + "Passwords cannot have spaces");
						showScreen();
						this.end();
						return;
					}
					String cancel = message[0];
					if (cancel.equals("cancel")) {
						showScreen();
						this.end();
						return;
					}
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSetGroupPassword(p.getUniqueId(), group, String.join(" ", message)));
					this.end();
					showScreen();
				}
			};

		}), permMan.getPasswordEdit());
	}

	private IClickable getPermsClickable() {
		return permissionWrap(new LClickable(Material.OAK_FENCE_GATE, String.format(
				"%sView and edit permissions for %s%s", ChatColor.GOLD, group.getColoredName(), ChatColor.GOLD), p -> {
			PermissionManageGUI permGui = new PermissionManageGUI(group, player, this);
			permGui.reconstruct();
		}), permMan.getListPerms());
	}

	private IClickable getLinkingClickable() {
		return permissionWrap(new LClickable(Material.CHAIN,
				String.format("%sView existing group links and link %s%s to another group", ChatColor.GOLD,
						group.getColoredName(), ChatColor.GOLD),
				p -> {
					//TODO
				}), permMan.getLinkGroup());
	}

	private IClickable getMergingClickable() {
		return permissionWrap(new LClickable(Material.SPONGE, String.format("%sMerge %s%s into another group",
				ChatColor.GOLD, group.getColoredName(), ChatColor.GOLD), p -> {
			MergeGUI mergeGui = new MergeGUI(group, player, this);
			mergeGui.showScreen();
		}), permMan.getMergeGroup());
	}

	private IClickable permissionWrap(IClickable click, PermissionType perm) {
		if (!GroupAPI.hasPermission(player, group, perm)) {
			ItemUtils.addLore(click.getItemStack(), ChatColor.RED + "You do not have permission to do this");
			return new DecorationStack(click.getItemStack());
		}
		return click;
	}

	private IClickable getDeletingClickable() {
		return permissionWrap(new LClickable(Material.BARRIER,
				String.format("%sDelete %s%s permanently", ChatColor.GOLD, group.getColoredName(), ChatColor.GOLD),
				p -> {
					ComponableSection confirm = CommonGUIs.genConfirmationGUI(6, 9, () -> {
						ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitDeleteGroup(player.getUniqueId(), group.getName()));
						parent.showScreen();
					}, String.format("%s%sYes, delete %s%s%s permanently", ChatColor.RED, ChatColor.BOLD,
							group.getColoredName(), ChatColor.RED, ChatColor.BOLD), () -> {
						reconstruct();
						showScreen();
					}, ChatColor.RED + "No, go back");
					inventory.clear();
					inventory.addComponent(confirm, i -> true);
					inventory.show();
				}), permMan.getDeleteGroup());
	}

	private IClickable getBackButton() {
		ItemStack backToOverview = new ItemStack(Material.ARROW);
		ItemUtils.setDisplayName(backToOverview, ChatColor.GOLD + "Go back to previous menu");
		return new  LClickable(backToOverview, p -> {
			parent.showScreen();
		});
	}
}
