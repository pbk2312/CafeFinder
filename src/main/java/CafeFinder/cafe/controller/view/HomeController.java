package CafeFinder.cafe.controller.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${google.map.key}")
    private String googleApiKey;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("googleApiKey", googleApiKey);
        return "index";
    }

}
