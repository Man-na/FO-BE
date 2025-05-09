package com.manna.fobe.matching.repository;

import com.manna.fobe.matching.entity.InvitationLink;
import com.manna.fobe.matching.entity.MatchingResult;
import com.manna.fobe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationLinkRepository extends JpaRepository<InvitationLink, Long> {
    List<InvitationLink> findByMatchingResult(MatchingResult matchingResult);

    List<InvitationLink> findByCreatedBy(User createdBy);

    Optional<InvitationLink> findByLinkCode(String linkCode);

    List<InvitationLink> findByIsActiveTrue();
}