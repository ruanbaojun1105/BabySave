package mybaby.models.friend;

import android.database.Cursor;

public class PhoneContact {
	public enum remoteSync{		
		LocalModified,
		SyncError,
		SyncSuccess
	}

	private int contactId;
	private int rowVersion;
	private int remoteSyncFlag;
	
	public int getContactId(){
		return contactId;
	}
	public void  setContactId(int contactId){
		this.contactId = contactId;
	}
	public int getRowVersion(){
		return rowVersion;
	}
	public void  setRowVersion(int rowVersion){
		this.rowVersion = rowVersion;
	}
	public int getRemoteSyncFlag(){
		return remoteSyncFlag;
	}
	public void  setRemoteSyncFlag(int remoteSyncFlag){
		this.remoteSyncFlag = remoteSyncFlag;
	}
	
	public PhoneContact(Cursor c){
		this.setContactId(c.getInt(0));
		this.setRowVersion(c.getInt(1));
		this.setRemoteSyncFlag(c.getInt(2));
	}
	
	public PhoneContact(int contactId,
						int rowVersion,
						int remoteSyncFlag){
		this.setContactId(contactId);
		this.setRowVersion(rowVersion);
		this.setRemoteSyncFlag(remoteSyncFlag);
	}
}
