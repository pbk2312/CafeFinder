package CafeFinder.cafe.member.controller;


import CafeFinder.cafe.member.dto.MemberLoginDto;
import CafeFinder.cafe.member.dto.MemberSignUpDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/member")
@Controller
public class MemberController {

    @GetMapping("/signupAndLogin")
    public String signUpandLogin(
        @ModelAttribute MemberLoginDto memberLoginDto,
        @ModelAttribute MemberSignUpDto memberSignUpDto,
        HttpServletRequest request
    ) {

        String referer = request.getHeader("Referer");
        if (referer != null) {
            request.getSession().setAttribute("redirectUrl", referer);
        }
        return "SignupAndLogin";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/edit")
    public String edit() {
        return "profileEdit";
    }

    @GetMapping("/cafeScraps")
    public String getCafeScraps() {
        return "scrap_cafe";
    }


}
