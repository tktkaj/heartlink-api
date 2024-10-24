package com.ss.heartlinkapi.linkmatch.controller;

import com.ss.heartlinkapi.couple.entity.CoupleEntity;
import com.ss.heartlinkapi.couple.service.CoupleService;
import com.ss.heartlinkapi.linkmatch.dto.MatchAnswer;
import com.ss.heartlinkapi.linkmatch.service.CoupleMatchService;
import com.ss.heartlinkapi.linkmatch.entity.LinkMatchAnswerEntity;
import com.ss.heartlinkapi.linkmatch.entity.LinkMatchEntity;
import com.ss.heartlinkapi.linkmatch.service.CoupleMatchStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/couple")
public class CoupleMatchController {

    @Autowired
    private CoupleMatchService coupleMatchService;

    @Autowired
    private CoupleService coupleService;

    @Autowired
    private CoupleMatchStatisticsService statisticsService;

    // 커플 매치 질문 조회
    @GetMapping("/missionmatch/questions")
    public ResponseEntity<?> getMatchQuestion() {
        // 오류 500 검사
        try {
            LinkMatchEntity result = coupleMatchService.getMatchQuestion();
            // 오류 404 검사
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 커플 매치 답변 저장
    @PostMapping("/missionmatch/questions/choose")
    public ResponseEntity<?> matchChoose(@RequestBody MatchAnswer matchAnswer) {
        // 오류 500 검사
        try {
            // 오류 400 검사
            if (matchAnswer == null || matchAnswer.getQuestionId() == null
                    || matchAnswer.getSelectedOption() > 1 || matchAnswer.getSelectedOption() < 0 || matchAnswer.getUserId() == null) {
                return ResponseEntity.badRequest().build();
            }
            LinkMatchAnswerEntity result = coupleMatchService.answerSave(matchAnswer);
            // 오류 404 검사
            if (result != null) {
                // 매칭 성공 여부 확인
                int matchResult = coupleMatchService.checkTodayMatching(result.getCoupleId().getCoupleId());
                if(matchResult == 1) { // 매칭 성공
                    // 매칭 성공 시 커플엔티티 카운트 수 증가
                    int countUpresult = coupleService.matchCountUp(result.getCoupleId().getCoupleId());
                    if(countUpresult == 1) {
                        // 카운트 증가 성공하여 1 반환
                        return ResponseEntity.status(HttpStatus.OK).body(countUpresult);
                    } else {
                        // 카운트 증가 실패
                        return ResponseEntity.ok("매칭 성공했으나 카운트 증가 실패");
                    }
                } else if(matchResult == 0) { // 매칭 실패
                    // 매칭 실패하여 0 반환
                    return ResponseEntity.status(HttpStatus.OK).body(matchResult);
                } else {
                    // 상대방이 선택지를 고르지 않아 2 반환
                    return ResponseEntity.status(HttpStatus.OK).body(matchResult);
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

    }

    // 매치 답변 내역 조회
    @GetMapping("/missionmatch/answerList/{coupleId}")
    public ResponseEntity<?> getMatchAnswerList(@PathVariable Long coupleId) {
        // 오류 500 검사
        try{
            // 오류 400 검사
            if(coupleId == null) {
                return ResponseEntity.badRequest().build();
            }
            CoupleEntity couple = coupleService.findById(coupleId);
        List<LinkMatchAnswerEntity> answerList = coupleMatchService.findAnswerListByCoupleId(couple);
        return ResponseEntity.ok(answerList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 통계 - 일일 매치 통계 조회(일일 매치 답변 별 성별 비율 통계, 일일 매칭된 커플 퍼센트, 월별 매칭 횟수 조회)
    @GetMapping("/statistics/dailyMatch/{coupleId}")
    public ResponseEntity<?> getStatisticsDailyMatchById(@PathVariable Long coupleId) {
        try{
            Date todayDate = new Date();
            LocalDate today = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LinkMatchEntity todayMatch = statisticsService.findMatchByDate(today);

            if(todayMatch == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> rateResult = statisticsService.matchRate(todayMatch, coupleId);

            if(rateResult == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(rateResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

    }

}