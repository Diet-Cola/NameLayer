package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.SetDefaultJoinRank;

public class RabbitSetDefaultJoinRank extends RabbitGroupAction{

	private GroupRank targetRank;
	public RabbitSetDefaultJoinRank(UUID executor, String groupName, GroupRank targetRank) {
		super(executor, groupName);
		this.targetRank = targetRank;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sSet the default join rank of group %s%s to %s", ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN, targetRank.getName()));
			return;
		}
		SetDefaultJoinRank.FailureReason reason = SetDefaultJoinRank.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
			case GROUP_DOES_NOT_EXIST:
				groupDoesNotExistMessage();
				return;
			case RANK_DOES_NOT_EXIST:
				sendMessage(String.format("%sThe rank %s%s does not exist for the group %s", ChatColor.RED, targetRank.getName(), ChatColor.RED, group.getColoredName()));
				return;
			case NO_PERMISSION:
				String missingPerm = reply.optString("missing_perm", null);
				noPermissionMessage(missingPerm);
				return;
			case ALREADY_THAT_RANK:
				sendMessage(String.format("%sThe default join rank is already set to %s", ChatColor.RED, targetRank.getName()));
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
		return SetDefaultJoinRank.REQUEST_ID;
	}
}
