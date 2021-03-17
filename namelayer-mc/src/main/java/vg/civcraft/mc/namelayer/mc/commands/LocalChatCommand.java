package vg.civcraft.mc.namelayer.mc.commands;


import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.AikarCommand;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.model.ChatTracker;
import vg.civcraft.mc.namelayer.mc.model.chat.LocalChatMode;

@Description("Switches (or sends message) to local chat.")
public class LocalChatCommand extends AikarCommand {

	private static final String ALIAS = "local|localchat|lchat|lc|exit|e";

	private final ChatTracker modeManager;

	public LocalChatCommand(final NameLayerPlugin plugin) {
		this.modeManager = plugin.getChatTracker();
	}

	@CommandAlias(ALIAS)
	public void switchToLocalChat(final Player sender) {
		this.modeManager.resetChatMode(sender);
	}

	@CommandAlias(ALIAS)
	public void sendMessageToLocalChat(final Player sender, final String message) {
		LocalChatMode.sendLocalMessage(sender, message);
	}

}
