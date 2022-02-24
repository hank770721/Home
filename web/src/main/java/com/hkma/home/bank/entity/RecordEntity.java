package com.hkma.home.bank.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@Table(name = "bank_record")
public class RecordEntity extends BaseEntity implements Serializable {
	@Id
	@Column(name="recordId", nullable = false)
	private String recordId;
	
	@Column(name="recordDate")
	private String recordDate;
	
	@Column(name="transMode")
	private String transMode;
	
	@Column(name="fromAccountUserId")
	private String fromAccountUserId;
	
	@Column(name="fromAccountId")
	private String fromAccountId;
	
	@Column(name="toAccountUserId")
	private String toAccountUserId;
	
	@Column(name="toAccountId")
	private String toAccountId;
	
	@Column(name="memo")
	private String memo;
	
	@NotNull(message="未輸入")
	@Column(name="amount")
	private Double amount;
	
	@Column(name="isDividend", nullable = true)
	private String isDividend;
	
	@Column(name="stockId")
	private String stockId;
	
	@Column(name="fromTable")
	private String fromTable;
	
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
	
	public void setFromAccountUserId(String fromAccountUserId) {
        this.fromAccountUserId = fromAccountUserId;
    }
	
	public String getFromAccountUserId() {
        return fromAccountUserId;
    }
	
	public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }
	
	public String getFromAccountId() {
        return fromAccountId;
    }
	
	public void setToAccountUserId(String toAccountUserId) {
        this.toAccountUserId = toAccountUserId;
    }
	
	public String getToAccountUserId() {
        return toAccountUserId;
    }
	
	public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }
	
	public String getToAccountId() {
        return toAccountId;
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
	
	public void setIsDividend(String isDividend) {
		this.isDividend = isDividend;
    }
	
	public String getIsDividend() {
        return isDividend;
    }
	
	public String getStockId() {
        return stockId;
    }
	
	public String getFromTable() {
        return fromTable;
    }
}
