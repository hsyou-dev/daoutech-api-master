package com.daoutech.api.domain.statistics.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import com.daoutech.api.security.UserRole;
import com.daoutech.api.util.UserUtil;

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
@Table(name = "tb_statistics")
public class Statistics implements Serializable, Persistable<String> {

	private static final long serialVersionUID = 1L;
	
	//시간|가입자수|탈퇴자수|결제금액|사용금액|매출금액|등록일시/수정일시/수정(등록)ID

	//--- ENTITY PRIMARY KEY
	@Id
	@Column(name="date_hour", nullable=false, length=10)
	private String dateHour;
	
	//--- ENTITY DATA FIELDS
	@Column(name="join_cnt")
	private int joinCnt;
	
	@Column(name="leave_cnt")
	private int leaveCnt;
	
	@Column(name="pay_amount")
	private long payAmount;
	
	@Column(name="use_amount")
	private long useAmount;
	
	@Column(name="sales_amount")
	private long salesAmount;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="create_dttm", nullable=false)
	private Date createDttm;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modify_dttm")
	private Date modifyDttm;
    
    @Column(name="update_id", length=20)
	private String updateId;
	
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="update_id", referencedColumnName="user_id")
//	private User user;
    
	
    @PrePersist
    public void persistCreateDttm() {
    	this.createDttm = this.createDttm == null ? Timestamp.valueOf(LocalDateTime.now()) : this.createDttm;
    	this.updateId = UserRole.SYSTEM.getDefaultValue();
    	UserUtil.getCurrentLoginUser().ifPresent(o -> {
    		this.updateId = o.getUserId();
    	});
//    	this.user = User.builder().userId(UserRole.SYSTEM.getDefaultValue()).build();
//    	UserUtil.getCurrentLoginUser().ifPresent(o -> {
//    		this.user.setUserId(o.getUserId());
//    	});
    }
    @PreUpdate
    public void updateModifyDttm() {
    	this.modifyDttm = this.modifyDttm == null ? Timestamp.valueOf(LocalDateTime.now()) : this.modifyDttm;
    	this.updateId = UserRole.SYSTEM.getDefaultValue();
    	UserUtil.getCurrentLoginUser().ifPresent(o -> {
    		this.updateId = o.getUserId();
    	});
//    	this.user = User.builder().userId(UserRole.SYSTEM.getDefaultValue()).build();
//    	UserUtil.getCurrentLoginUser().ifPresent(o -> {
//    		this.user.setUserId(o.getUserId());
//    	});
    }
    
	@Override
	public String getId() {
		return this.dateHour;
	}
	@Override
	public boolean isNew() {
		return true;
	}
}
