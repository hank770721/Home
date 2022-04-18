package com.hkma.home.bank.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(BankAccountListPk.class)
@Table(name = "bank_account")
public class BankAccountListEntity implements Serializable {
	@Id
	private String userId;
	
	@Id
	private String id;
	
	private String bankId;

	private String memo;
	
    private String bankName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
}

class BankAccountListPk implements Serializable {
	private String userId;
	private String id;
}
