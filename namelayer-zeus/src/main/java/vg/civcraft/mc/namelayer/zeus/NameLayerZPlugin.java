package vg.civcraft.mc.namelayer.zeus;

import com.github.maxopoly.zeus.plugin.ZeusLoad;
import com.github.maxopoly.zeus.plugin.ZeusPlugin;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.log.abstr.GroupActionLogFactory;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.AcceptInviteHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.BlacklistPlayerHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.ChangeGroupColorHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.CreateGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.CreateRankHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.DeleteGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.DeleteRankHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.EditPermissionHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.InvitePlayerHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.JoinGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.LeaveGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.LinkGroupsHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.MergeGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.PromotePlayerHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RegisterMetaDataDefaultHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RegisterPermissionHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RejectInviteHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RemoveMemberHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RenameGroupHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RenameRankHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RequestGroupCacheHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.RevokeInviteHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.SendGroupChatMessageHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.SendLocalChatMessageHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.SendPrivateChatMessageHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.SetDefaultJoinRankHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.SetPasswordHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.SetPasswordJoinRankHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.UnblacklistPlayerHandler;
import vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits.UnlinkGroupsHandler;

@ZeusLoad(name = "NameLayer", version = "1.0", description = "Player driven group management")
public class NameLayerZPlugin extends ZeusPlugin {

	private static NameLayerZPlugin instance;

	public static NameLayerZPlugin getInstance() {
		return instance;
	}

	private ZeusGroupTracker groupTracker;
	private ServerGroupKnowledgeTracker groupKnowledgeTracker;
	private NameLayerDAO dao;
	private GroupActionLogFactory actionLogFactory;

	@Override
	public boolean onEnable() {
		instance = this;
		this.dao = new NameLayerDAO(getName(), logger);
		if (!this.dao.updateDatabase()) {
			return false;
		}
		this.actionLogFactory = new GroupActionLogFactory();
		Group.setActionLogFactory(this.actionLogFactory);
		this.groupTracker = new ZeusGroupTracker(dao);
		this.groupKnowledgeTracker = new ServerGroupKnowledgeTracker(groupTracker, dao);
		registerRabbitListeners();
		registerPluginlistener(new NameLayerListener());
		return true;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	private void registerRabbitListeners() {
		registerRabbitListener(new AcceptInviteHandler(), new BlacklistPlayerHandler(), new CreateGroupHandler(),
				new CreateRankHandler(), new DeleteGroupHandler(), new DeleteRankHandler(), new EditPermissionHandler(),
				new InvitePlayerHandler(), new JoinGroupHandler(), new LeaveGroupHandler(), new LinkGroupsHandler(),
				new MergeGroupHandler(), new PromotePlayerHandler(), new RegisterPermissionHandler(),
				new RejectInviteHandler(), new RemoveMemberHandler(), new RenameGroupHandler(), new RenameRankHandler(),
				new RevokeInviteHandler(), new SetPasswordHandler(), new UnlinkGroupsHandler(), new ChangeGroupColorHandler(),
				new SendGroupChatMessageHandler(), new SendLocalChatMessageHandler(), new SetDefaultJoinRankHandler(),
				new SendPrivateChatMessageHandler(), new RequestGroupCacheHandler(), new RegisterMetaDataDefaultHandler(),
				new UnblacklistPlayerHandler(), new SetPasswordJoinRankHandler());
	}

	public NameLayerDAO getDAO() {
		return dao;
	}
	
	public GroupActionLogFactory getActionLogFactory() {
		return actionLogFactory;
	}

	public ServerGroupKnowledgeTracker getGroupKnowledgeTracker() {
		return groupKnowledgeTracker;
	}

	public ZeusGroupTracker getGroupTracker() {
		return groupTracker;
	}

}
