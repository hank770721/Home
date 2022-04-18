package com.hkma.home.bank.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(AccountGroupPk.class)
@Table(name = "bank_accountgroup")
public class AccountGroupEntity implements Serializable {
	@Id
	private String userId;
	
	@Id
	private String groupId;
	
	private String groupName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}

class AccountGroupPk implements Serializable {
	private String userId;
	private String groupId;
}