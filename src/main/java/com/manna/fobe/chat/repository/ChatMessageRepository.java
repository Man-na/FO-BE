package com.manna.fobe.chat.repository;

import com.manna.fobe.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    Page<ChatMessage> findByChatRoomId(Pageable pageable , int chatRoomId);
}