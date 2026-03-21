package com.example.slot_checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDateTime;

@Controller
public class SlotController {

    @Autowired
    private AccessLogRepository repository;

    @GetMapping("/")
    public String index() {
        AccessLog log = new AccessLog();
        log.setAccessTime(LocalDateTime.now());
        log.setMemo("Top Page Access");
        repository.save(log);
        
        return "index";
    }
}
