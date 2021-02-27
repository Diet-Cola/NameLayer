package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.PropertyChange;

public class ChangeDefaultJoinRank extends PropertyChange {

	public static final String ID = "SET_DEFAULT_JOIN_RANK";

	public ChangeDefaultJoinRank(long time, UUID player, String oldValue, String newValue) {
		super(time, player, oldValue, newValue);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	public static ChangeDefaultJoinRank load(LoggedGroupActionPersistence persist) {
		return new ChangeDefaultJoinRank(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName());
	}
}
