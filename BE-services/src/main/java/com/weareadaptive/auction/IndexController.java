package com.weareadaptive.auction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class IndexController implements ErrorController {
    Logger logger = LoggerFactory.getLogger(Logger.class);

    @PostMapping("/")
    void postError(@RequestBody final Object body) {
        logger.error("invalid body: " + body.toString());
    }
}
