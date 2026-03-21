package com.example.slot_checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SlotController {

    @Autowired
    private AccessLogService accessLogService;

    // 1. トップページ（/）にアクセスした時
    @GetMapping("/")
    public String index() {
        accessLogService.saveLog("Top Page Access");
        return "index";
    }

    // 2. 「判別を実行する」ボタン（/calculate）を押した時 ★ここを追加！
    @GetMapping("/calculate")
    public String calculate() {
        // ボタンが押されたこともログに残す（これで20件掃除も走ります）
        accessLogService.saveLog("判別実行ボタン押下");
        
        // とりあえず今は index.html に戻す設定です。
        // もし結果表示用のHTML（result.htmlなど）を別に作っているなら、ここを "result" に変えます。
        return "index"; 
    }
}
