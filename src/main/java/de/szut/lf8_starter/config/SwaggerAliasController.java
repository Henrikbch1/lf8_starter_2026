package de.szut.lf8_starter.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerAliasController {

    @GetMapping("/swagger")
    public String swagger() {
        return "forward:/swagger-ui/index.html";
    }
}
