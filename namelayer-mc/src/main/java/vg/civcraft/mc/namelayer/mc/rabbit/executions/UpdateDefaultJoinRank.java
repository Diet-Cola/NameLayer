package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class UpdateDefaultJoinRank extends AbstractGroupModificationHandler{
	@Override
	protected void handle(Group group, JSONObject data) {
		int newDefaultJoinRankID = data.getInt("new_default_join_rank_id");
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		GroupRank newDefaultJoinRank = rankHandler.getRank(newDefaultJoinRankID);
		group.getGroupRankHandler().setDefaultInvitationRank(newDefaultJoinRank);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.UPDATE_DEFAULT_JOIN_RANK;
	}
}
