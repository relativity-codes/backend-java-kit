package com.swifre.trade_fx_maven.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    /**
     * This class is used to handle requests to the home page.
     * It can be extended to add more functionality in the future.
     */
    // Add methods to handle requests here if needed in the future.
    @RequestMapping("/")
    public String home() {
        return "Welcome to Trade FX API!";
    }

}
