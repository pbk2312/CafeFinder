package CafeFinder.cafe.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gu")
public class CafeInfoController {

    @GetMapping("/{district}")
    public String getCafeListByDistrict(@PathVariable String district, Model model) {
        model.addAttribute("district", district);
        return "cafeThemeList";
    }

    @GetMapping("/{district}/{theme}")
    public String getCafeListByDistrictAndTheme(@PathVariable String district,
                                                @PathVariable String theme,
                                                Model model) {
        model.addAttribute("district", district);
        model.addAttribute("theme", theme);
        return "themeListCafes";
    }


}
