package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

public class AddInviteMessage extends GroupMemberRankModifyMessage {

	public AddInviteMessage(int groupID, UUID player, int rankID) {
		super(groupID, player, rankID);
	}

	@Override
	protected void fillJson(JSONObject json) {
		//nothing needed
	}

	@Override
	public String getIdentifier() {
		return "nl_add_invite";
	}
}