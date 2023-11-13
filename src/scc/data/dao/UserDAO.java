package scc.data.dao;

import scc.data.dto.User;
import scc.utils.Hash;

import java.util.Arrays;

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
	private String[] houseIds;
	private String[] rentalIds;

	public UserDAO() {
	}
	public UserDAO(User u) {
		this(u.getId(), u.getName(), Hash.of(u.getPwd()), u.getPhotoId(), u.getHouseIds(), u.getRentalIds());
	}
	public UserDAO(String id, String name, String pwd, String photoId, String[] houseIds, String[] rentalIds) {
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

	public User toUser() {
		return new User(id, name, pwd, photoId,
				houseIds == null ? null : Arrays.copyOf(houseIds,houseIds.length),
				rentalIds == null ? null : Arrays.copyOf(rentalIds,rentalIds.length));
	}
	@Override
	public String toString() {
		return "UserDAO [_rid=" + _rid + ", _ts=" + _ts + ", id=" + id + ", name=" + name + ", pwd=" + pwd
				+ ", photoId=" + photoId + ", houseIds=" + Arrays.toString(houseIds)
				+ ", rentalIds=" + Arrays.toString(rentalIds) + "]";
	}

}
