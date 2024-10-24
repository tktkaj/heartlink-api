package com.ss.heartlinkapi.search.controller;

import com.ss.heartlinkapi.linktag.entity.LinkTagEntity;
import com.ss.heartlinkapi.post.entity.PostEntity;
import com.ss.heartlinkapi.search.entity.SearchHistoryEntity;
import com.ss.heartlinkapi.search.service.SearchService;
import com.ss.heartlinkapi.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/history")
    public ResponseEntity<?> searchHistory(@RequestParam Long userId){
        List<SearchHistoryEntity> historyList = searchService.findHistoryByUserId(userId);
        return null;
    }

    @GetMapping("/keyword")
    public ResponseEntity<?> search(@RequestParam String keyword, @RequestParam Long userId) {
        try {

            if(keyword == null || keyword.isEmpty() || userId == null) {
                return ResponseEntity.badRequest().body(null);
            }

            if (keyword.startsWith("@")) {
                System.out.println(keyword);
                UserEntity user = searchService.searchByUserId(keyword, userId);
                if(user == null) {
                    return ResponseEntity.ok("검색 결과가 없습니다.");
                }
                return ResponseEntity.ok(user.getUserId());
            } else if (keyword.startsWith("&")) {
                LinkTagEntity tag = searchService.searchByTag(keyword, userId);
                if(tag == null) {
                    return ResponseEntity.ok("검색 결과가 없습니다.");
                }
                return ResponseEntity.ok(tag.getId());
            } else {
                System.out.println("키워드 : "+keyword);
                List<PostEntity> post = searchService.searchByPost(keyword, userId);
                if(post == null) {
                    return ResponseEntity.ok("검색 결과가 없습니다.");
                }
                List<Map<String, Object>> postList = new ArrayList<>();
                for(int i=0; i<post.size(); i++) {
                    Map<String, Object> postMap = new HashMap<>();
                    for(int j=0; j<post.size(); j++) {
                        postMap.put("id", post.get(i).getPostId());
                        postMap.put("content", post.get(i).getContent());
                    }
                    postList.add(i, postMap);
                }

                return ResponseEntity.ok(postList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
