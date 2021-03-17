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
import vg.civcraft.mc.namelayer.core.requests.InvitePlayer;

public class RabbitInvitePlayer extends RabbitGroupAction {

	private String playerName;
	private GroupRank targetRank;

	public RabbitInvitePlayer(UUID executor, Group group, String playerName, GroupRank targetRank) {
		super(executor, group.getName());
		this.playerName = playerName;
		this.targetRank = targetRank;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("target_player", playerName);
		json.put("rank", targetRank.getId());
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%s%s %shas been invited as %s%s%s to %s", ChatColor.YELLOW,
				playerName, ChatColor.GREEN, ChatColor.YELLOW, targetRank.getName(),
				ChatColor.GREEN, group.getColoredName()));
			//Acknowledgement msg

			UUID target = ArtemisPlugin.getInstance().getPlayerDataManager().getOnlinePlayerData(playerName).getUUID();
			TextComponent message = new TextComponent(String.format("%sYou have been invited to %s%s as a %s%s%s!", ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN, ChatColor.YELLOW, targetRank.getName(), ChatColor.GREEN));
			ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSendPlayerTextComponent(
					NameAPI.CONSOLE_UUID, target, message));
			return;
		}
		InvitePlayer.FailureReason reason = InvitePlayer.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NO_PERMISSION:
			noPermissionMessage(missingPerm);
			return;
		case PLAYER_DOES_NOT_EXIST:
			playerDoesNotExistMessage(playerName);
			return;
		case RANK_DOES_NOT_EXIST:
			sendMessage(String.format("%sThe rank you invited %s%s%s to does not exist", ChatColor.RED, ChatColor.YELLOW, playerName, ChatColor.RED));
			return;
		case ALREADY_INVITED:
			sendMessage(String.format("%sThe player %s%s%s is already tracked for %s%s. You have to modify their rank instead of inviting them.",
					ChatColor.RED, ChatColor.YELLOW, playerName, ChatColor.RED,
					group.getColoredName(), ChatColor.RED));
			return;
		case BLACKLISTED_RANK:
			sendMessage(String.format("%sYou can not invite players to the blacklist rank %s%s%s in %s", ChatColor.RED,
					ChatColor.GOLD, targetRank.getName(), ChatColor.RED, group.getColoredName()));
			return;		
		default:
			break;

		}

	}
	
	@Override
	public String getIdentifier() {
		return InvitePlayer.REQUEST_ID;
	}

}
