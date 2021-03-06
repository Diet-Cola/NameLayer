package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.RevokeInvite;

public class RevokeInviteHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, RevokeInvite.REPLY_ID, sendingServer, RevokeInvite.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		String playerName = data.getString("playerName");
		UUID targetPlayer = ZeusMain.getInstance().getPlayerManager().getUUID(playerName);
		if (targetPlayer == null) {
			sendReject(ticket, RevokeInvite.REPLY_ID, sendingServer, RevokeInvite.FailureReason.PLAYER_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			GroupRank rankInvitedTo = group.getInvite(targetPlayer);
			if (rankInvitedTo == null) {
				sendReject(ticket, RevokeInvite.REPLY_ID, sendingServer,
						RevokeInvite.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			PermissionType permNeeded =
					getGroupTracker().getPermissionTracker().getRemovePermission(rankInvitedTo.getId());
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				sendReject(ticket, RevokeInvite.REPLY_ID, sendingServer, RevokeInvite.FailureReason.NO_PERMISSION);
				return;
			}
			getGroupTracker().deleteInvite(group, targetPlayer);
			getGroupTracker().addLogEntry(group,
					new vg.civcraft.mc.namelayer.core.log.impl.RevokeInvite(System.currentTimeMillis(), executor,
							rankInvitedTo.getName(), targetPlayer));
			Map<String, Object> replyParameter = new HashMap<>();
			replyParameter.put("rank_invited_to", rankInvitedTo.getName());
			sendAccept(ticket, RevokeInvite.REPLY_ID, sendingServer, replyParameter);
		}
	}

	@Override
	public String getIdentifier() {
		return RevokeInvite.REQUEST_ID;
	}

}
