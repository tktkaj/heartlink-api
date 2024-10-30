package com.ss.heartlinkapi.search.service;

import com.ss.heartlinkapi.elasticSearch.service.DeepLService;
import com.ss.heartlinkapi.elasticSearch.service.ElasticService;
import com.ss.heartlinkapi.linktag.entity.LinkTagEntity;
import com.ss.heartlinkapi.linktag.repository.LinkTagRepository;
import com.ss.heartlinkapi.login.dto.CustomUserDetails;
import com.ss.heartlinkapi.post.entity.PostEntity;
import com.ss.heartlinkapi.post.entity.PostFileEntity;
import com.ss.heartlinkapi.post.repository.PostFileRepository;
import com.ss.heartlinkapi.post.repository.PostRepository;
import com.ss.heartlinkapi.search.entity.SearchHistoryEntity;
import com.ss.heartlinkapi.search.repository.SearchRepository;
import com.ss.heartlinkapi.user.entity.UserEntity;
import com.ss.heartlinkapi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LinkTagRepository linkTagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private ElasticService elasticService;

    @Autowired
    private DeepLService deepLService;

    @Autowired
    private PostFileRepository postFileRepository;

    // 유저 아이디 검색
    @Transactional
    public UserEntity searchByUserId(String keyword, Long userId) {
        keyword = keyword.trim().substring(1);

        UserEntity findUser = userRepository.findByLoginId(keyword);
        UserEntity user = userRepository.findById(userId).orElse(null);

        if(user == null || findUser == null) {
            return null;
        }

        SearchHistoryEntity searchHistory = searchRepository.findByKeywordAndTypeAndUserId(keyword, "id", user);

        if(searchHistory != null) {
            searchHistory.setUpdatedAt(LocalDateTime.now());
            searchRepository.save(searchHistory);
        } else {
            SearchHistoryEntity searchHistoryEntity = new SearchHistoryEntity();
            searchHistoryEntity.setUserId(user);
            searchHistoryEntity.setKeyword(user.getLoginId());
            searchHistoryEntity.setType("id");
            searchHistoryEntity.setCreatedAt(LocalDateTime.now());
            SearchHistoryEntity result = searchRepository.save(searchHistoryEntity);
            System.out.println("Result : " + result);
        }
        return findUser;
    }

    // 태그명으로 태그 검색
    public LinkTagEntity searchByTag(String keyword, Long userId) {
        keyword = keyword.trim().substring(1);

        LinkTagEntity findTag = linkTagRepository.findByKeywordContains(keyword);
        UserEntity user = userRepository.findById(userId).orElse(null);

        if(user == null || findTag == null) {
            return null;
        }

        SearchHistoryEntity searchHistory = searchRepository.findByKeywordAndTypeAndUserId(keyword, "tag", user);

        if(searchHistory != null) {
            searchHistory.setUpdatedAt(LocalDateTime.now());
            searchRepository.save(searchHistory);
        } else {
            SearchHistoryEntity searchHistoryEntity = new SearchHistoryEntity();
            searchHistoryEntity.setUserId(user);
            searchHistoryEntity.setKeyword(findTag.getKeyword());
            searchHistoryEntity.setType("tag");
            searchHistoryEntity.setCreatedAt(LocalDateTime.now());
            SearchHistoryEntity result = searchRepository.save(searchHistoryEntity);
            System.out.println("Result : " + result);
        }
        return findTag;
    }

    // 키워드로 게시글 검색
    @Transactional
    public List<PostEntity> searchByPost(String keyword, Long userId) {
        keyword = keyword.trim();
        List<PostEntity> findPost = postRepository.findAllByContentIgnoreCaseContaining(keyword);
        UserEntity user = userRepository.findById(userId).orElse(null);

        if(user == null || findPost == null) {
            return Collections.emptyList();
        }

        SearchHistoryEntity searchHistory = searchRepository.findByKeywordAndTypeAndUserId(keyword, "content", user);
        SearchHistoryEntity elasticEntity = new SearchHistoryEntity();
        String deepLResult;

        if(searchHistory != null) {
            searchHistory.setUpdatedAt(LocalDateTime.now());
            searchRepository.save(searchHistory);
            deepLResult = deepLService.translate(searchHistory.getKeyword());
        } else {
            searchHistory = new SearchHistoryEntity();
            searchHistory.setUserId(user);
            searchHistory.setKeyword(keyword);
            searchHistory.setType("content");
            searchHistory.setCreatedAt(LocalDateTime.now());
            SearchHistoryEntity result = searchRepository.save(searchHistory);
            deepLResult = deepLService.translate(searchHistory.getKeyword());
        }

        // Elastic용 entity
        elasticEntity.setKeyword(deepLResult);
        elasticEntity.setType(searchHistory.getType());
        elasticEntity.setCreatedAt(searchHistory.getUpdatedAt()==null?searchHistory.getCreatedAt():searchHistory.getUpdatedAt());
        elasticEntity.setUpdatedAt(null);
        elasticEntity.setSearchHistoryId(searchHistory.getSearchHistoryId());
        elasticEntity.setUserId(user);
        elasticService.addOrUpdateHistory(elasticEntity);
        return findPost;
    }

    // 유저 아이디로 검색기록 가져오기
    public List<Map<String, Object>> findHistoryByUserId(Long userId) {
       UserEntity user = userRepository.findById(userId).orElse(null);
       List<SearchHistoryEntity> history = searchRepository.findByUserId(user);
       if(history.isEmpty()) {
           return null;
       }
       List<Map<String, Object>> historyList = new ArrayList<>();
       int size = history.size()<=5 ? history.size() : 5;
       for(int i=0; i<size; i++) {
           Map<String, Object> map = new HashMap<>();
           map.put("type", history.get(i).getType());
           map.put("keyword", history.get(i).getKeyword());
           if(history.get(i).getUpdatedAt() != null) {
               map.put("date", history.get(i).getUpdatedAt());
           } else {
               map.put("date", history.get(i).getCreatedAt());
           }
           historyList.add(map);
       }

       return historyList;
    }

    // 검색창 옆에 띄울 게시글 목록 가져오기
    // 좋아요 많은 순+검색기록 관련 순으로 섞고 나서 연관없는 게시글 최근순으로 가져오기
    public List<Map<String, Object>> getPost(CustomUserDetails user) {
        System.out.println("1");
        List<PostEntity> manyLikePostList = postRepository.findAllByOrderByLikeCountDesc(); // 좋아요 많은 순으로 게시글 목록 조회
        System.out.println("2");
        List<SearchHistoryEntity> searchHistoryList = searchRepository.findByUserId(user.getUserEntity()); // 유저의 검색기록 리스트 조회
        System.out.println("23232323"+searchHistoryList);
        System.out.println("3");
        List<PostEntity> searchPostList = new ArrayList<>(); // 검색기록 키워드가 포함된 피드 목록 생성
        for(SearchHistoryEntity searchHistory : searchHistoryList) { // 검색기록 리스트 순회
            if(searchHistory.getType().equals("content")) { // 만약 검색기록의 타입이 content일 때
                System.out.println("4");
                // 해당 키워드로 게시글 리스트 모두 조회
                List<PostEntity> keywordFindPostList = postRepository.findAllByContentIgnoreCaseContaining(searchHistory.getKeyword());
                for(PostEntity post : keywordFindPostList) { // 게시글을 검색기록 키워드가 포함된 목록에 저장
                    System.out.println("5");
                    searchPostList.add(post);
                    System.out.println("6666666666666");
                }
            }
        }

//        List<PostEntity> mixPostList = mixPostList(manyLikePostList, searchPostList);
        List<PostEntity> mixPostList = manyLikePostList;
        System.out.println("1313131313"+mixPostList);
        List<PostEntity> mixPostList2 = searchPostList;
        System.out.println("2424242424"+mixPostList2);
        System.out.println("어딘데대체");

        List<Map<String, Object>> postMap = new ArrayList<>();
        System.out.println("7"+postMap);

        for(PostEntity post : mixPostList) {
            System.out.println("8"+post);
            Map<String, Object> map = new HashMap<>();
            System.out.println(postFileRepository.findByPostId(post.getPostId()));
//            PostFileEntity file = postFileRepository.findByPostId(post.getPostId()).get(0);
            System.out.println("9");
            map.put("postId",post.getPostId());
//            map.put("postImgUrl",file.getFileUrl());
            map.put("likeCount",post.getLikeCount());
            map.put("commentCount",post.getCommentCount());
            postMap.add(map);
        }

        System.out.println("10");
        return postMap;

    }

//    // 게시글 섞기 (좋아요 많은 순 + 검색기록에 따른 게시글 리스트)
//    private List<PostEntity> mixPostList(List<PostEntity> manyLikePostList, List<PostEntity> searchPostList) {
//        System.out.println("mixPostList 메서등드으ㅡ으 11");
//        if(searchPostList.isEmpty()) { // 검색기록에 따른 피드 내용이 없을 경우
//            System.out.println("mixPostList 메서등드으ㅡ으 22");
//            System.out.println("mixPostList 메서등드으ㅡ으 22"+manyLikePostList);
//            return manyLikePostList;
//        }
//
//        System.out.println("mixPostList 메서등드으ㅡ으 33");
//        Set<PostEntity> mergedSet = new LinkedHashSet<>(searchPostList);
//
//        System.out.println("mixPostList 메서등드으ㅡ으 44");
//        for (PostEntity post : manyLikePostList) {
//            System.out.println("mixPostList 메서등드으ㅡ으 55");
//            mergedSet.add(post); // 검색기록 순의 글 목록에 좋아요 순 글 목록을 중복없이 합치기
//        }
//
//        System.out.println("mixPostList 메서등드으ㅡ으 66");
//        return new ArrayList<>(mergedSet);
//    }
}
