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
	private String stockroomUserId;
	
	@Id
	@Column(nullable = false)
	private String stockroomId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
}

class AuthorityPk implements Serializable {
	private String userId;
	private String stockroomUserId;
	private String stockroomId;
	
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
		if (this.userId.equals(other.userId) && this.stockroomUserId.equals(other.stockroomUserId) && this.stockroomId.equals(other.stockroomId)) {
			return true;
		}
		
		return false;
	}
	
	@Override
    public int hashCode(){
        return userId.hashCode() + stockroomUserId.hashCode() + stockroomId.hashCode();
    }
}
