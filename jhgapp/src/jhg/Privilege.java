package jhg;

/**
 * DOC
 * 
 * @author John
 *
 */
public enum Privilege {
	NONE,					//0 this user can do nothing. the user won't see it in count or lookup.
	LOOKUP,                 //1 this user can see this record counted and can see it in lookup.
	READ,					//2 this user can see the current record in detail
	WRITE,					//3 this user can contribute to the record, update and do above (update)
	//EXPORT,                 //4 this user can export all records 
	OWN,					//5 this user can import, create, or remove (soft-delete) a record and add/remove/update roles on read or write and do above
	APPROVE,                //6 this user can add approval, reject, unlock, transfer owner to another in same role and do above
	SUPER;                   //7 this user can do all the above plus whatever migrations may be needed. 

	public boolean has(Privilege p){
		switch(this){
			case SUPER:   return true;
			case NONE:    switch(p){case NONE:return true;default: return false;}
			case LOOKUP:  switch(p){case NONE:;case LOOKUP:return true;default:return false;}
			case READ:    switch(p){case NONE:;case LOOKUP:;case READ:return true;default:return false;}
			//case EXPORT:  switch(p){case NONE:;case LOOKUP:;case READ:;case EXPORT:return true;default:return false;}
			case WRITE:   switch(p){case NONE:;case LOOKUP:;case READ:;case WRITE:return true;default:return false;}
			case OWN:     switch(p){case NONE:;case LOOKUP:;case READ:;case WRITE:;case OWN:return true;default:return false;}
			case APPROVE: switch(p){case NONE:;case LOOKUP:;case READ:;case WRITE:;case OWN:;case APPROVE:return true;default:return false;}
			default: return false;
		}
	}
	public String idString(){
		return String.valueOf(this.ordinal());
	}
}

//none,lookup,read,write,export,own,approve,super

/*

SS - search summary
SD - search detail

STATE    OPERATION  PRIVS_CAN  						RESULTING_STATE	Fields                

NULL     CREATE     OWN        						CREATED			created_by, owner, create_ts, id, bk, (other req)
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