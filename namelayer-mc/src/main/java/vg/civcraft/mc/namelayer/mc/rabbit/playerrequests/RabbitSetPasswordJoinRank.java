package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.SetPasswordJoinRank;

public class RabbitSetPasswordJoinRank extends RabbitGroupAction{

	private GroupRank targetRank;
	public RabbitSetPasswordJoinRank(UUID executor, String groupName, GroupRank targetRank) {
		super(executor, groupName);
		this.targetRank = targetRank;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sYou set the password join rank to %s for group %s", ChatColor.GREEN, targetRank.getName(), group.getColoredName()));
			return;
		}
		SetPasswordJoinRank.FailureReason reason = SetPasswordJoinRank.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
			case NO_PERMISSION:
				String missingPerm = reply.optString("missing_perm", null);
				noPermissionMessage(missingPerm);
				return;
			case NULL_PASSWORD:
				sendMessage(String.format("%sThe group %s%s does not have a password", ChatColor.RED, group.getColoredName(), ChatColor.RED));
				return;
			case GROUP_DOES_NOT_EXIST:
				groupDoesNotExistMessage();
				return;
			case ALREADY_THAT_RANK:
				sendMessage(String.format("%sThe password join rank is already set to %s%s for the group %s", ChatColor.RED, targetRank.getName(), ChatColor.RED, group.getColoredName()));
				return;
			case RANK_DOES_NOT_EXIST:
				sendMessage(String.format("%sThe rank %s%s does not exist for the group %s", ChatColor.RED, targetRank.getName(), ChatColor.RED, group.getColoredName()));
				return;
			default:
				break;
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("target_rank_id", targetRank.getId());
	}

	@Override
	public String getIdentifier() {
		return SetPasswordJoinRank.REQUEST_ID;
	}
}
