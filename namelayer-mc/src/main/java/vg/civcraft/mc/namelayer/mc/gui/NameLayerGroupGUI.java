package vg.civcraft.mc.namelayer.mc.gui;

import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;

/**
 * Abstract utility class, which provides some functionality needed for all guis
 *
 */
public abstract class NameLayerGroupGUI {
	protected final Group group;
	protected final Player player;
	protected final GroupTracker groupTracker;
	protected final NameLayerPermissionManager permMan;

	public NameLayerGroupGUI(Group g, Player p) {
		this.permMan = NameLayerPlugin.getInstance().getNameLayerPermissionManager();
		this.groupTracker = NameLayerPlugin.getInstance().getGroupTracker();
		this.group = g;
		this.player = p;
	}

	public void setupIn(ComponableInventory inv) {

	}

	protected Player getPlayer() {
		return player;
	}

	protected Group getGroup() {
		return group;
	}
}
