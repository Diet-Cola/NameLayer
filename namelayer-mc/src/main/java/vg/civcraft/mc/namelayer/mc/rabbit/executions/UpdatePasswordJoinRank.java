package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class UpdatePasswordJoinRank extends AbstractGroupModificationHandler{
	@Override
	protected void handle(Group group, JSONObject data) {
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		GroupRank targetRank = rankHandler.getRank(data.getInt("target_rank_id"));
		getGroupTracker().setPasswordJoinRank(group, targetRank);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.UPDATE_PASSWORD_JOIN_RANK;
	}
}
