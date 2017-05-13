package mybaby.models;

public class BaseObject {

	int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isNew(){
		return id <= 0;
	} 
}
