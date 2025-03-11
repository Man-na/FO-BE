package com.manna.fobe.chat.repository;

import com.manna.fobe.chat.entity.ChatRoomUser;
import com.manna.fobe.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Integer> {
    Page<ChatRoomUser> findByUser(User user, Pageable pageable);
}