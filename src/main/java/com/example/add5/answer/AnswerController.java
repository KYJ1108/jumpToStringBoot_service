package com.example.add5.answer;

import com.example.add5.question.Question;
import com.example.add5.question.QuestionService;
import com.example.add5.user.SiteUser;
import com.example.add5.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

// '/answer'로 시작하는 모든 요청에 대한 처리를 담당하는 컨트롤러 클래스
@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    // 의존성 주입을 위한 필드 선언
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    // 새로운 답변을 생성하는 POST 요청 처리 메서드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm,
                               BindingResult bindingResult, Principal principal) {
        // 주어진 ID에 해당하는 질문을 가져옴
        Question question = this.questionService.getQuestion(id);
        // 현재 사용자 정보를 가져옴
        SiteUser siteUser = this.userService.getUser(principal.getName());
        // 입력 유효성 검사 실패 시, 질문과 함께 상세 페이지를 다시 표시
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        // 답변 생성 후 해당 질문 상세 페이지로 리다이렉션
        Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s#anser_%s", answer.getQuestion().getId(), answer.getId());
    }

    // 답변 수정 폼을 제공하는 GET 요청 처리 메서드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        // 주어진 ID에 해당하는 답변을 가져옴
        Answer answer = this.answerService.getAnswer(id);
        // 현재 사용자가 답변의 작성자인지 확인하고, 아니면 예외를 발생시킴
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        // 답변 수정 폼에 답변 내용을 설정하여 반환
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    // 답변을 수정하는 POST 요청 처리 메서드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal) {
        // 입력 유효성 검사 실패 시 수정 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        // 주어진 ID에 해당하는 답변을 가져옴
        Answer answer = this.answerService.getAnswer(id);
        // 현재 사용자가 답변의 작성자인지 확인하고, 아니면 예외를 발생시킴
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        // 답변 수정 후 해당 질문 상세 페이지로 리다이렉션
        this.answerService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    // 답변을 삭제하는 GET 요청 처리 메서드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id){
        // 주어진 ID에 해당하는 답변을 가져옴
        Answer answer = this.answerService.getAnswer(id);
        // 현재 사용자가 답변의 작성자인지 확인하고, 아니면 예외를 발생시킴
        if(!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        // 답변 삭제 후 해당 질문 상세 페이지로 리다이렉션
        this.answerService.delete(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    // 답변에 대한 투표를 처리하는 GET 요청 처리 메서드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id){
        // 주어진 ID에 해당하는 답변을 가져옴
        Answer answer = this.answerService.getAnswer(id);
        // 현재 사용자 정보를 가져옴
        SiteUser siteUser = this.userService.getUser(principal.getName());
        // 답변에 투표 후 해당 질문 상세 페이지로 리다이렉션
        this.answerService.vote(answer, siteUser);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId(), answer.getId());
    }
}
