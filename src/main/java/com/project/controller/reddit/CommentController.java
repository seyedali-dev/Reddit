package com.project.controller.reddit;

import com.project.helper.payload.reddit.CommentDto;
import com.project.service.reddit.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //create a comment
    @PostMapping
    public ResponseEntity<CommentDto> save(@Valid @RequestBody CommentDto commentDto) {
        return new ResponseEntity<>(commentService.save(commentDto), HttpStatus.CREATED);
    }

    //find all comments by post
    @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentDto>> findAllByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findAllByPostId(postId));
    }

    //find all comments by username
    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<CommentDto>> findAllByUsername(@PathVariable String username) {
        return ResponseEntity.ok(commentService.findAllByUsername(username));
    }
}