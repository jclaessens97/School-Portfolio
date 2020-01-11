package be.kdg.sensorservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class to show the homepage
 */
@Controller
public class HomeController {
    @GetMapping
    public ModelAndView home(ModelAndView mav) {
        mav.setViewName("home_page");
        return mav;
    }
}
