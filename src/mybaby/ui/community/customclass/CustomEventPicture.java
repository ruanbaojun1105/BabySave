package mybaby.ui.community.customclass;

public class CustomEventPicture{
	private String mMsg;
	private int deleteMediaFileId;
	public CustomEventPicture(String msg) {
		// TODO Auto-generated constructor stub
		mMsg = msg;
	}

	public CustomEventPicture(int deleteMediaFileId) {
		this.deleteMediaFileId = deleteMediaFileId;
	}

	public int getDeleteMediaFileId() {
		return deleteMediaFileId;
	}

	public String getMsg(){
		return mMsg;
	}
}
