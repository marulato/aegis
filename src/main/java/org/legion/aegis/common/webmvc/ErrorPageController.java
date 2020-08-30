package org.legion.aegis.common.webmvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/web/error")
    public String redirect500() {
        return "errors/500";
    }
}
