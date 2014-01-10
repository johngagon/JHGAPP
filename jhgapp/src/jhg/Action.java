package jhg;

/**
 * Action is an application concept limited to actions in java applications.
 * This can function stand-alone or with a front end and is a very basic level of 
 * handling concepts for data writing applications.
 * 
 * It holds the information about each instance of an action including who is doing what when 
 * and with what objects.
 * 
 * @author John
 *
 */
public class Action {
	
	/**
	 * The action can have a code for the kind of action it is.
	 * The codes also help determine the privilege needed for the action, the state 
	 * required for the action and the transitioned to state for completing an action 
	 * successfully.
	 * 
	 * @author John
	 * 
	 */
	public static enum Code{
		NONE(Privilege.NONE,State.EXISTING,State.NOCHANGE),
		LISTENTITIES(Privilege.LOOKUP,State.EXISTING,State.NOCHANGE),
		CREATE(Privilege.OWN,State.BLANK,State.SAVED),
		IMPORT(Privilege.OWN,State.BLANK,State.SAVED),
		LOOKUP(Privilege.LOOKUP,State.PUBLISHED,State.NOCHANGE),//up to exported 
		SEARCH(Privilege.LOOKUP,State.PUBLISHED,State.NOCHANGE),//up to exported 
		VIEW(Privilege.READ,State.PUBLISHED,State.NOCHANGE),       
		OPEN(Privilege.WRITE,State.SHARED,State.NOCHANGE),		//up to shared or maybe even submitted 
		CLOSE(Privilege.WRITE,State.SHARED,State.NOCHANGE),		
		UPDATE(Privilege.WRITE,State.SHARED,State.NOCHANGE),
		
		PUBLISH(Privilege.OWN,State.SAVED,State.PUBLISHED),
		UNPUBLISH(Privilege.OWN,State.PUBLISHED,State.SAVED),
		SHARE(Privilege.OWN,State.PUBLISHED,State.PUBLISHED),
		UNSHARE(Privilege.OWN,State.SHARED,State.PUBLISHED),
		SUBMIT(Privilege.OWN,State.SAVED,State.SUBMITTED),      //locks the record. (no update
		UNSUBMIT(Privilege.OWN,State.SUBMITTED,State.SAVED),    //unlocks
		APPROVE(Privilege.APPROVE,State.SUBMITTED,State.APPROVED),
		UNAPPROVE(Privilege.APPROVE,State.APPROVED,State.SUBMITTED),
		REJECT(Privilege.APPROVE,State.SUBMITTED,State.REJECTED),
		UNREJECT(Privilege.APPROVE,State.REJECTED,State.SUBMITTED),
		
		TRANSFER_OWNER(Privilege.APPROVE,State.EXISTING,State.NOCHANGE),
		ASSIGN_APPROVER(Privilege.APPROVE,State.EXISTING,State.NOCHANGE),
		
		EXPORT(Privilege.APPROVE,State.REJECTED,State.EXPORTED),           //rejected or approved.
		DELETE(Privilege.OWN,State.SAVED,State.DELETED),                   //record gone
		ARCHIVE(Privilege.APPROVE,State.EXPORTED,State.ARCHIVED),          //record gone
		VIEW_HISTORY(Privilege.APPROVE,State.SAVED,State.NOCHANGE);		
//		ROLLBACK(Privilege.APPROVE,State.SAVED,State.NOCHANGE),      TODO impl?
//		ROLLFORWARD(Privilege.APPROVE,State.SAVED,State.NOCHANGE),
	
		private Privilege priv;
		private State requiredState;
		private State completedState;
		
		/*
		 * Private constructor for an enum.
		 */
		private Code(Privilege p, State _r, State _s){
			this.priv = p;
			this.requiredState = _r;
			this.completedState = _s;
		}
		
		/**
		 * Get the privilege for this action.
		 * 
		 * @return
		 */
		public Privilege getPrivilege(){
			return this.priv;
		}
		
		/**
		 * Gets the required state of what we're acting on.
		 * This is the value set during the code's enum's construction.
		 * 
		 * @return
		 */
		public State getDefaultRequired(){
			return requiredState;
		}
		
