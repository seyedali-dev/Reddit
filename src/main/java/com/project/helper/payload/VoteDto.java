package com.project.helper.payload;

import com.project.helper.enums.VoteType;
import lombok.Data;

@Data
public class VoteDto {
    private VoteType voteType;
    private Long postId;
}