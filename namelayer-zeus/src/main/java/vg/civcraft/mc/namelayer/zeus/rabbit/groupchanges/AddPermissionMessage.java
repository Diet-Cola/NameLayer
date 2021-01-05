package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

public class AddPermissionMessage extends GroupChangeMessage {

	private int rankID;
	private int permID;
	
	public AddPermissionMessage(int groupID, int rankID, int permID) {
		super(groupID);
		this.rankID = rankID;
		this.permID = permID;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("rank_id", rankID);
		json.put("perm_id", permID);
	}

	@Override
	public String getIdentifier() {
		return "nl_add_perm";
	}

}