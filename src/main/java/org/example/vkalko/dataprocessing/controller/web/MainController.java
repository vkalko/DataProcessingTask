package org.example.vkalko.dataprocessing.controller.web;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/")
public class MainController {

    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    @GetMapping("/")
    public String response() throws IOException {
        return "Index";
    }


}
