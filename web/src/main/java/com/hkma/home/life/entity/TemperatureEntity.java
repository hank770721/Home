package com.hkma.home.life.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@Table(name = "life_temperature")
public class TemperatureEntity extends BaseEntity implements Serializable {
	@Id
	@Column(name="recordId", nullable = false)
	private String recordId;
	
	@Column(name="recordDate")
	private String recordDate;
	
	@Column(name="userId")
	private String userId;
	
	@NotNull(message="未輸入")
	@Column(name="temperature")
	private Double temperature;
	
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
	
	public void setUserId(String userId) {
        this.userId = userId;
    }
	
	public String getUserId() {
        return userId;
    }
	
	public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
	
	public Double getTemperature() {
        return temperature;
    }
}
