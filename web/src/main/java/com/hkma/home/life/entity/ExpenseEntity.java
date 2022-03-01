package com.hkma.home.life.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@Table(name = "life_expense")
public class ExpenseEntity extends BaseEntity implements Serializable {
	@Id
	@Column(name="recordId", nullable = false)
	private String recordId;
	
	@Column(name="recordDate")
	private String recordDate;
	
	@Column(name="transMode")
	private String transMode;
	
	@Column(name="accountUserId")
	private String accountUserId;
	
	@Column(name="accountId")
	private String accountId;
	
	@Column(name="memo")
	private String memo;
	
	@NotNull(message="未輸入")
	@Column(name="amount")
	private Double amount;
	
	private String isConsolidation;
	
	public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
	
	public String getRecordId() {
        return recordId;
    }
	
	public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
	
	public String getRecordDate() {
        return recordDate;
    }
	
	public void setTransMode(String transMode) {
        this.transMode = transMode;
    }
	
	public String getTransMode() {
        return transMode;
    }
	
	public void setAccountUserId(String accountUserId) {
        this.accountUserId = accountUserId;
    }
	
	public String getAccountUserId() {
        return accountUserId;
    }
	
	public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
	
	public String getAccountId() {
        return accountId;
    }
	
	public void setMemo(String memo) {
        this.memo = memo;
    }
	
	public String getMemo() {
        return memo;
    }
	
	public void setAmount(Double amount) {
        this.amount = amount;
    }
	
	public Double getAmount() {
        return amount;
    }

	public String getIsConsolidation() {
		return isConsolidation;
	}

	public void setIsConsolidation(String isConsolidation) {
		this.isConsolidation = isConsolidation;
	}
}
