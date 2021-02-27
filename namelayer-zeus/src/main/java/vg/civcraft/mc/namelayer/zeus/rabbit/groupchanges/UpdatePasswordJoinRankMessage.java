package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class UpdatePasswordJoinRankMessage extends GroupChangeMessage{

	private int targetRankID;

	public UpdatePasswordJoinRankMessage(int groupID, int targetRankID) {
		super(groupID);
		this.targetRankID = targetRankID;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("target_rank_id", targetRankID);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.UPDATE_PASSWORD_JOIN_RANK;
	}
}
