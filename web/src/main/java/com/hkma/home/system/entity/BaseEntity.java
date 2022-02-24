package com.hkma.home.system.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
	@Column(updatable = false)
	@CreatedDate
	private Date enterDatetime;
	
	@Column(updatable = false)
	private String enterUserId;
	
	@LastModifiedDate
	private Date updateDatetime;
	
	private String updateUserId;
	
	public Date getEnterDatetime() {
        return enterDatetime;
    }

    public void setEnterDatetime(Date enterDatetime) {
        this.enterDatetime = enterDatetime;
    }
    
    public String getEnterUserId() {
        return enterUserId;
    }

    public void setEnterUserId(String enterUserId) {
        this.enterUserId = enterUserId;
    }
    
    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }
    
    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}
