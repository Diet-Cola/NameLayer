package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class UpdateDefaultJoinRankMessage extends GroupChangeMessage{

	private int newDefaultJoinRankID;

	public UpdateDefaultJoinRankMessage(int groupID, int newDefaultJoinRankID) {
		super(groupID);
		this.newDefaultJoinRankID = newDefaultJoinRankID;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("new_default_join_rank_id", newDefaultJoinRankID);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.UPDATE_DEFAULT_JOIN_RANK;
	}
}
