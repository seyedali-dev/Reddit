package com.project.repository.reddit;

import com.project.model.reddit.Post;
import com.project.model.reddit.Vote;
import com.project.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    //finding the recent vote which was submitted by the user for this post
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}