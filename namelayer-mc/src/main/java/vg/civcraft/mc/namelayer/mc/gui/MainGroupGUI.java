package vg.civcraft.mc.namelayer.mc.gui;


import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.NameAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableSection;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.civmodcore.inventorygui.components.SlotPredicates;
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.civmodcore.inventorygui.components.impl.CommonGUIs;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitLeaveGroup;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitRevokeInvite;
import vg.civcraft.mc.namelayer.mc.util.PlayerGroupSetting;

public class MainGroupGUI {

	private final Group group;
	private final Player player;
	private final GUIGroupOverview parent;

	private ComponableInventory inventory;
	private Scrollbar contentComponent;
	private StaticDisplaySection bottomBar;
	private boolean showInheritedMembers;
	private boolean showInvites;
	private boolean showMembers;
	private boolean showBlacklisted;
	private Set<GroupRank> ranksShown;
	private Set<GroupRank> ranksViewable;
	private NameLayerPermissionManager permMan;

	public MainGroupGUI(GUIGroupOverview parent, Player player, Group group) {
		this.group = group;
		this.player = player;
		this.parent = parent;
		this.permMan = NameLayerPlugin.getInstance().getNameLayerPermissionManager();
		ranksShown = new HashSet<>();
		ranksViewable = new HashSet<>();
		showInheritedMembers = false;
		showInvites = true;
		showMembers = true;
		showBlacklisted = false;
	}

	ComponableInventory getInventory() {
		return inventory;
	}

	/**
	 * Shows the main gui overview for a specific group based on the properties of
	 * this class
	 */
	public void showScreen() {
		if (inventory == null) {
			inventory = new ComponableInventory(ChatColor.GOLD + group.getName(), 6, player);
		} else {
			inventory.removeComponent(contentComponent);
		}
		inventory.clear();
		List<IClickable> clicks = constructContent();
		this.contentComponent = new Scrollbar(clicks, 45);
		inventory.addComponent(contentComponent, i -> true);
		inventory.addComponent(getBottomBar(), SlotPredicates.offsetRectangle(1, 9, 5, 0));
		inventory.show();
	}

	private StaticDisplaySection getBottomBar() {
		bottomBar = new StaticDisplaySection(9);
		bottomBar.set(getInvitePlayerClickable(), 0);
		bottomBar.set(getAddBlackListClickable(), 1);
		bottomBar.set(getVisibilityMenuClickable(), 2);
		bottomBar.set(getLeaveGroupClickable(), 3);
		bottomBar.set(getSuperMenuClickable(), 4);
		bottomBar.set(getDefaultGroupClickable(), 5);
		bottomBar.set(getRankManageClickable(), 6);
		bottomBar.set(getAdminStuffClickable(), 7);
		bottomBar.set(getInfoStack(), 8);
		return bottomBar;
	}

	private IClickable getVisibilityMenuClickable() {
		return new LClickable(Material.LECTERN, ChatColor.GOLD + "Filter and adjust sorting", p -> {
			StaticDisplaySection toggles = new StaticDisplaySection(9);
			toggles.set(
					constructToggle(showInheritedMembers, 1, "Show inherited members", b -> showInheritedMembers = b),
					1);
			toggles.set(constructToggle(showInheritedMembers, 3, "Show invites", b -> showInheritedMembers = b), 3);
			toggles.set(constructToggle(showInheritedMembers, 5, "Show members", b -> showInheritedMembers = b), 5);
			toggles.set(
					constructToggle(showInheritedMembers, 7, "Show blacklisted players", b -> showInheritedMembers = b),
					7);
		});
	}

	private IClickable constructToggle(boolean state, int slot, String name, Consumer<Boolean> setter) {
		return new LClickable((state ? Material.GREEN_DYE : Material.RED_DYE), ChatColor.GOLD + name, p -> {
			setter.accept(!state);
			bottomBar.set(constructToggle(!state, slot, name, setter), slot);
		});
	}

