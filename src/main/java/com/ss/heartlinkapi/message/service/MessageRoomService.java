package com.ss.heartlinkapi.message.service;

import com.ss.heartlinkapi.message.dto.ApplyMessageDTO;
import com.ss.heartlinkapi.message.dto.ChatMsgListDTO;
import com.ss.heartlinkapi.message.entity.MessageRoomEntity;
import com.ss.heartlinkapi.message.repository.MessageRepository;
import com.ss.heartlinkapi.message.repository.MessageRoomRepository;
import com.ss.heartlinkapi.user.entity.ProfileEntity;
import com.ss.heartlinkapi.user.entity.UserEntity;
import com.ss.heartlinkapi.user.repository.ProfileRepository;
import com.ss.heartlinkapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageRoomService {

    private final MessageRoomRepository messageRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final MessageService messageService;

    public List<Object> getChatUsers(Long userId) {

        List<Object> chatUsers = new ArrayList<>();

        List<MessageRoomEntity> messageRoomEntities = messageRoomRepository.findByUser1IdOrUser2Id(userId, userId);

        log.info("entities : {}", messageRoomEntities);
        for (MessageRoomEntity entity : messageRoomEntities) {

            HashMap<String, Object> chat = new HashMap<>();
            List<ChatMsgListDTO> messages = new ArrayList();

            Long otherUserId = 0L;

//            대화 상대 userId를 확인하는 조건문
            if (Objects.equals(entity.getUser1Id(), userId)) {
                otherUserId = entity.getUser2Id();
            } else {
                otherUserId = entity.getUser1Id();
            }

//            대화 상대 entity 가져오기
            UserEntity chatUserEntity = userRepository.findById(otherUserId).get();

//            대화 상대 유저이름
            String otherLoginId = chatUserEntity.getLoginId();
            chat.put("otherLoginId", otherLoginId);

//            대화 상대 유저 이미지
            ProfileEntity profileEntity = profileRepository.findByUserEntity(chatUserEntity);
            String otherUserImg = profileEntity.getProfile_img();
            chat.put("otherUserImg", otherUserImg);

//            msg_room_id 가져오기
            Long messageRoomId = entity.getId();
            chat.put("msgRoomId", messageRoomId);

//            마지막 메시지 구하기
            String lastMessage = messageRepository.findByMsgRoomIdOrderByCreatedAt(messageRoomId);
            chat.put("lastMessage", lastMessage);

//            로그인 상태 확인
            chat.put("login", true);


            chatUsers.add(chat);
        }

        return chatUsers;
    }

    public void applyMessage(ApplyMessageDTO applyMessageDTO) {

        MessageRoomEntity messageRoomEntity = new MessageRoomEntity();
        messageRoomEntity.setUser1Id(applyMessageDTO.getUserId());
        messageRoomEntity.setUser2Id(applyMessageDTO.getApplyId());
        messageRoomEntity.setCreatedAt(LocalDateTime.now());
//        수락전은 N으로해서 팔로우 된 사람들과의 대화랑 구분
        messageRoomEntity.setType("N");
        messageRoomRepository.save(messageRoomEntity);
    }
//    msgRoomId를 기준으로 방을 삭제
    public void applyRejection(Long msgRoomId) {
        messageRoomRepository.deleteById(msgRoomId);
    }

//    msgRoomId를 기준으로 type을 y로 업데이트
    public void applyAccept(Long msgRoomId) {
        MessageRoomEntity messageRoomEntity = messageRoomRepository.findById(msgRoomId).orElseThrow(() -> new RuntimeException("MsgRoomId not found"));
        messageRoomEntity.setType("Y");
        messageRoomRepository.save(messageRoomEntity);
    }
}
