package com.project.service.reddit;

import com.project.helper.exception.ResourceNotFoundException;
import com.project.helper.payload.reddit.SubredditDto;
import com.project.model.reddit.Subreddit;
import com.project.model.user.User;
import com.project.repository.reddit.PostRepository;
import com.project.repository.reddit.SubredditRepository;
import com.project.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@AllArgsConstructor
public class SubredditService {
    //repositories
    private final PostRepository postRepo;
    private final SubredditRepository subredditRepo;
    //service
    private final UserService userService;
    //bean
    private final ModelMapper mapper;
    private Date date;

    //create a subreddit community
    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        User user = userService.currentUser();
        Subreddit subreddit = mapper.map(subredditDto, Subreddit.class);
        subreddit.setCreatedAt(simpleDateFormat.format(date));
        subreddit.setUser(user);
        Subreddit saveSubreddit = subredditRepo.save(subreddit);

//        int size = postRepo.findAllBySubreddit(subreddit).size();
//        subredditDto.setNumberOfPosts(size);
//        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<size: "+size);

        return mapper.map(saveSubreddit, SubredditDto.class);
    }

    //find all the subreddits
    @Transactional
    public List<SubredditDto> findAll() {
        return subredditRepo.findAll().stream().map((subreddit -> mapper.map(subreddit, SubredditDto.class))).collect(toList());
    }

    //find a subreddit by subreddit id
    @Transactional
    public SubredditDto findById(long subredditId) {
        Subreddit subreddit = subredditRepo.findById(subredditId).orElseThrow(() -> new ResourceNotFoundException("Subreddit", "SubredditID", String.valueOf(subredditId)));
        return mapper.map(subreddit, SubredditDto.class);
    }
}