	private List<IClickable> constructContent() {
		List<IClickable> result = new ArrayList<>();
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		List<GroupRank> ranks = new ArrayList<>(rankHandler.getAllRanks());
		boolean sortByRank = NameLayerPlugin.getInstance().getSettingsManager().getGUISortSetting().getValue(player);
		Map<GroupRank, List<UUID>> invertedInviteList = null;
		if (showInvites) {
			invertedInviteList = new HashMap<>();
			for (Entry<UUID, GroupRank> entry : group.getAllInvites().entrySet()) {
				List<UUID> existingInvites = invertedInviteList.computeIfAbsent(entry.getValue(),
						s -> new ArrayList<>());
				existingInvites.add(entry.getKey());
			}
		}
		if (sortByRank) {
			Collections.sort(ranks, Comparator.comparing(GroupRank::getName));
		}
		for (GroupRank rank : ranks) {
			if (rank == rankHandler.getDefaultNonMemberRank()) {
				continue;
			}
			if (!showBlacklisted && rankHandler.isBlacklistedRank(rank)) {
				continue;
			}
			if (!showMembers && rankHandler.isMemberRank(rank)) {
				continue;
			}
			boolean canModify = GroupAPI.hasPermission(player, group, NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker().getRemovePermission(rank.getId()));
			List<IClickable> tempList = new ArrayList<>();
			for (UUID uuid : group.getAllTrackedByType(rank)) {
				ItemStack is = getSkullFor(uuid);
				ItemUtils.addLore(is,
						String.format("%sRank: %s%s", ChatColor.DARK_AQUA, ChatColor.DARK_GRAY, rank.getName()));
				IClickable click;
				if (canModify) {
					ItemUtils.addLore(is, "", ChatColor.AQUA + "Click to modify");
					click = new LClickable(is, p -> handlePlayerClick());
				} else {
					click = new DecorationStack(is);
				}
				tempList.add(click);
			}
			if (showInvites) {
				List<UUID> invitees = invertedInviteList.get(rank);
				if (invitees != null) {
					for (UUID uuid : invitees) {
						ItemStack is = getSkullFor(uuid);
						ItemUtils.addLore(is, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Pending invite", "",
								String.format("%sRank: %s%s", ChatColor.DARK_AQUA, ChatColor.DARK_GRAY,
										rank.getName()));
						ItemUtils.addGlow(is);
						IClickable click;
						if (canModify) {
							ItemUtils.addLore(is, "", ChatColor.AQUA + "Click to modify");
							click = new LClickable(is, p -> handleInviteClick(rank, uuid));
						} else {
							click = new DecorationStack(is);
						}
						tempList.add(click);
					}
				}
			}
			if (!sortByRank) {
				// sort by member name, which is included in the items name
				tempList.sort(Comparator.comparing(i -> i.getItemStack().getItemMeta().getDisplayName()));
			}
			result.addAll(tempList);
		}
		return result;
	}

	private void handlePlayerClick() {
		RankManageGUI rankGUI = new RankManageGUI(group, player, this);
		rankGUI.showScreen();
	}

