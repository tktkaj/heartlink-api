package com.ss.heartlinkapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.heartlinkapi.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
	boolean existsByPhone(String phone);
	boolean existsByLoginId(String loginId);
	UserEntity findByLoginId(String loginId);
	UserEntity findByPhone(String phone);
	// 커플코드로 유저 검색
	UserEntity findByCoupleCode(String code);
}
