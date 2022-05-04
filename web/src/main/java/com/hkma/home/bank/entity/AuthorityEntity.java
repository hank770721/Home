package com.hkma.home.bank.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(AuthorityPk.class)
@Table(name = "bank_authority")
public class AuthorityEntity implements Serializable {
	@Id
	private String userId;
	
	@Id
	private String accountUserId;
	
	@Id
	private String accountId;
	
	private String orderNumber;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

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
	
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
}

class AuthorityPk implements Serializable {
	private String userId;
	private String accountUserId;
	private String accountId;
	
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
		if (this.userId.equals(other.userId) && this.accountUserId.equals(other.accountUserId) && this.accountId.equals(other.accountId)) {
			return true;
		}
		
		return false;
	}
	
	@Override
    public int hashCode(){
        return userId.hashCode() + accountUserId.hashCode() + accountId.hashCode();
    }
}
