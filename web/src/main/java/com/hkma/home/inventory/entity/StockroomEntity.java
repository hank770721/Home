package com.hkma.home.inventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hkma.home.system.entity.BaseEntity;

@Entity
@IdClass(StockroomPk.class)
@Table(name = "inventory_stockroom")
public class StockroomEntity implements Serializable {
	@Id
	@Column(nullable = false)
	private String userId;
	
	@Id
	@Column(nullable = false)
	private String id;
	
	private String name;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

class StockroomPk implements Serializable{
	private String userId;
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
		
		final StockroomPk other = (StockroomPk)obj;
		if (this.userId.equals(other.userId) && this.id.equals(other.id)) {
			return true;
		}
		
		return false;
	}
	
	@Override
    public int hashCode(){
        return userId.hashCode() + id.hashCode();
    }
}