package com.project.helper.payload.reddit;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class SubredditDto implements Serializable {
    private Long subredditId;
    @NotEmpty
    @Size(min = 3, message = "Community name must be greater than 3 characters!")
    private String name;
    private String description;
    private String createdAt;
    private int numberOfPosts = 0;
}