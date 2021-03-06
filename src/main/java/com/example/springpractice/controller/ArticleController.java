package com.example.springpractice.controller;

import com.example.springpractice.dto.ArticleForm;
import com.example.springpractice.dto.CommentDto;
import com.example.springpractice.entity.Article;
import com.example.springpractice.repository.ArticleRepository;
import com.example.springpractice.service.ArticleService;
import com.example.springpractice.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
public class ArticleController {

    @Autowired // 스프링 부트가 미리 생성해놓은 객체를 자동 연결
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form) {
        log.info(form.toString());

        // 1. templates에서 전달 받은 DTO(ArticleForm)를 Entity(Article)로 변환
        Article article = form.toEntity();
        log.info(article.toString());

        // 2. Repository를 이용해 Entity를 DB안에 저장
        Article saved = articleRepository.save(article);
        log.info(saved.toString());

        return "redirect:/articles/" + saved.getId();
    }

    @GetMapping("/articles/{id}")// 요청 URL 처리
    public String show(@PathVariable Long id, Model model) { // URL에서 id를 변수로 가져옴
        log.info("id = " + id);

        // 1: id로 데이터를 가져옴
        // Article articleEntity = articleRepository.findById(id).orElse(null);
        Article articleEntity = articleService.show(id);
        List<CommentDto> commentDtos = commentService.comments(id);
        // 2: 가져온 데이터를 모델에 등록
        model.addAttribute("article", articleEntity);
        model.addAttribute("commentDtos", commentDtos);

        // 3: 보여줄 페이지를 설정
        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model) {
        // 1: 모든 Article을 가져온다
        List<Article> articleEntityList = articleRepository.findAll();

        // 2: 가져온 Article 묶음을 뷰로 전달한다
        model.addAttribute("articleList", articleEntityList);

        // 3: 뷰 페이지를 설정한다
        return "articles/index";
    }
    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        // 수정할 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null);

        // 모델에 데이터 등록
        model.addAttribute("article", articleEntity);

        // 뷰 페이지 설정
        return "articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleForm form) {
        log.info(form.toString());

        // 1. DTO를 entity로 변환
        Article articleEntity = form.toEntity();
        log.info(articleEntity.toString());

        // 2. entity를 DB로 저장
        // 2-1. DB에서 기존 데이터를 가져옴
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);
        // 2-2. 기존 데이터가 있으면 값을 갱신
        if (target != null) {
            articleRepository.save(articleEntity);
        }

        // 3. 수정 결과 페이지로 리다이렉트
        return "redirect:/articles/" + articleEntity.getId();
    }
    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr) {
        log.info("삭제 요청이 들어왔습니다");

        // 1. 삭제 대상을 가져옴
        Article target = articleRepository.findById(id).orElse(null);
        log.info(target.toString());

        // 2. 대상을 삭제
        if (target != null) {
            articleRepository.delete(target);
            rttr.addFlashAttribute("msg","삭제 되었습니다.");
        }

        // 3. 결과 페이지로 리다이렉트
        return "redirect:/articles";
    }



}