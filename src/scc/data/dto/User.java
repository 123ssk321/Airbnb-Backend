package scc.data.dto;

import java.util.Arrays;

/**
 * Represents a User, as returned to the clients
 * 
 * NOTE: array of house ids is shown as an example of how to store a list of elements and 
 * handle the empty list.
 */
public class User {
	private String id; // nickname
	private String name;
	private String pwd;
	private String photoId;
	private String[] houseIds;
	private String[] rentalIds;
	public User(String id, String name, String pwd, String photoId, String[] houseIds, String[] rentalIds) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
		this.houseIds = houseIds;
		this.rentalIds = rentalIds;
	}
	public User(){}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	public String[] getHouseIds() {
		return houseIds == null ? new String[0] : houseIds ;
	}
	public void setHouseIds(String[] houseIds) {
		this.houseIds = houseIds;
	}

	public String[] getRentalIds() {
		return rentalIds;
	}

	public void setRentalIds(String[] rentalIds) {
		this.rentalIds = rentalIds;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", pwd=" + pwd + ", photoId=" + photoId + ", houseIds="
				+ Arrays.toString(houseIds) + ", rentaldIds=" + Arrays.toString(rentalIds) + "]";
	}

}
