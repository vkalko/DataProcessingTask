package org.example.vkalko.dataprocessing.controller.web;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping("/")
    public String response() {
        return "Index";
    }


}
