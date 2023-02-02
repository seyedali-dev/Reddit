package com.project.controller.reddit;

import com.project.helper.payload.ApiResponse;
import com.project.helper.payload.reddit.PostDto;
import com.project.service.reddit.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/v1/post")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    //create a post
    @PostMapping
    public ResponseEntity<ApiResponse> savePost(@Valid @RequestBody PostDto postDto) {
        postService.save(postDto);
        return new ResponseEntity<>(new ApiResponse("Post created successfully!", true), HttpStatus.CREATED);
    }

    //get all the posts
    @GetMapping
    public ResponseEntity<List<PostDto>> findAll() {
        return status(HttpStatus.OK).body(postService.findAll());
    }

    //get post by id
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> findById(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.findById(postId), HttpStatus.OK);
    }

    @GetMapping("/by-subreddit/{subredditId}")
    public ResponseEntity<List<PostDto>> findBySubredditId(@PathVariable Long subredditId) {
        return new ResponseEntity<>(postService.findPostBySubredditId(subredditId), HttpStatus.OK);
    }

    //get all posts by username
    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<PostDto>> findByUsername(@PathVariable String username) {
        return new ResponseEntity<>(postService.findPostByUsername(username), HttpStatus.OK);
    }
}