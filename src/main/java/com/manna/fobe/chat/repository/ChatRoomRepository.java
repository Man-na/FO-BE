package com.manna.fobe.chat.repository;

import com.manna.fobe.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    Page<ChatRoom> findByUserId(int userId, Pageable pageable);
}