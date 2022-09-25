create table if not exists tb_user (
     	user_id varchar(20) not null,
       	user_pw varchar(100) not null,
       	user_nm varchar(50) not null,
       	user_type varchar(1) not null,
       	fnl_login_dttm timestamp,
       	primary key (user_id)
	);
    
create table if not exists tb_statistics (
     	date_hour varchar(10) not null,
    	join_cnt integer not null,
        leave_cnt integer not null,
        pay_amount bigint not null,
        use_amount bigint not null,
        sales_amount bigint not null,
        update_id varchar(20),
        create_dttm timestamp not null,
        modify_dttm timestamp,
        primary key (date_hour)
    );


-- dummy data --
insert 
	into 
		tb_user 
		(user_id, user_pw, user_nm, user_type) 
	values 
		('system', '$2a$10$EyaDJCbSfYQn0qYjw7lc0eyY2WVgYcBEzdMRG1m/MMvOJw9GfYDqe', '시스템', 'S');
		
insert 
	into 
		tb_user 
		(user_id, user_pw, user_nm, user_type) 
	values 
		('admin', '$2a$10$EyaDJCbSfYQn0qYjw7lc0eyY2WVgYcBEzdMRG1m/MMvOJw9GfYDqe', '유관리', 'A');
		
insert 
	into 
		tb_user 
		(user_id, user_pw, user_nm, user_type) 
	values 
		('user', '$2a$10$EyaDJCbSfYQn0qYjw7lc0eyY2WVgYcBEzdMRG1m/MMvOJw9GfYDqe', '유사용', 'U');