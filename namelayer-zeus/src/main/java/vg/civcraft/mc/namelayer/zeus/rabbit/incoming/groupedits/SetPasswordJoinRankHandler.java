package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import com.github.maxopoly.zeus.servers.ConnectedServer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.NameLayerMetaData;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.log.impl.ChangePasswordJoinRank;
import vg.civcraft.mc.namelayer.core.requests.SetPasswordJoinRank;

public class SetPasswordJoinRankHandler extends GroupRequestHandler{
	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, SetPasswordJoinRank.REPLY_ID, sendingServer, SetPasswordJoinRank.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			GroupRankHandler rankHandler = group.getGroupRankHandler();
			GroupRank oldPasswordJoinRank = rankHandler.getDefaultPasswordJoinRank();
			GroupRank targetRank = rankHandler.getRank(data.getInt("target_rank_id"));
			if (targetRank == null) {
				sendReject(ticket, SetPasswordJoinRank.REPLY_ID, sendingServer, SetPasswordJoinRank.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.PASSWORD);
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", permNeeded.getName());
				sendReject(ticket, SetPasswordJoinRank.REPLY_ID, sendingServer, SetPasswordJoinRank.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			if (group.getMetaData(NameLayerMetaData.PASSWORD_KEY) == null) {
				sendReject(ticket, SetPasswordJoinRank.REPLY_ID, sendingServer, SetPasswordJoinRank.FailureReason.NULL_PASSWORD);
				return;
			}
			if (rankHandler.getDefaultPasswordJoinRank() == targetRank) {
				sendReject(ticket, SetPasswordJoinRank.REPLY_ID, sendingServer, SetPasswordJoinRank.FailureReason.ALREADY_THAT_RANK);
				return;
			}
			getGroupTracker().setPasswordJoinRank(group, targetRank);
			getGroupTracker().addLogEntry(group, new ChangePasswordJoinRank(System.currentTimeMillis(), executor, oldPasswordJoinRank.getName(), targetRank.getName()));
			sendAccept(ticket, SetPasswordJoinRank.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return SetPasswordJoinRank.REQUEST_ID;
	}
}
