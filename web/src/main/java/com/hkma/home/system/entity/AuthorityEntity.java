package com.hkma.home.system.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity(name = "SystemAuthority")
@IdClass(AuthorityPk.class)
@Table(name = "system_authority")
public class AuthorityEntity implements Serializable {
	@Id
	@Column(nullable = false)
	private String userId;
	
	@Id
	@Column(nullable = false)
	private String systemId;
	
	@Id
	@Column(nullable = false)
	private String modelId;
	
	@Id
	@Column(nullable = false)
	private String processId;
	
	private String systemName;
	
	private String processName;
	
	private String href;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}

class AuthorityPk implements Serializable {
	private String userId;
	private String systemId;
	private String modelId;
	private String processId;
	
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
		
		final AuthorityPk other = (AuthorityPk)obj;
		if (this.userId.equals(other.userId) && this.systemId.equals(other.systemId)
			&& this.modelId.equals(other.modelId) && this.processId.equals(other.processId)) {
			return true;
		}
		
		return false;
	}
	
	@Override
    public int hashCode(){
        return userId.hashCode() + systemId.hashCode() + modelId.hashCode() + processId.hashCode();
    }
}