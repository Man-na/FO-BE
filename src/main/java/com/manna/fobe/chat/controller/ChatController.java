package com.manna.fobe.chat.controller;

import com.manna.fobe.chat.dto.CreateChatRoomDto;
import com.manna.fobe.chat.dto.JoinChatRoomDto;
import com.manna.fobe.chat.entity.ChatMessage;
import com.manna.fobe.chat.entity.ChatRoom;
import com.manna.fobe.chat.service.ChatService;
import com.manna.fobe.common.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat-room")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody CreateChatRoomDto createChatRoomDto, @RequestAttribute("userId") int userId) {
        ChatRoom createdChatRoom = chatService.createChatRoom(createChatRoomDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdChatRoom)
                .statusCode(201)
                .resultMessage("채팅방 추가 성공")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/chat-rooms")
    public ResponseEntity<ResponseMessage> getMyChatRooms(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestAttribute("userId") int userId
    ) {
        Page<ChatRoom> chatRooms = chatService.getMyChatRooms(PageRequest.of(page - 1, size), userId);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(chatRooms)
                .statusCode(200)
                .resultMessage("채팅방 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/join-chat-room")
    public ResponseEntity<ResponseMessage> joinChatRoom(
            @RequestBody JoinChatRoomDto joinChatRoomDto,
            @RequestAttribute("userId") int userId
    ) {
        chatService.joinChatRoom(joinChatRoomDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("채팅방 참가 성공")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<ResponseMessage> getChatMessages(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @PathVariable("chatRoomId") int chatRoomId) {
        Page<ChatMessage> chatMessages = chatService.getChatMessagesByRoomId(PageRequest.of(page - 1, size), chatRoomId);

        ResponseMessage response = ResponseMessage.builder()
                .data(chatMessages)
                .statusCode(200)
                .resultMessage("채팅 목록 조회 성공")
                .build();

        return ResponseEntity.ok(response);
    }
}