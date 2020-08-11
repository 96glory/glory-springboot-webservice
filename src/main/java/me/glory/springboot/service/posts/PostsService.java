package me.glory.springboot.service.posts;

import lombok.RequiredArgsConstructor;
import me.glory.springboot.domain.posts.PostsRepository;
import me.glory.springboot.web.dto.PostsSaveRequestDto;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto){
        return postsRepository.save(requestDto.toEntity()).getId();
    }
}
