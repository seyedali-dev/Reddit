package com.project.service.reddit;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.project.helper.constants.Constants;
import com.project.helper.exception.ResourceNotFoundException;
import com.project.helper.payload.reddit.PostDto;
import com.project.model.reddit.Post;
import com.project.model.reddit.Subreddit;
import com.project.model.user.User;
import com.project.repository.reddit.CommentRepository;
import com.project.repository.reddit.PostRepository;
import com.project.repository.reddit.SubredditRepository;
import com.project.repository.user.UserRepository;
import com.project.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {
    //repositories
    private final PostRepository postRepo;
    private final SubredditRepository subredditRepo;
    private final UserRepository userRepo;
    private final CommentRepository commentRepo;
    //service
    private final UserService userService;
    //bean
    private final ModelMapper mapper;

    //create a post
    @Transactional
    public void save(PostDto postDto) {
        Post post = mapper.map(postDto, Post.class);
        post.setUser(userService.currentUser());
        post.setSubreddit(mapper.map(postDto.getSubreddit(), Subreddit.class));
        post.setCreatedAt(Instant.now());
        post.getSubreddit().setNumberOfPosts(++Constants.postCount);
        log.info("-----------------> "+post.getSubreddit().getNumberOfPosts());
        postRepo.save(post);

        //TODO: subreddit repository is saving the post counts but other data are getting null.
        subredditRepo.save(post.getSubreddit());

        postDto.setCommentCount(commentRepo.findByPost(post).size());
        postDto.setDuration(TimeAgo.using(post.getCreatedAt().toEpochMilli()));
    }

    //get all posts
    @Transactional(readOnly = true)
    public List<PostDto> findAll() {
        return postRepo.findAll().stream().map((post -> mapper.map(post, PostDto.class))).collect(Collectors.toList());
    }

    //get post by id
    @Transactional(readOnly = true)
    public PostDto findById(long postId) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "PostID", String.valueOf(postId)));
        return mapper.map(post, PostDto.class);
    }

    //get post by subreddit id
    @Transactional(readOnly = true)
    public List<PostDto> findPostBySubredditId(long subredditId) {
        Subreddit subreddit = subredditRepo.findById(subredditId).orElseThrow(() -> new ResourceNotFoundException("Subreddit", "SubredditID", String.valueOf(subredditId)));
        return postRepo.findAllBySubreddit(subreddit).stream().map((post) -> mapper.map(post, PostDto.class)).collect(Collectors.toList());
    }

    //get post by username
    @Transactional(readOnly = true)
    public List<PostDto> findPostByUsername(String username) {
        User user = userRepo.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "Username", username));
        return postRepo.findAllByUser(user).stream().map((post) -> mapper.map(post, PostDto.class)).collect(Collectors.toList());
    }
}