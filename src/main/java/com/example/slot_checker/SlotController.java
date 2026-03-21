package com.example.slot_checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SlotController {

    // 直接 Repository を触るのではなく、Service（司令塔）にお願いする
    @Autowired
    private AccessLogService accessLogService;

    @GetMapping("/")
    public String index() {
        // 保存と20件掃除をセットで実行！
        accessLogService.saveLog("Top Page Access");
        
        return "index";
    }
}
