package com.daoutech.api.domain.user.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "tb_user")
public class User implements Serializable, Persistable<String> {

	private static final long serialVersionUID = 1L;
	
	//--- ENTITY PRIMARY KEY
	@Id
    @Column(name="user_id", nullable=false, length=20)
	private String userId;

	//--- ENTITY DATA FIELDS
    @Column(name="user_pw", nullable=false, length=100)
	private String userPw;

    @Column(name="user_nm", nullable=false, length=50)
	private String userNm;
    
    @Column(name="user_type", nullable=false, length=1)
	private String userType;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fnl_login_dttm")
	private Date fnlLoginDttm;
    
    
	@Override
	public String getId() {
		return this.userId;
	}
	@Override
	public boolean isNew() {
		return true;
	}
}
