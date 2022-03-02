package com.hkma.home.inventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@IdClass(StockroomPk.class)
@Table(name = "inventory_stockroom")
public class StockroomEntity implements Serializable {
	@Id
	@Column(nullable = false)
	private String userId;
	
	@Id
	@Column(nullable = false)
	private String id;
	
	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