	private void handleInviteClick(GroupRank rank, UUID playerInviteIsFor) {
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitRevokeInvite(player.getUniqueId(), group, NameAPI.getNameLocal(playerInviteIsFor)));
		showScreen();
	}

	private static ItemStack getSkullFor(UUID uuid) {
		ItemStack is = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		skullMeta.setDisplayName(ChatColor.GOLD + NameAPI.getNameLocal(uuid));
		is.setItemMeta(skullMeta);
		return is;
	}

	private IClickable getAddBlackListClickable() {
		ItemStack inviteStack = new ItemStack(Material.COOKIE);
		ItemUtils.setDisplayName(inviteStack, ChatColor.GOLD + "Add player to blacklist");
		return new LClickable(inviteStack, p -> {
			InvitationGUI invGui = new InvitationGUI(group, p, MainGroupGUI.this, true);
			invGui.showScreen();
		});
	}

	private IClickable getInvitePlayerClickable() {
		ItemStack inviteStack = new ItemStack(Material.COOKIE);
		ItemUtils.setDisplayName(inviteStack, ChatColor.GOLD + "Invite new member");
		return new LClickable(inviteStack, p -> {
			InvitationGUI invGui = new InvitationGUI(group, p, MainGroupGUI.this, false);
			invGui.showScreen();
		});
	}

	private IClickable getDefaultGroupClickable() {
		PlayerGroupSetting defGroupSetting = NameLayerPlugin.getInstance().getSettingsManager().getDefaultGroup();
		ItemStack is = new ItemStack(Material.BRICKS);
		ItemUtils.setDisplayName(is, ChatColor.GOLD + "Default group");
		Group defGroup = defGroupSetting.getGroup(player);
		if (defGroup != null && defGroup.equals(group)) {
			ItemUtils.addLore(is, ChatColor.AQUA + "This group is your current default group");
			return new DecorationStack(is);
		} else {
			ItemUtils.addLore(is, String.format("%sClick to make %s%s your default group", ChatColor.AQUA,
					group.getColoredName(), ChatColor.AQUA));
			if (defGroup != null) {
				ItemUtils.addLore(is,
						String.format("%sYour current default group: %s", ChatColor.BLUE, defGroup.getColoredName()));
			}
			return new LClickable(is, p -> {
				NameLayerPlugin.getInstance().getLogger().log(Level.INFO,
						p.getName() + " set default group to " + group.getName() + " via the gui");
				if (defGroup == null) {
					p.sendMessage(String.format("%sYou have set your default group to %s", ChatColor.GREEN,
							group.getColoredName()));
				} else {
					p.sendMessage(String.format("%sYou have changed your default group from %s%s to %s",
							ChatColor.GREEN, defGroup.getColoredName(), ChatColor.GREEN, group.getColoredName()));
				}
				defGroupSetting.setGroup(player, group);
				inventory.clear();
				showScreen();
			});
		}
	}

	private IClickable getSuperMenuClickable() {
		return new LClickable(Material.PAINTING, ChatColor.GOLD + "Return to overview for all your groups", p -> {
			showParent();
		});
	}

	private IClickable getRankManageClickable() {
		return new LClickable(Material.BOOKSHELF, ChatColor.GOLD + "Manage ranks for this group", p -> {
			handlePlayerClick();
		});
	}

	public void showParent() {
		if (parent != null) {
			parent.showScreen();
			return;
		}
		GUIGroupOverview gui = new GUIGroupOverview(player, inventory);
		gui.showScreen();
	}

	private IClickable getAdminStuffClickable() {
		ItemStack is = new ItemStack(Material.DIAMOND);
		ItemUtils.setDisplayName(is, ChatColor.GOLD + "Owner functions");
		return new LClickable(is, player -> {
				AdminFunctionsGUI subGui = new AdminFunctionsGUI(player, group, MainGroupGUI.this);
				subGui.showScreen();
		});
	}

	/**
	 * Constructs the icon used in the gui for leaving a group
	 */
	private Clickable getLeaveGroupClickable() {
		return new LClickable(Material.IRON_DOOR, ChatColor.GOLD + "Leave group", p -> {
			ComponableSection yesNoSec = CommonGUIs.genConfirmationGUI(5, 9, () -> {
				ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitLeaveGroup(p.getUniqueId(), group));
				showParent();
			}, String.format("%sYes, leave %s", ChatColor.GREEN, group.getColoredName()), () -> {
				showScreen();
			}, String.format("%sNo, do not leave %s", ChatColor.RED, group.getColoredName()));
			inventory.clear();
			inventory.addComponent(yesNoSec, i -> true);
			inventory.show();
		});
	}

	private IClickable getInfoStack() {
		ItemStack is = new ItemStack(Material.PAPER);
		ItemUtils.setDisplayName(is, ChatColor.GOLD + "Stats for " + group.getColoredName());
		ItemUtils.addLore(is, ChatColor.DARK_AQUA + "Your current rank: " + ChatColor.YELLOW
				+ group.getRank(player.getUniqueId()).getName());
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		for (GroupRank rank : group.getGroupRankHandler().getAllRanks()) {
			if (rank == rankHandler.getDefaultNonMemberRank()) {
				continue;
			}
			if (!GroupAPI.hasPermission(player, group, NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker().getListPermission(rank.getId()))) {
				continue;
			}
			int count = group.getAllTrackedByType(rank).size();
			if (count == 0) {
				continue;
			}
			ItemUtils.addLore(is, String.format("%s%s %s", ChatColor.AQUA, count, rank.getName()));
		}
		if (GroupAPI.hasPermission(player, group, NameLayerPlugin.getInstance().getNameLayerPermissionManager().getGroupStats())) {
			ItemUtils.addLore(is,
					String.format("%s%s members in total", ChatColor.DARK_AQUA, group.getAllMembers().size()));
		}
		return new DecorationStack(is);
	}
}
