package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import org.json.JSONObject;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;

public class CreateGroup extends LoggedGroupAction {

	public static final String ID = "CREATE_GROUP";

	private String name;

	public CreateGroup(long time, UUID player, String name) {
		super(time, player);
		Preconditions.checkNotNull(name, "Name may not be null");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("name", name);
	}

}
