package de.docestate.softwaretest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/", "/neu", "/immobilien/{id:\\d+}", "/immobilien/{id:\\d+}/bearbeiten"})
    public String forward() {
        return "forward:/index.html";
    }
}