		/**
		 * Get the state required of an owner which can differ from most default states required.
		 * 
		 * @return
		 */
		public State getOwnerRequired(){
			switch(this){
				case LOOKUP:;
				case SEARCH:;
				case VIEW:;
				case OPEN:;
				case CLOSE:;
				case UPDATE:return State.SAVED;
				default: return this.getDefaultRequired();
			}
		}
		
		/**
		 * Is the code a reading code or not?
		 * 
		 * @return true if it's a data read related code.
		 */
		public boolean isRead(){       
			switch(this){
				case LOOKUP:
				case SEARCH:
				case VIEW:return true;
				default:return false;
			}
		}
		
		/**
		 * Is this a data writing code or not?
		 * 
		 * @return true if it's a data writing related code.
		 */
		public boolean isWrite(){
			switch(this){
				case OPEN: 
				case CLOSE:
				case UPDATE:return true;
				default:return false;
			}
		}
		
		/**
		 * For this action's code, can something in the given state and privilege be allowed to be proceed?
		 * 
		 * @param s  the state of an object to be receiving the action.
		 * @param p  the privilege of the user doing the action.
		 * @return true if this action's require state and privilege are allowable with the ones given.
		 */
		public boolean canWorkOn(State s, Privilege p){
			return this.requiredState.equals(s) || this.requiredState.equals(State.EXISTING);
			//TODO this method only gets used the by the SimpleApplicationEngine checkState but it doesn't make use of privilege
			//that is done later by checkPrivilege. However, the privilege might be useful in conjunction with some states.
			//currently, this just sees if state matches this action's required or the action allows any.
			//it's used in cases where the state must match. (archive,assign approver, delete,export,transfer,update state)
			//TODO clarify the usage here.
		}
		
		/**
		 * For this action's code, can something in the given state and privilege be allowed for reading with the given object's state/priv?
		 * If the user is an owner or higher, we may allow reading data in more situations.
		 * 
		 * @param s  the state of the object for the action.
		 * @param isOwnerOrHigher  if the user is an owner or higher 
		 * @return true if this action allows objects of this state and this kind of user to read.
		 */
		public boolean canLookupRead(State s, boolean isOwnerOrHigher){  //these actions can be done on models in state s.
			//TODO refactor this all into one call and make these private these.
			switch(s){
				case SAVED: if(isOwnerOrHigher){return true;}else{return false;}				    
				case PUBLISHED:             //published lookup,readers,writers on up.
				case SHARED:case SUBMITTED:case REJECTED: case APPROVED: case EXPORTED:
				return true;
				default:return false;
			}
		}
		
		/**
		 * For this action's code, can something in the given state and privilege be allowed for writing with the given object's state/priv?
		 * If the user is an owner or higher, we may allow writing data in more situations.
		 * 
		 * @param s  the state of the object for the action.
		 * @param isOwnerOrHigher  if the user is an owner or higher 
		 * @return true if this action allows objects of this state and this kind of user to write.
		 */
		public boolean canWrite(State s, boolean isOwnerOrHigher){
			switch(s){
				case SAVED:case PUBLISHED:  if(isOwnerOrHigher){return true;}else{return false;}
				case SHARED:                //shared writers
				return true;
				default:return false;
			}
		}
		
		/**
		 * Get the completion state for this action.
		 * 
		 * @return
		 */
		public State getCompletedState(){
			return this.completedState;
		}
	}
	
	private Code code;
	private Integer managerId;
	private Integer modelId;
	private Integer changeId;
	private Integer userId;
	private Integer roleId;
	private Integer txfrUserId;
	private String client;
	private String signature;
	private String changes;
	private String comment;
	
	/**
	 * Create an action with the given code.
	 * 
	 * @param _code
	 */
	public Action(Code _code){
		this.code = _code;
		managerId = 0;
		modelId = 0;
		changeId = 0;
		userId = 0;
		roleId = 0;
		txfrUserId = 0;
		client = "";
		signature = "";
		changes = "";
		comment = "";
	}
	
