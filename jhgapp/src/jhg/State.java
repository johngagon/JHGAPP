package jhg;

/**
 * DOC
 * 
 * @author John
 *
 */
public enum State {
	BLANK,
	EXISTING,
	NOCHANGE,
	SAVED,
	PUBLISHED,
	SHARED,
	SUBMITTED,
	APPROVED,
	REJECTED,
	EXPORTED,
	ARCHIVED,
	DELETED
}//in archive is only text, not read by this program.
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