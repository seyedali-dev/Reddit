package com.project.helper.payload.reddit;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.Instant;

@Data
public class PostDto implements Serializable {
    private Long postId;
    private @NotEmpty String postName;
    private @NotEmpty String url;
    private String description;
    private Instant createdAt;
    private SubredditDto subreddit;
//    private @NotEmpty UserDto user;

    //for showing the comments and votes and posts
    private Integer commentCount;
    private Integer voteCount;
    private String duration; //for showing 'a certain time ago'
}