	/**
	 * Initialize this action with the details of the action. 
	 * 
	 * @param _manId the identification of the entity
	 * @param _modId the identification of the model.
	 * @param _chgId the identification of the change version no.
	 * @param _usrId the identification of the user.
	 * @param _roleId the identification of the user's role.
	 * @return the modified action.
	 */
	public Action init(Integer _manId, Integer _modId, Integer _chgId, Integer _usrId, Integer _roleId){
		this.managerId = _manId;
		this.modelId = _modId;
		this.changeId = _chgId;
		this.userId = _usrId;
		this.roleId = _roleId;
		return this;
	}
	
	/**
	 * Get the privilege from the code on this action.
	 * 
	 * @return
	 */
	public Privilege getPrivilege(){
		return this.code.getPrivilege();
	}
	
	/**
	 * Get the required state from the coe of this action.
	 * @return
	 */
	public State getRequiredState(){
		return this.code.getDefaultRequired();
	}
	
	/**
	 * Set the client string for this action.
	 * 
	 * @param _str
	 * @return the modified action.
	 */
	public Action client(String _str){
		this.client = _str;
		return this;
	}
	
	/**
	 * Set the comment string for this action.
	 * 
	 * @param _str
	 * @return the modified action.
	 */
	public Action comment(String _str){
		this.comment = _str;
		return this;
	}
	
	/**
	 * Set the user id for transfer types of actions.
	 * 
	 * @param _t the user id for the user to be transferred to.
	 * 
	 * @return the modified action.
	 */
	public Action transfer(Integer _t){
		this.txfrUserId = _t;
		return this;
	}
	
	/**
	 * Set a kind of object update summary or detail of the changes in the fields performed.
	 * 
	 * @param _s  the detail of each previous and update value and/or the summary of changes from the user.
	 * @return the modified action.
	 */
	public Action changes(String _s){ // TODO could do newModel.toJSON or something
		this.changes = _s;
		return this;
	}
	
	/**
	 * Actions that require signatures can have that signature hash set here.
	 * 
	 * @param _s the signature hash
	 * @return the modified action.
	 */
	public Action sign(String _s){
		this.signature = _s;
		return this;
	}

	/**
	 * Get the code enum value for this type of action.
	 * 
	 * @return the code
	 */
	public Code getCode() {
		return code;
	}
	
	/**
	 * Get the manager identifier that corresponds to the entity/model type for this action.
	 * 
	 * @return the managerId
	 */
	public Integer getManagerId() {
		return managerId;
	}
	
	/**
	 * Get the model's id specific to this action.
	 * 
	 * @return the modelId
	 */
	public Integer getModelId() {
		return modelId;
	}
	/**
	 * Get the change id (version/audit id) specific to this action.
	 * 
	 * @return the changeId
	 */
	public Integer getChangeId() {
		return changeId;
	}
	
	/**
	 * Get the user id of the user doing this action.
	 * 
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	
	/**
	 * Get the role id of the role of the user performing the action.
	 * 
	 * @return the roleId
	 */
	public Integer getRoleId() {
		return roleId;
	}
	
	/**
	 * Get the user id of the user being transferred to for all transfer like actions.
	 * 
	 * @return the txfrUserId
	 */
	public Integer getTxfrUserId() {
		return txfrUserId;
	}
	
	/**
	 * Get the client string (in the event of named clients using this application and performing the actions.
	 * 
	 * @return the client
	 */
	public String getClient() {
		return client;
	}
	
	/**
	 * Get the signature hash provided to this action for signed actions.
	 * 
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}
	
	/**
	 * Get the change summary or details for this action. 
	 * 
	 * @return the changes
	 */
	public String getChanges() {
		return changes;
	}
	
	/**
	 * Get the user comment provided to this action.
	 * 
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	
	
}
/*
		sb.append(Model.AUX_MODEL+"( MANAGER_ID, MODEL_ID, CHANGE_ID, EVENT_ID, EVENT_STAMP, ");
		sb.append(" USER_ID, ROLE_ID,   TXFR_USER_ID,  CLIENT, SIGNATURE, ");
		sb.append(" CHANGES, COMMENTS ) ");				
 */

