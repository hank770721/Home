package com.hkma.home.asset.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@IdClass(GoalPk.class)
@Table(name = "asset_goal")
public class GoalEntity extends BaseEntity implements Serializable {
	@Id
	@Column(name="accountUserId", nullable = false)
	private String accountUserId;
	
	@Id
	@Column(name="accountId", nullable = false)
	private String accountId;
	
	private Double amount;
	
	private String memo;

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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
