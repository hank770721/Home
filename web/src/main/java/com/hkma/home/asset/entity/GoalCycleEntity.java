package com.hkma.home.asset.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@IdClass(GoalCyclePk.class)
@Table(name = "asset_goalcycle")
public class GoalCycleEntity extends BaseEntity implements Serializable {
	@Id
	private String accountUserId;
	
	@Id
	private String accountId;
	
	@Id
	private String id;
	
	private int month;
	
	private int day;
	
	private int hour;

	private int minute;
	
	private int second;
	
	private int cycleQty;
	
	private int cycleType;
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getCycleQty() {
		return cycleQty;
	}

	public void setCycleQty(int cycleQty) {
		this.cycleQty = cycleQty;
	}

	public int getCycleType() {
		return cycleType;
	}

	public void setCycleType(int cycleType) {
		this.cycleType = cycleType;
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

class GoalCyclePk implements Serializable {
	private String accountUserId;
	private String accountId;
	private String id;
	
	@Override
    public boolean equals(Object obj){
		if (obj == null){
            return false ;
        }
		
		if (this == obj){
            return true;
        }
		
		if (getClass() != obj.getClass()){
            return false;
        }
		
		final GoalCyclePk other = (GoalCyclePk)obj;
		if (this.accountUserId.equals(other.accountUserId) && this.accountId.equals(other.accountId) && this.id.equals(other.id)) {
			return true;
		}
		
		return false;
	}
	
	@Override
    public int hashCode(){
        return accountUserId.hashCode() + accountId.hashCode() + id.hashCode();
    }
}