package com.ss.heartlinkapi.message.entity;


import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "message_room")
public class MessageRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user1_id")
    private Long user1Id;
    @Column(name = "user2_id")
    private Long user2Id;
    @Column(name = "type")
    private String type;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
