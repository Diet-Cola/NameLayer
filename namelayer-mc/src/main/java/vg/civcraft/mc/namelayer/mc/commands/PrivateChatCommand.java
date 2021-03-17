package vg.civcraft.mc.namelayer.mc.commands;

import co.aikar.commands.annotation.Optional;
import com.google.common.base.Strings;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.model.PlayerData;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import vg.civcraft.mc.civmodcore.command.AikarCommand;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.ChatTracker;
import vg.civcraft.mc.namelayer.mc.model.chat.PrivateChatMode;
import vg.civcraft.mc.namelayer.mc.util.ChatStrings;
import vg.civcraft.mc.namelayer.mc.util.NameLayerSettingManager;

@Description("Leaves your private chat.")
public class PrivateChatCommand extends AikarCommand {

	private static final String ALIAS = "message|msg|m|pm|tell";

	private final ChatTracker modeManager;
	private final NameLayerSettingManager settings;

	public PrivateChatCommand(final NameLayerPlugin plugin) {
		this.settings = plugin.getSettingsManager();
		this.modeManager = plugin.getChatTracker();
	}

	@CommandAlias(ALIAS)
	public void togglePrivateChat(final Player sender) {
		this.modeManager.resetChatMode(sender);
	}

	@CommandAlias(ALIAS)
	@CommandCompletion("@players @nothing")
	public void switchToPrivateChat(final Player sender, final String name, @Optional final String message) {
		final PlayerData receiverData = ArtemisPlugin.getInstance().getPlayerDataManager().getOnlinePlayerData(name);
		if (receiverData == null) {
			sender.sendMessage(ChatStrings.chatRecipientNowOffline);
			return;
		}
		if (Strings.isNullOrEmpty(message)) {
			this.modeManager.setReplyChannel(receiverData.getUUID(), sender.getUniqueId());
			this.modeManager.setChatMode(sender, new PrivateChatMode(receiverData.getUUID()), true);
		} else {
			this.modeManager.setReplyChannel(receiverData.getUUID(), sender.getUniqueId());
			PrivateChatMode.sendPrivateMessage(sender, receiverData.getUUID(), message);
		}
	}
}
