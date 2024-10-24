package com.ss.heartlinkapi.couple.service;

import com.ss.heartlinkapi.couple.dto.CoupleCode;
import com.ss.heartlinkapi.couple.entity.CoupleEntity;
import com.ss.heartlinkapi.couple.repository.CoupleRepository;
import com.ss.heartlinkapi.user.entity.UserEntity;
import com.ss.heartlinkapi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Service
public class CoupleService {
    @Autowired
    private CoupleRepository coupleRepository;

    @Autowired
    private UserRepository userRepository;

    // 유저아이디로 커플아이디 조회
    public CoupleEntity findByUser1_IdOrUser2_Id(Long id) {
        return coupleRepository.findCoupleByUserId(id);
    }

    // 커플 아이디로 커플 객체 반환
    public CoupleEntity findById(Long coupleId) {
        return coupleRepository.findById(coupleId).orElse(null);
    }

    // 디데이 날짜 추가/수정
    public CoupleEntity setAnniversary(CoupleEntity couple) {
        return coupleRepository.save(couple);
    }

    // 커플 매칭 카운트 증가
    public int matchCountUp(Long coupleId) {
        return coupleRepository.matchCountUp(coupleId);
    }

    // 커플 연결
    @Transactional
    public CoupleEntity coupleCodeMatch(UserEntity user1, CoupleCode code) {
        String coupleCode = code.getCode().toUpperCase().trim();
        UserEntity user2 = userRepository.findByCoupleCode(coupleCode);

        CoupleEntity newCouple = new CoupleEntity();
        newCouple.setUser1(user1);
        newCouple.setUser2(user2);
        newCouple.setCreatedAt(new Timestamp(new Date().getTime()));
        return coupleRepository.save(newCouple);
    }

    // 커플코드 연결 전 확인
    public int codeCheck(CoupleCode code){
        String coupleCode = code.getCode().toUpperCase().trim();
        UserEntity user = userRepository.findByCoupleCode(coupleCode);
        if(user == null) {
            return 1; // 존재하지 않는 커플코드
        }
        CoupleEntity couple = coupleRepository.findCoupleByUserId(user.getUserId());
        if(couple != null) {
            return 2; // 이미 상대가 커플임
        }
        return 3; // 정상
    }

    // 커플 해지일 설정
    public CoupleEntity setBreakDate(CoupleEntity couple) {
        LocalDate breakDate = LocalDate.now().plusMonths(3);
        couple.setBreakupDate(breakDate);
        return coupleRepository.save(couple);
    }

    // 커플 해지일 삭제
    public CoupleEntity deleteBreakDate(CoupleEntity couple) {
        couple.setBreakupDate(null);
        return coupleRepository.save(couple);
    }

    // 커플 해지 유예기간 지나고 최종 해지
    public boolean finalUnlinkCouple(CoupleEntity couple) {
        if(couple.getBreakupDate().isBefore(LocalDate.now())) {
            UserEntity user1 = couple.getUser1();
            UserEntity user2 = couple.getUser2();
            System.out.println("111111 : "+user1);
            System.out.println("222222 : "+user2);

            coupleRepository.delete(couple);

            user1.setCoupleCode(generateRandomCode());
            user2.setCoupleCode(generateRandomCode());

            userRepository.save(user1);
            userRepository.save(user2);

            return true;
        } else {
            return false;
        }

    }

    // 커플 코드 랜덤값 적용
    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int CODE_LENGTH = 6;

    public String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    // 커플 해지 유예기간 없이 즉시 해지
    public void finalNowUnlinkCouple(CoupleEntity couple) {
        UserEntity user1 = couple.getUser1();
        UserEntity user2 = couple.getUser2();

        coupleRepository.delete(couple);

        user1.setCoupleCode(generateRandomCode());
        user2.setCoupleCode(generateRandomCode());

        userRepository.save(user1);
        userRepository.save(user2);
    }
    
    // 내 커플의 아이디 가져오기
    public UserEntity getCouplePartner(Long userId) {
    	CoupleEntity couple = coupleRepository.findCoupleByUserId(userId);
    	if(couple != null) {
    		if(couple.getUser1().getUserId().equals(userId)) {
    			return couple.getUser2();
    		} else {
    			return couple.getUser1();
    		}
    	}
    	return null;
    }
}
