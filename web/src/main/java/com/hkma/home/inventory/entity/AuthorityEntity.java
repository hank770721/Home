package com.hkma.home.inventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity(name = "InventoryAuthority")
@IdClass(AuthorityPk.class)
@Table(name = "inventory_authority")
public class AuthorityEntity implements Serializable {
	@Id
	@Column(nullable = false)
	private String userId;
	
	@Id
	@Column(nullable = false)
	private String dataUserId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDataUserId() {
		return dataUserId;
	}

	public void setDataUserId(String dataUserId) {
		this.dataUserId = dataUserId;
	}
}
