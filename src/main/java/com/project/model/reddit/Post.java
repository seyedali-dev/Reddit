package com.project.model.reddit;

import com.project.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private @NotEmpty String postName;
    private @Nullable String url;
    private @Lob String description;
    private int voteCount;
    private int commentCount;
    private Instant createdAt;

    /*relationship*/
    @ManyToOne(fetch = FetchType.EAGER)
    private Subreddit subreddit;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    /*end of relationship*/
}