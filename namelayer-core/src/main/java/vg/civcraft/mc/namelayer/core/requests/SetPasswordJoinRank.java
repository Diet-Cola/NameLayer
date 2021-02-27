package vg.civcraft.mc.namelayer.core.requests;

public class SetPasswordJoinRank {

	private SetPasswordJoinRank() {}

	public static final String REQUEST_ID = "nl_req_set_password_join_rank";
	public static final String REPLY_ID = "nl_ans_set_password_join_rank";

	public enum FailureReason {
		NO_PERMISSION, NULL_PASSWORD, GROUP_DOES_NOT_EXIST, ALREADY_THAT_RANK, RANK_DOES_NOT_EXIST;
	}
}
