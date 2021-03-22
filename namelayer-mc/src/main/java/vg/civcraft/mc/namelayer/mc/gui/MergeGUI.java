package vg.civcraft.mc.namelayer.mc.gui;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.civmodcore.inventorygui.components.impl.CommonGUIs;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitMergeGroup;

public class MergeGUI {

	private AdminFunctionsGUI parent;
	private boolean mergeIntoThisGroup;
	private ComponableInventory inventory;
	private Group group;
	private Player player;

	public MergeGUI(Group g, Player p, AdminFunctionsGUI parent) {
		this.group = g;
		this.player = p;
		this.parent = parent;
		this.inventory = parent.getInventory();
	}

	public void showScreen() {
		ItemStack mergeThisIntoOtherStack = new ItemStack(Material.MINECART);
		ItemUtils.setDisplayName(mergeThisIntoOtherStack, ChatColor.GOLD + "Merge this group into a different one");
		ItemUtils.addLore(mergeThisIntoOtherStack, ChatColor.AQUA
				+ "This action will transfer all reinforcements, bastions and snitches of this group to the one you chose next. "
				+ "This group will be deleted in the process");
		ItemStack mergeOtherIntoThisStack = new ItemStack(Material.CHEST_MINECART);
		ItemUtils.setDisplayName(mergeOtherIntoThisStack, ChatColor.GOLD + "Merge a different group into this one");
		ItemUtils.addLore(mergeOtherIntoThisStack, ChatColor.AQUA
				+ "This action will transfer all reinforcements, bastions and snitches of the group you chose next to this group. "
				+ "The group chosen will be deleted in the process");
		StaticDisplaySection display = new StaticDisplaySection(54);
		display.set(new LClickable(mergeThisIntoOtherStack, p -> {
			mergeIntoThisGroup = false;
			showMergeGroupSelector();
		}), 20);
		display.set(new LClickable(mergeOtherIntoThisStack, p -> {
			mergeIntoThisGroup = true;
			showMergeGroupSelector();
		}), 24);

		// exit button
		ItemStack backToOverview = new ItemStack(Material.ARROW);
		ItemUtils.setDisplayName(backToOverview, ChatColor.GOLD + "Go back to previous menu");
		display.set(new LClickable(backToOverview, p -> {
			parent.showScreen();
		}), 49);
		inventory.clear();
		inventory.addComponent(display, i -> true);
		inventory.show();
	}

	private void showMergeGroupSelector() {
		List<IClickable> clicks = new ArrayList<>();
		List<Group> groups = new ArrayList<>(NameLayerPlugin.getInstance().getGroupTracker().getGroupsForPlayer(player.getUniqueId()));
		Collections.sort(groups);
		for (Group otherGroup : groups) {
			ItemStack is = GUIGroupOverview.getHashedItem(otherGroup.getName().hashCode());
			ItemUtils.setDisplayName(is, otherGroup.getColoredName());
			clicks.add(new LClickable(is, p -> {
				Group toKeep;
				Group toRemove;
				if (mergeIntoThisGroup) {
					toKeep = this.group;
					toRemove = otherGroup;
				} else {
					toKeep = otherGroup;
					toRemove = this.group;
				}
				CommonGUIs.genConfirmationGUI(6, 9, () -> {
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitMergeGroup(player.getUniqueId(), toKeep, toRemove));
					if (mergeIntoThisGroup) {
						parent.showScreen();
					} else {
						ClickableInventory.forceCloseInventory(p);
					}
				}, String.format("%sYes, merge %s%s into %s", ChatColor.GREEN, toRemove.getColoredName(),
						ChatColor.GREEN, toKeep.getName()), () -> {
					parent.showScreen();
				}, ChatColor.RED + "No, cancel merging");
			}));
		}
		inventory.clear();
		Scrollbar groupScroll = new Scrollbar(clicks, 45);
		inventory.addComponent(groupScroll, i -> true);
		StaticDisplaySection section = new StaticDisplaySection(9);
		ItemStack backToOverview = new ItemStack(Material.ARROW);
		ItemUtils.setDisplayName(backToOverview, ChatColor.GOLD + "Go back to previous menu");
		section.set(new LClickable(backToOverview, p -> {
			parent.showScreen();
		}), 4);
		inventory.addComponent(section, i -> true);
		inventory.show();
	}

}
