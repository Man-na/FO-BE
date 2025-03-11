package com.manna.fobe.chat.service;

import com.manna.fobe.chat.dto.CreateChatRoomDto;
import com.manna.fobe.chat.dto.JoinChatRoomDto;
import com.manna.fobe.chat.entity.ChatRoom;
import com.manna.fobe.chat.entity.ChatRoomUser;
import com.manna.fobe.chat.repository.ChatRoomRepository;
import com.manna.fobe.chat.repository.ChatRoomUserRepository;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ChatRoom createChatRoom(CreateChatRoomDto createChatRoomDto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .title(createChatRoomDto.getTitle())
                .creator(user)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public Page<ChatRoom> getMyChatRooms(Pageable pageable, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return chatRoomUserRepository.findByUser(user, pageable)
                .map(ChatRoomUser::getChatRoom);
    }

    @Transactional
    public void joinChatRoom(JoinChatRoomDto joinChatRoomDto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ChatRoom chatRoom = chatRoomRepository.findById(joinChatRoomDto.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));

        ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();

        chatRoomUserRepository.save(chatRoomUser);
    }
}