package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import com.github.maxopoly.zeus.servers.ConnectedServer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.SetDefaultJoinRank;

public class SetDefaultJoinRankHandler extends GroupRequestHandler{
	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, SetDefaultJoinRank.REPLY_ID, sendingServer, SetDefaultJoinRank.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			GroupRankHandler rankHandler = group.getGroupRankHandler();
			GroupRank targetRank = rankHandler.getRank(data.getInt("target_rank_id"));
			if (targetRank == null) {
				sendReject(ticket, SetDefaultJoinRank.REPLY_ID, sendingServer, SetDefaultJoinRank.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getInvitePermission(targetRank.getId());
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", permNeeded.getName());
				sendReject(ticket, SetDefaultJoinRank.REPLY_ID, sendingServer, SetDefaultJoinRank.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			if (rankHandler.getDefaultInvitationRank() == targetRank) {
				sendReject(ticket, SetDefaultJoinRank.REPLY_ID, sendingServer, SetDefaultJoinRank.FailureReason.ALREADY_THAT_RANK);
				return;
			}
			getGroupTracker().setDefaultJoinRank(group, targetRank);
			sendAccept(ticket, SetDefaultJoinRank.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return SetDefaultJoinRank.REQUEST_ID;
	}
}
