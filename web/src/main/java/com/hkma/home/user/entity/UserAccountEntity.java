package com.hkma.home.user.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_account")
public class UserAccountEntity implements Serializable {
	@Id
	@Column(name="userId", nullable = false)
	private String userId;
	
	@Column(name="password")
	private String password;
	
	public String getUserId() {
        return userId;
    }
}
