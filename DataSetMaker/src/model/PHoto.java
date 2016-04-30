package model;

import java.util.Date;

public class PHoto implements Comparable<PHoto> {

	private float latitude;
	private float longitude;
	private Date date;
	private String user;
	private String url;
	
	
	public PHoto(float latitude, float longitude, Date date, String user ) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.date = date;
		this.user = user;
	}


	public float getLatitude() {
		return latitude;
	}


	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}


	public float getLongitude() {
		return longitude;
	}


	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	@Override
	public String toString() {
		return "PHoto [latitude=" + latitude + ", longitude=" + longitude
				+ ", date=" + date + ", user=" + user + "]";
	}


	public String getUrl() {
		return this.url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}


	@Override
	public int compareTo(PHoto PHoto) {
		return getDate().compareTo(PHoto.getDate());
	}
	
	
	

}
