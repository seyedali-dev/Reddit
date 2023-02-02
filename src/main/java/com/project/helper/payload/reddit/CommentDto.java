package com.project.helper.payload.reddit;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Data
public class CommentDto implements Serializable {
    private Long commentId;

    private @NotEmpty String text;
    private Date createdAt;
    private Long postId;
}