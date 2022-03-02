package com.hkma.home.inventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@Table(name = "inventory_use")
public class InventoryUseEntity extends BaseEntity implements Serializable {
	@Id
	@Column(name="recordId", nullable = false)
	private String recordId;
	
	@Column(name="recordDate")
	private String recordDate;
	
	@Column(name="purchaseId")
	private String purchaseId;
	
	@Column(name="beginDate")
	private String beginDate;
	
	@Column(name="endDate")
	private String endDate;
	
	@Column(name="isRunOut")
	private String isRunOut;

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
	
	public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }
	
	public String getPurchaseId() {
        return purchaseId;
	}
	
	public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }
	
	public String getBeginDate() {
        return beginDate;
    }
	
	public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
	
	public String getEndDate() {
        return endDate;
    }
	
	public void setIsRunOut(String isRunOut) {
        this.isRunOut = isRunOut;
    }
	
	public String getIsRunOut() {
        return isRunOut;
    }
}
