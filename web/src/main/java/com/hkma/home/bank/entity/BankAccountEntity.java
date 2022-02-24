package com.hkma.home.bank.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@IdClass(BankAccountPk.class)
@Table(name = "bank_account")
public class BankAccountEntity implements Serializable {
	@Id
	@Column(name="userId", nullable = false)
	private String userId;
	
	@Id
	@Column(name="id", nullable = false)
	private String id;
	
	@Column(name="bankId")
	private String bankId;

	@Column(name="memo")
	private String memo;
	
	private String isBankAccount;
	
	@Column(name="isSecurities")
	private String isSecurities;
	
	@Column(name="isCreditCard")
	private String isCreditCard;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name="bankId", referencedColumnName="id", insertable=false, updatable=false)
    private BankEntity bank;
	
	public void setUserId(String userId) {
        this.userId = userId;
    }
	
	public String getUserId() {
        return userId;
    }

	public void setId(String id) {
        this.id = id;
    }
	
	public String getId() {
        return id;
    }
	
	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	
	public void setMemo(String memo) {
        this.memo = memo;
    }
	
	public String getMemo() {
        return memo;
    }

	public String getIsBankAccount() {
		return isBankAccount;
	}

	public void setIsBankAccount(String isBankAccount) {
		this.isBankAccount = isBankAccount;
	}

	public String getIsSecurities() {
		return isSecurities;
	}

	public void setIsSecurities(String isSecurities) {
		this.isSecurities = isSecurities;
	}

	public String getIsCreditCard() {
		return isCreditCard;
	}

	public void setIsCreditCard(String isCreditCard) {
		this.isCreditCard = isCreditCard;
	}

	public BankEntity getBank() {
		return bank;
	}

	public void setBank(BankEntity bank) {
		this.bank = bank;
	}
}
