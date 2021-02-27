package vg.civcraft.mc.namelayer.core.requests;

public class SetDefaultJoinRank {

	public static final String REQUEST_ID = "nl_req_set_default_join_rank";
	public static final String REPLY_ID = "nl_ans_set_default_join_rank";

	private SetDefaultJoinRank() {}

	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, RANK_DOES_NOT_EXIST, ALREADY_THAT_RANK, NO_PERMISSION;
	}
}
