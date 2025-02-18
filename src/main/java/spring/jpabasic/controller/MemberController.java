package spring.jpabasic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import spring.jpabasic.controller.Forms.MemberForm;
import spring.jpabasic.domain.Address;
import spring.jpabasic.domain.Member;
import spring.jpabasic.service.MemberService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm()); // 회원정보 객체를 모델에 담아 뷰로 넘김
        return "members/createMemberForm";
    }

    //@Valid: 유효성 검증(조건에 맞는지 검증, MemberForm은 name이 Not Empty 임.
    // 제약조건에는 NotNull, NotEmpty, NotBlank, Min, Max, Pattern, Email, Size 가 있음
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){
        // BindingResult: Validation(유효성 검사) 실패 시 그에 대한 에러 정보를 BindingResult에 담고, 컨트롤러를 호출
        // @Valid 가 선행되어야 함
        if (result.hasErrors()){
            return "members/createMemberForm"; // 에러가 발생하는 정보를 가지고 MemberForm 으로 다시 돌아감.
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setAddress(address);
        member.setName(form.getName());

        memberService.join(member);
        return "redirect:/"; //기본 화면으로 넘어간다.
    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
