package com.hkma.home.stock.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "collect.stock_profile")
public class StockProfileEntity implements Serializable {
	@Id
	@Column(name="id", nullable = false)
	private String id;
	
	@Column(name="name", nullable = false)
	private String name;
	
	public String getId() {
		return id;
    }
	
	public String getName() {
		return name;
    }
}
