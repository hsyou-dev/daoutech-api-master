package com.daoutech.api.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daoutech.api.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

}
