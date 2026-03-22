package com.example.slot_checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class SlotController {

    @Autowired
    private AccessLogService accessLogService;

    @PostMapping("/calculate")
    public String calculate(
        @RequestParam("type") String type,
        @RequestParam("total") int total,
        @RequestParam("bb") int bb,
        @RequestParam("rb") int rb,
        @RequestParam(value = "grape", required = false) Integer grape,
        Model model) {

        Double bbProb = (bb > 0) ? (double) total / bb : null;
        Double rbProb = (rb > 0) ? (double) total / rb : null;
        Double totalProb = (bb + rb > 0) ? (double) total / (bb + rb) : null;
        
        Double grapeProb = null;
        if (grape != null && grape > 0) {
            grapeProb = (double) total / grape;
        }

        model.addAttribute("machineName", getMachineName(type));
        model.addAttribute("bbProb", bbProb);
        model.addAttribute("rbProb", rbProb);
        model.addAttribute("totalProb", totalProb);
        model.addAttribute("grapeProb", grapeProb);

        model.addAttribute("percentages", java.util.Arrays.asList(10, 15, 20, 15, 10, 30)); 
        model.addAttribute("estimate", 6);

        accessLogService.saveLog("判別実行: " + type + " (" + total + "G)");

        return "result"; 
    }

    private String getMachineName(String type) {
        switch(type) {
            case "my": return "マイジャグラーV";
            case "im": return "アイムジャグラーEX";
            case "gogo3": return "ゴーゴージャグラー3";
            case "happy": return "ハッピージャグラーVⅢ";
            case "funky": return "ファンキージャグラー2";
            case "girls": return "ジャグラーガールズSS";
            case "mr": return "ミスタージャグラー";
            default: return "ジャグラーシリーズ";
        }
    }
}
