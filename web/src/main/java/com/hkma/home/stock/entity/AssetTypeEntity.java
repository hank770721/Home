package com.hkma.home.stock.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(AssetTypePk.class)
@Table(name = "home.stock_assettype")
public class AssetTypeEntity implements Serializable {
	@Id
	@Column(nullable = false)
	private String accountUserId;
	
	@Id
	@Column(nullable = false)
	private String accountId;
	
	@Id
	@Column(nullable = false)
	private String id;
	
	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

class AssetTypePk implements Serializable {
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
		
		final AssetTypePk other = (AssetTypePk)obj;
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