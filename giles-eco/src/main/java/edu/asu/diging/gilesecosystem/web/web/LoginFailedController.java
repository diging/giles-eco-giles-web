package edu.asu.diging.gilesecosystem.web.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginFailedController {

    @RequestMapping(value="/signin")
    public String loginFailed() {
        return "signin";
    }
}
