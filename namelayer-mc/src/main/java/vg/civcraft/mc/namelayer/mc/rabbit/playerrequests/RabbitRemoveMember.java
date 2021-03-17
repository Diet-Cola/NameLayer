package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.NameAPI;
import com.github.maxopoly.artemis.rabbit.outgoing.RabbitSendPlayerTextComponent;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.RemoveMember;

public class RabbitRemoveMember extends RabbitGroupAction {

	
	private String playerName;

	public RabbitRemoveMember(UUID executor, Group group, String playerName) {
		super(executor, group.getName());
		this.playerName = playerName;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			int currentRankId = reply.getInt("current_rank_id");
			GroupRank currentRank = group.getGroupRankHandler().getRank(currentRankId);
			sendMessage(String.format("%s%s%s with the rank %s%s%s was kicked from %s", ChatColor.YELLOW,
				playerName, ChatColor.GREEN, ChatColor.YELLOW, currentRank.getName(),
				ChatColor.GREEN, group.getColoredName()));
			//Acknowledgement msg

			UUID target = ArtemisPlugin.getInstance().getPlayerDataManager().getOnlinePlayerData(playerName).getUUID();
			TextComponent message = new TextComponent(ChatColor.RED + "You have been removed from " + group.getColoredName() + ChatColor.RED + "!");
			ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSendPlayerTextComponent(
					NameAPI.CONSOLE_UUID, target, message));
			return;
		}
		RemoveMember.FailureReason reason = RemoveMember.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case CANNOT_KICK_SELF:
			sendMessage(ChatColor.RED + "You can not kick yourself from a group, leave it instead");
			return;
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NOT_A_MEMBER:
			sendMessage(ChatColor.RED + "The player is not a member of the group or you do not have sufficient permission to demote them");
			return;
		case NO_PERMISSION:
			noPermissionMessage(missingPerm);
			return;
		case PLAYER_DOES_NOT_EXIST:
			playerDoesNotExistMessage(playerName);
			return;
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("playerName", playerName);
	}
	
	@Override
	public String getIdentifier() {
		return RemoveMember.REQUEST_ID;
	}

}
