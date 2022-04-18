package com.hkma.home.bank.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(AccountGroupDetailPk.class)
@Table(name = "bank_accountgroupdetail")
public class AccountGroupDetailEntity implements Serializable {
	@Id
	private String userId;
	
	@Id
	private String groupId;
	
	@Id
	private String accountUserId;
	
	@Id
	private String accountId;

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

	public String getAccountUserId() {
		return accountUserId;
	}

	public void setAccountUserId(String accountUserId) {
		this.accountUserId = accountUserId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}

class AccountGroupDetailPk implements Serializable {
	private String userId;
	private String groupId;
	private String accountUserId;
	private String accountId;
}
