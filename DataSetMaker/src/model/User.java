package model;

import java.util.List;

public class User {
	
	private String id;
	private List<PHoto> photos;
	
	
	public User(String id, List<PHoto> photos) {
		super();
		this.id = id;
		this.photos = photos;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public List<PHoto> getPhotos() {
		return photos;
	}


	public void setPhotos(List<PHoto> photos) {
		this.photos = photos;
	}

	
	
}
