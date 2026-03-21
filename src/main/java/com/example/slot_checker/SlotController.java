package com.example.slot_checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 

@Controller
public class SlotController {

    @Autowired
    private AccessLogService accessLogService;

    @GetMapping("/")
    public String index() {
        accessLogService.saveLog("Top Page Access");
        return "index";
    }

    // ★ @GetMapping を @PostMapping に変更！
    @PostMapping("/calculate")
    public String calculate() {
        // ボタンが押されたログを残す
        accessLogService.saveLog("判別実行ボタン押下");
    
        return "result"; 
    }
}
