package com.manna.fobe.chat.service;

import com.manna.fobe.chat.dto.CreateChatRoomDto;
import com.manna.fobe.chat.dto.JoinChatRoomDto;
import com.manna.fobe.chat.entity.ChatMessage;
import com.manna.fobe.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    ChatRoom createChatRoom(CreateChatRoomDto createChatRoomDto, int userId);

    Page<ChatRoom> getMyChatRooms(Pageable pageable, int userId);

    void joinChatRoom(JoinChatRoomDto joinChatRoomDto, int userId);

    Page<ChatMessage> getChatMessagesByRoomId(Pageable pageable, int chatRoomId);
}