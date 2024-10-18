package com.ss.heartlinkapi.mission.controller;

import com.ss.heartlinkapi.mission.dto.LinkMissionDTO;
import com.ss.heartlinkapi.mission.entity.LinkMissionEntity;
import com.ss.heartlinkapi.mission.service.CoupleMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/couple")
public class CoupleMissionController {

    @Autowired
    private CoupleMissionService MissionService;

    // 매월 미션태그 조회
    @GetMapping("/missionslink")
    public ResponseEntity<?> selectMissionTags(@RequestParam(value = "year", required = false) Integer year, @RequestParam(value = "month", required = false) Integer month){
        try{

            // 넘어온 날짜가 없을 경우 디폴트값 현재
            if(year == null){
                 year = LocalDate.now().getYear();
             }
            if(month == null){
                month = LocalDate.now().getMonthValue();
            }

            List<LinkMissionEntity> missionList = MissionService.findMissionByYearMonth(year, month);

            if(missionList == null || missionList.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(missionList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}