package com.project.controller.reddit;

import com.project.helper.payload.reddit.SubredditDto;
import com.project.service.reddit.SubredditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/subreddit")
public class SubredditController {
    private final SubredditService subredditService;

    //create a subreddit
    @PostMapping
    public ResponseEntity<SubredditDto> createSubreddit(@Valid @RequestBody SubredditDto subredditDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subredditService.save(subredditDto));
    }

    //get all subreddit
    @GetMapping
    public ResponseEntity<List<SubredditDto>> findAllSubreddits() {
        return ResponseEntity.ok().body(subredditService.findAll());
    }

    //get a subreddit by id
    @GetMapping("/{id}")
    public ResponseEntity<?> findSubredditById(@PathVariable Long id) {
        return ResponseEntity.ok().body(subredditService.findById(id));
    }
}