package com.project.service.reddit;

import com.project.helper.exception.ResourceNotFoundException;
import com.project.helper.payload.email.Email;
import com.project.helper.payload.reddit.CommentDto;
import com.project.model.reddit.Comment;
import com.project.model.reddit.Post;
import com.project.model.user.User;
import com.project.repository.reddit.CommentRepository;
import com.project.repository.reddit.PostRepository;
import com.project.repository.user.UserRepository;
import com.project.service.mail.MailContentBuilder;
import com.project.service.mail.MailService;
import com.project.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService {
    //repositories
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    //services
    private final UserService userService;
    private final MailService mailService;
    //beans
    private final ModelMapper mapper;
    private final MailContentBuilder mailContentBuilder;

    //create a comment on the post with logged-in user, and send a notification email to the post owner
    @Transactional
    public CommentDto save(CommentDto commentDto) {
        User user = userService.currentUser();
        Post post = postRepo.findById(commentDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "PostID", String.valueOf(commentDto.getPostId())));
        Comment comment = mapper.map(commentDto, Comment.class);
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(new Date());
        sendNotificationEmail(commentDto, user);
        return mapper.map(commentRepo.save(comment), CommentDto.class);
    }

    private void sendNotificationEmail(CommentDto commentDto, User user) {
        String POST_URL = "http://localhost:8080/api/v1/post/" + commentDto.getPostId();
        String body = mailContentBuilder.build(user.getUsername() + " has commented a post on your post. Come reply to the user! " + POST_URL);
        mailService.sendEmail(new Email(
                "Your post received a comment!",
                user.getEmail(),
                body));
    }

    //find all comments by post id
    @Transactional
    public List<CommentDto> findAllByPostId(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "PostID", String.valueOf(postId)));
        return commentRepo.findByPost(post)
                .stream().map((comment ->
                        mapper.map(comment, CommentDto.class))).collect(toList());
    }

    //find all comments by username
    @Transactional
    public List<CommentDto> findAllByUsername(String username) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Username", username));
        return commentRepo.findByUser(user)
                .stream().map(comment ->
                        mapper.map(comment, CommentDto.class)).collect(toList());
    }
}