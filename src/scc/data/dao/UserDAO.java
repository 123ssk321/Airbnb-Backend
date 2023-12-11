package scc.data.dao;

import scc.data.dto.User;
import scc.utils.Hash;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a User, as stored in the database
 */
public class UserDAO {
	private String _rid;
	private String _ts;
	private String id; // nickname
	private String name;
	private String pwd;
	private String photoId;
	private List<String> houseIds;
	private List<String> rentalIds;

	public UserDAO() {
	}
	public UserDAO(User u) {
		this(u.getId(), u.getName(), Hash.of(u.getPwd()), u.getPhotoId(), new ArrayList<>(List.of(u.getHouseIds())), new ArrayList<>(List.of(u.getRentalIds())));
	}
	public UserDAO(String id, String name, String pwd, String photoId, List<String> houseIds, List<String> rentalIds) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
		this.houseIds = houseIds;
		this.rentalIds = rentalIds;
	}
	public String get_rid() {
		return _rid;
	}
	public void set_rid(String _rid) {
		this._rid = _rid;
	}
	public String get_ts() {
		return _ts;
	}
	public void set_ts(String _ts) {
		this._ts = _ts;
	}
	public String getId() {
		return id;
	}
	public void setId(String id){
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
	public List<String> getHouseIds() {
		return houseIds;
	}
	public void setHouseIds(List<String> houseIds) {
		this.houseIds = houseIds;
	}

	public List<String> getRentalIds() {
		return rentalIds;
	}

	public void setRentalIds(List<String> rentalIds) {
		this.rentalIds = rentalIds;
	}

	public User toUser() {
		return new User(id, name, pwd, photoId,
				houseIds == null ? null : houseIds.toArray(new String[0]),
				rentalIds == null ? null : rentalIds.toArray(new String[0]));
	}
	@Override
	public String toString() {
		return "UserDAO [_rid=" + _rid + ", _ts=" + _ts + ", _id=" + id + ", name=" + name + ", pwd=" + pwd
				+ ", photoId=" + photoId + ", houseIds=" + houseIds
				+ ", rentalIds=" + rentalIds + "]";
	}

}
