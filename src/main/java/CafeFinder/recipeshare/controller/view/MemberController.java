package CafeFinder.recipeshare.controller.view;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import CafeFinder.recipeshare.dto.MemberLoginDto;
import CafeFinder.recipeshare.dto.MemberSignUpDto;

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

}
