package CafeFinder.cafe.cafe.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cafe")
public class CafeController {

    @Value("${google.map.key}")
    private String googleKey;

    @GetMapping("/{district}")
    public String getCafeListByDistrict(@PathVariable String district, Model model) {
        model.addAttribute("district", district);
        return "themeList";
    }

    @GetMapping("/{district}/{theme}")
    public String getCafeListByDistrictAndTheme(@PathVariable String district,
                                                @PathVariable String theme,
                                                Model model) {
        model.addAttribute("district", district);
        model.addAttribute("theme", theme);
        return "cafeList";
    }

    @GetMapping("/detail/{cafeCode}")
    public String getCafeDetail(@PathVariable String cafeCode, Model model) {
        model.addAttribute("cafeCode", cafeCode);
        model.addAttribute("googleAppkey", googleKey);
        return "cafeDetail";
    }

}
