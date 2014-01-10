package jhg.z;

public enum Operation {}
	/*
	READ(State.NOCHANGE,Privilege.READ,State.NOCHANGE),
	SEARCH(State.NOCHANGE,Privilege.READ,State.NOCHANGE),
	LOOKUP(State.NOCHANGE,Privilege.READ,State.NOCHANGE),
	EXPORT(State.NOCHANGE,Privilege.EXPORT,State.NOCHANGE),
	CREATE(State.BLANK,Privilege.OWN,State.SAVED),
	IMPORT(State.BLANK,Privilege.OWN,State.SAVED),
	OPENSAVED(State.SAVED,Privilege.WRITE,State.SAVED),
	;
	
	private State start;
	private State end;
	private Privilege priv;
	private Operation(State _start,Privilege _priv, State _end){
		this.start = _start;
		this.priv = _priv;
		this.end = _end;
	}
	
	public State nextState(){
		return this.end;
	}

}
/*
STATE    OPERATION  PRIVS_CAN  						RESULTING_STATE	Fields                

BLANK    CREATE     OWN        						CREATED			created_by, owner, create_ts, id, bk, (other req)
SAVED    READ       OWN								SAVED
SAVED    OPEN_WRITE OWN		   						IN_USE          inuse_ts, inuse_by        					
SAVED(I) SAVE       OWN        						SAVED 			modified_ts, modified_by  (version)
SAVED    PUBLISH_R  OWN        						SAVEDR
SAVEDR   PUBLISH_W  OWN        						SAVEDW
SAVED    PUBLISH_W  OWN        						SAVEDW
SAVEDR   READ/SS    ALL(except NONE,LOOKUP)			SAVEDR
SAVEDR   LOOKUP/SD  ALL(except NONE)				SAVEDR
SAVEDR   EXPORT		OWN,APPROVE,EXPORT,WRITE     	SAVEDR
SAVEDW   OPEN_WRITE OWN,APPROVE,WRITE,				IN_USE
SAVEDW(I)SAVE 		OWN,APPROVE,WRITE,				SAVEDW
SAVEDW	 TRANSFER	APPROVE							SAVEDW			owner
SAVEDR	 TRANSFER	APPROVE							SAVEDR			owner
NULL     IMPORT     OWN,APPROVE						SAVED
SAVEDW   SUBMIT     OWN                             SUBMITTED
SUBMITD  APPROVE    APPROVE							APPROVED        approved_ts
SAVEDW   DELETE     OWN,APPROVE                     DELETED         delete_ts
DELETED  ARCHIVE    APPROVE							NULL
SUBMITD  REJECT     APPROVE                         REJECTED        rejected_ts
REJECD   OPEN_WRITE OWN,WRITE                       REJECTED(I)
REJECD(I)SAVE       OWN,WRITE                       REJECTED
REJECD   SUBMIT     OWN                             SUBMITD
REJECD   APPROVE    APPROVE                         APPROVED
APPROVED ARCHIVE    SUPER                           NULL

ILLEGAL:
APPROVED OPEN_WRITE     
*/                 