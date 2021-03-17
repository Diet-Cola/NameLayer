package vg.civcraft.mc.namelayer.mc.gui.folder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.gui.GUIGroupOverview;
import vg.civcraft.mc.namelayer.mc.gui.MainGroupGUI;

public class GroupEntry extends FolderElement {
	private Group group;

	public GroupEntry(FolderElement parent, String groupName) {
		super(groupName, parent);
		this.group = GroupAPI.getGroup(groupName);
	}

	public Group getGroup() {
		return group;
	}

	@Override
	public IClickable getGUIEntry(GUIGroupOverview gui, Player player) {
		ItemStack is = GUIGroupOverview.getHashedItem(group.getName().hashCode());
		ItemUtils.setDisplayName(is, group.getColoredName());
		return new LClickable(is, p -> {
			if (doMovingCheck(gui, player)) {
				return;
			}
			MainGroupGUI groupGui = new MainGroupGUI(gui, player, group);
			groupGui.showScreen();
		});
	}
}
