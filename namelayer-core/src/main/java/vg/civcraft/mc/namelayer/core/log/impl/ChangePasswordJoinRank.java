package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.PropertyChange;

public class ChangePasswordJoinRank extends PropertyChange {

	public static final String ID = "SET_PASSWORD_JOIN_RANK";
	

	public ChangePasswordJoinRank(long time, UUID player, String oldValue, String newValue) {
		super(time, player, oldValue, newValue);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	public static ChangePasswordJoinRank load(LoggedGroupActionPersistence persist) {
		return new ChangePasswordJoinRank(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName());
	}
}
