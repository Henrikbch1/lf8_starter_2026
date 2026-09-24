package de.szut.lf8_starter.welcome;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class WelcomeController {
    @GetMapping("/welcome") public String welcome() { return "Willkommen beim LF8-Starter!"; }
}
