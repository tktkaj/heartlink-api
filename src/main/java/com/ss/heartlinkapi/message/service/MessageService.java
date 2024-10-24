package com.ss.heartlinkapi.message.service;

import com.ss.heartlinkapi.block.entity.BlockEntity;
import com.ss.heartlinkapi.block.repository.BlockRepository;
import com.ss.heartlinkapi.message.dto.BlockUserCheckDTO;
import com.ss.heartlinkapi.message.dto.ChatMsgListDTO;
import com.ss.heartlinkapi.message.entity.MessageEntity;
import com.ss.heartlinkapi.message.repository.MessageRepository;
import com.ss.heartlinkapi.user.entity.UserEntity;
import com.ss.heartlinkapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    /*
    * 사용자들의 대화내역은 채팅방 번호 즉 msgRoomId를 기본키로 해서 저장되므로
    * 이를 기준으로 메세지를 list형태에 저장하여 가져온다.
    */
    public List<ChatMsgListDTO> getAllChatMessage(Long msgRoomId) {

        List<ChatMsgListDTO> list = new ArrayList<>();
        
        List<MessageEntity> messageEntities = messageRepository.findByMsgRoomId(msgRoomId);

        for (MessageEntity entity : messageEntities) {

            Long senderId = entity.getSenderId();
            String content = entity.getContent();
            String emoji = entity.getEmoji();
            String imgUrl = "hello.jpg";
            LocalDateTime lastMessageTime = entity.getCreatedAt();
            boolean isRead = entity.isRead();

            ChatMsgListDTO chatMsgListDTO = ChatMsgListDTO.builder()
                    .msgRoomId(msgRoomId)
                    .senderId(senderId)
                    .content(content)
                    .emoji(emoji)
                    .lastMessageTime(lastMessageTime)
                    .imageUrl(imgUrl)
                    .isRead(isRead)
                    .build();

            list.add(chatMsgListDTO);
        }
        return list;
    }

//    사용자가 보낸 채팅을 저장
    public void saveChatMessage(ChatMsgListDTO chatMsgListDTO) {

        MessageEntity messageEntity = new MessageEntity();
        
        messageEntity.setMsgRoomId(chatMsgListDTO.getMsgRoomId());
        messageEntity.setSenderId(chatMsgListDTO.getSenderId());
        messageEntity.setContent(chatMsgListDTO.getContent());
        messageEntity.setEmoji(chatMsgListDTO.getEmoji());
        messageEntity.setImgUrl(chatMsgListDTO.getImageUrl());
        messageEntity.setRead(chatMsgListDTO.isRead());
        messageEntity.setCreatedAt(chatMsgListDTO.getLastMessageTime());

        messageRepository.save(messageEntity);
    }


    public boolean blockMessage(BlockUserCheckDTO blockUserCheckDTO) {

//        받아온 userid, blockUserId를 엔티티로 변환
        UserEntity user = userRepository.findById(blockUserCheckDTO.getUserId()).get();
        UserEntity blockUser = userRepository.findById(blockUserCheckDTO.getBlockUserId()).get();

//        blockUserId와 UserId에 맞는 튜플이 있다면 반환하기
        if(blockRepository.findByBlockedIdAndBlockerId(blockUser, user)!=null)
            return true;

        return false;

    }
}

