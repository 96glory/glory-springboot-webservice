package me.glory.springboot.web;

import lombok.RequiredArgsConstructor;
import me.glory.springboot.config.auth.LoginUser;
import me.glory.springboot.config.auth.dto.SessionUser;
import me.glory.springboot.service.posts.PostsService;
import me.glory.springboot.web.dto.PostsListResponseDto;
import me.glory.springboot.web.dto.PostsResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user){
        model.addAttribute("posts", postsService.findAllDesc());

        // 어노테이션을 추가하여 세션값을 가져오는 부분을 생략할 수 있게 함.
        // SessionUser user = (SessionUser) httpSession.getAttribute("user");

        // 머스테치와 상호작용하기 위해서 사용
        if (user != null) {
           model.addAttribute("userName", user.getName());
        }
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model){
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }

}
