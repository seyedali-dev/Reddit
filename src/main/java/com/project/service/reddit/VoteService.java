package com.project.service.reddit;

import com.project.helper.exception.RedditException;
import com.project.helper.exception.ResourceNotFoundException;
import com.project.helper.payload.VoteDto;
import com.project.model.reddit.Post;
import com.project.model.reddit.Vote;
import com.project.repository.reddit.PostRepository;
import com.project.repository.reddit.VoteRepository;
import com.project.service.user.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.helper.enums.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {
    //repositories
    private final VoteRepository voteRepo;
    private final PostRepository postRepo;
    //service
    private final UserService userService;
    //bean
    private final ModelMapper mapper;

    //submit vote on a post
    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepo.findById(voteDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "PostID", String.valueOf(voteDto.getPostId())));
        Optional<Vote> voteByPostAndUser = voteRepo.findTopByPostAndUserOrderByVoteIdDesc(post, userService.currentUser());

        //if user has submitted vote, user shouldn't be able to vote again

        /*The issue in the code is that it's checking whether the user has already voted or not after saving the vote.
        Instead, the check should be done before saving the vote to prevent the duplicate vote from being inserted into the database.*/
        if (voteByPostAndUser.isEmpty() || !voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            if (UPVOTE.equals(voteDto.getVoteType()))
                post.setVoteCount(post.getVoteCount() + 1);
            else
                post.setVoteCount(post.getVoteCount() - 1);
            voteRepo.save(mapper.map(voteDto, Vote.class));
            postRepo.save(post);
        } else {
            throw new RedditException("You have already " + voteDto.getVoteType() + "'d for this post!");
        }
    }
}