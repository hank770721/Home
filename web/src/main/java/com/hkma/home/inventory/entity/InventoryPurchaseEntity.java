package com.hkma.home.inventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@Table(name = "inventory_purchase")
public class InventoryPurchaseEntity extends BaseEntity implements Serializable {
	@Id
	@Column(name="recordId", nullable = false)
	private String recordId;
	
	@Column(name="recordDate")
	private String recordDate;
	
	private String stockroomUserId;
	
	private String stockroomId;
	
	@NotBlank(message="未輸入")
	@Column(name="class1")
	private String class1;
	
	@NotBlank(message="未輸入")
	@Column(name="class2")
	private String class2;
	
	@NotBlank(message="未輸入")
	@Column(name="brand")
	private String brand;
	
	@NotBlank(message="未輸入")
	@Column(name="name")
	private String name;
	
	@Column(name="model")
	private String model;
	
	@Column(name="size")
	private String size;
	
	@NotNull(message="未輸入")
	@Column(name="quantity")
	private Double quantity;
	
	@Column(name="purchaseDate", nullable = true)
	private String purchaseDate;
	
	@Column(name="manufactureDate", nullable = true)
	private String manufactureDate;
	
	@Column(name="expiryDate", nullable = true)
	private String expiryDate;
	
	@Column(name="amount")
	private Double amount;

	public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
	
	public String getRecordId() {
        return recordId;
    }
	
	public String getStockroomUserId() {
		return stockroomUserId;
	}

	public void setStockroomUserId(String stockroomUserId) {
		this.stockroomUserId = stockroomUserId;
	}

	public String getStockroomId() {
		return stockroomId;
	}

	public void setStockroomId(String stockroomId) {
		this.stockroomId = stockroomId;
	}

	public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
	
	public String getRecordDate() {
        return recordDate;
    }
	
	public void setClass1(String class1) {
        this.class1 = class1;
    }
	
	public String getClass1() {
        return class1;
    }

	public void setClass2(String class2) {
        this.class2 = class2;
    }
	
	public String getClass2() {
        return class2;
    }
	
	public void setBrand(String brand) {
        this.brand = brand;
    }
	
	public String getBrand() {
        return brand;
    }
	
	public void setName(String name) {
        this.name = name;
    }
	
	public String getName() {
        return name;
    }
	
	public void setModel(String model) {
        this.model = model;
    }
	
	public String getModel() {
        return model;
    }
	
	public void setSize(String size) {
        this.size = size;
    }
	
	public String getSize() {
        return size;
    }
	
	public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
	
	public Double getQuantity() {
        return quantity;
    }
	
	public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
	
	public String getPurchaseDate() {
        return purchaseDate;
    }
	
	public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }
	
	public String getManufactureDate() {
        return manufactureDate;
    }
	
	public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
	
	public String getExpiryDate() {
        return expiryDate;
    }
	
	public void setAmount(Double amount) {
        this.amount = amount;
    }
	
	public Double getAmount() {
        return amount;
    }
}
