package vg.civcraft.mc.namelayer.mc.commands;

import com.github.maxopoly.artemis.ArtemisPlugin;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitSetPasswordJoinRank;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nlspjr")
public class SetPasswordJoinRank extends StandaloneCommand {
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		String groupName = args[0];
		Group group = GroupAPI.getGroup(groupName);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		GroupRank targetRank = rankHandler.getRank(args[1]);
		if (targetRank == null) {
			MsgUtils.sendRankNotExistMsg(player.getUniqueId(), group.getColoredName(), args[1]);
			return true;
		}
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSetPasswordJoinRank(player.getUniqueId(), group.getName(), targetRank));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName("", (Player) sender);
		}
		return Collections.emptyList();
	}
}
