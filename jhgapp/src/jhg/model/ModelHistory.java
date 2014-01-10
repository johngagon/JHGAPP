package jhg.model;

import java.util.Date;

import jhg.Action;

public class ModelHistory {

	//id;
	private Model model;
	private Integer changeId;
	private Action.Code event;
	private Date eventStamp;
	private Integer userId;
	private Integer roleId;
	private Integer transferUserId;

	private String client;
	private String signature;
	private String comments;

	/**
	 * Minimum required construction.
	 * 
	 * @param _chgId
	 * @param _user
	 * @param _role
	 * @param _stamp
	 * @param _m
	 * @param _ev
	 */
	public ModelHistory(Integer _chgId, Integer _user, Integer _role, 
			Date _stamp, Model _m, Action.Code _ev ){
		super();
		this.changeId = _chgId;
		this.userId = _user;
		this.roleId = _role;
		this.eventStamp = _stamp;
		this.model = _m;
		this.event = _ev;
		this.client = "";
		this.signature = "";
		this.comments = "";
	}
	
	
	
/**
	 * @param client the client to set
	 */
	public void setClient(String client) {
		this.client = client;
	}



	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}



	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}



	/*
			sb.append("MANAGER_ID INTEGER, ");
			sb.append("MODEL_ID INTEGER, ");
			sb.append("CHANGE_ID INTEGER, ");
			sb.append("EVENT_ID INTEGER, ");
			sb.append("EVENT_STAMP TIMESTAMP, ");
			sb.append("USER_ID INTEGER, ");
			sb.append("CLIENT VARCHAR(200), ");
			sb.append("ROLE_ID INTEGER, ");
			sb.append("SIGNATURE VARCHAR(500), ");
			//sb.append("CHANGES VARCHAR(8000), ");
			sb.append("COMMENTS VARCHAR(500) ");	
 */
	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}
	/**
	 * @return the changeId
	 */
	public int getChangeId() {
		return changeId;
	}
	/**
	 * @return the event
	 */
	public Action.Code getEvent() {
		return event;
	}
	/**
	 * @return the eventStamp
	 */
	public Date getEventStamp() {
		return eventStamp;
	}
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * @return the roleId
	 */
	public Integer getRoleId() {
		return roleId;
	}
	/**
	 * @return the client
	 */
	public String getClient() {
		return client;
	}
	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @return the transferUserId
	 */
	public Integer getTransferUserId() {
		return transferUserId;
	}



	/**
	 * @param transferUserId the transferUserId to set
	 */
	public void setTransferUserId(Integer transferUserId) {
		this.transferUserId = transferUserId;
	}	
}
