package com.hkma.home.asset.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@IdClass(FixedCostPk.class)
@Table(name = "asset_fixedcost")
public class FixedCostEntity extends BaseEntity implements Serializable {
	@Id
	@Column(nullable = false)
	private String userId;
	
	@Id
	@Column(nullable = false)
	private String id;
	
	private Integer month;
	
	private Integer day;
	
	private Integer cycleQty;
	
	private String cycleType;
	
	private String name;
	
	private Double amount;
	
	private String memo;

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

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getCycleQty() {
		return cycleQty;
	}

	public void setCycleQty(Integer cycleQty) {
		this.cycleQty = cycleQty;
	}

	public String getCycleType() {
		return cycleType;
	}

	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

class FixedCostPk implements Serializable {
	private String userId;
	private String id;
}