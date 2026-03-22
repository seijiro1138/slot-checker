SlotController.java

package com.example.slot_checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SlotController {

    @Autowired
    private AccessLogService accessLogService;

    @PostMapping("/calculate")
    public String calculate(
            @RequestParam(required = false) String type,
            @RequestParam int total,
            @RequestParam int bb,
            @RequestParam int rb,
            @RequestParam(required = false) Integer grape,
            Model model) {

        if (type == null || type.isEmpty()) {
            return "redirect:/";
        }

        double[] bbTargets;
        double[] rbTargets;
        String machineName;

        switch (type) {
            case "im":
                machineName = "アイムジャグラーEX";
                bbTargets = new double[]{273.1, 269.7, 269.7, 259.0, 259.0, 255.0};
                rbTargets = new double[]{439.8, 399.6, 331.0, 315.1, 255.0, 255.0};
                break;
            case "gogo3":
                machineName = "ゴーゴージャグラー3";
                bbTargets = new double[]{259.0, 258.0, 257.0, 254.0, 247.3, 234.9};
                rbTargets = new double[]{354.2, 332.7, 306.2, 268.6, 247.3, 234.9};
                break;
            case "happy":
                machineName = "ハッピージャグラーVⅢ";
                bbTargets = new double[]{273.1, 270.8, 263.2, 254.0, 239.2, 226.0};
                rbTargets = new double[]{397.2, 362.1, 332.7, 300.6, 273.1, 256.0};
                break;
            case "my":
                machineName = "マイジャグラーⅤ";
                bbTargets = new double[]{273.1, 270.8, 266.4, 254.0, 240.1, 229.1};
                rbTargets = new double[]{409.6, 385.5, 336.1, 290.0, 268.6, 229.1};
                break;
            case "funky":
                machineName = "ファンキージャグラー2";
                bbTargets = new double[]{266.4, 259.0, 256.0, 249.2, 240.1, 219.2};
                rbTargets = new double[]{439.8, 407.1, 366.1, 322.8, 299.3, 262.1};
                break;
            case "girls":
                machineName = "ジャグラーガールズSS";
                bbTargets = new double[]{273.1, 270.8, 260.1, 250.1, 243.6, 226.0};
                rbTargets = new double[]{381.0, 350.5, 316.6, 281.3, 270.8, 252.1};
                break;
            case "mr":
                machineName = "ミスタージャグラー";
                bbTargets = new double[]{268.6, 267.5, 260.1, 249.2, 240.9, 237.4};
                rbTargets = new double[]{374.5, 354.2, 331.0, 291.3, 257.0, 237.4};
                break;
            case "ultra":
                machineName = "ウルトラミラクルジャグラー";
                bbTargets = new double[]{267.5, 261.1, 256.0, 242.7, 233.2, 216.3};
                rbTargets = new double[]{425.6, 402.1, 350.5, 322.8, 297.9, 277.7};
                break;
            case "neo":
                machineName = "ネオアイムジャグラーEX";
                bbTargets = new double[]{273.1, 269.7, 269.7, 259.0, 259.0, 255.0};
                rbTargets = new double[]{439.8, 399.6, 331.0, 315.1, 255.0, 255.0};
                break;
            default:
                machineName = "アイムジャグラーEX";
                bbTargets = new double[]{273.1, 269.7, 269.7, 259.0, 259.0, 255.0};
                rbTargets = new double[]{439.8, 399.6, 331.0, 315.1, 255.0, 255.0};
                break;
        }

        // null対応（これ超重要）
        Double currentBbProb = (bb > 0) ? (double) total / bb : null;
        Double currentRbProb = (rb > 0) ? (double) total / rb : null;
        Double currentTotalProb = (bb + rb > 0) ? (double) total / (bb + rb) : null;

        double[] diffs = new double[6];
        double minDiff = Double.MAX_VALUE;
        int estimatedSetting = 1;

        for (int i = 0; i < 6; i++) {
            double bbDiff = (currentBbProb != null) ? Math.abs(currentBbProb - bbTargets[i]) : 0;
            double rbDiff = (currentRbProb != null) ? Math.abs(currentRbProb - rbTargets[i]) : 0;

            double totalDiff2 = 0;
            if (currentTotalProb != null) {
                double totalTarget = 1 / (1 / bbTargets[i] + 1 / rbTargets[i]);
                totalDiff2 = Math.abs(currentTotalProb - totalTarget);
            }

            double totalDiff = bbDiff + rbDiff + totalDiff2;
            diffs[i] = totalDiff;

            if (totalDiff < minDiff) {
                minDiff = totalDiff;
                estimatedSetting = i + 1;
            }
        }

        // スコア化
        double[] scores = new double[6];
        double totalScore = 0;

        for (int i = 0; i < 6; i++) {
            scores[i] = 1.0 / (Math.pow(diffs[i], 1.5) + 1.0);
            totalScore += scores[i];
        }

        // %化
        List<Integer> percentages = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            percentages.add((int) Math.round((scores[i] / totalScore) * 100));
        }

        model.addAttribute("type", type);
        model.addAttribute("machineName", machineName);
        model.addAttribute("percentages", percentages);
        model.addAttribute("estimate", estimatedSetting);

        model.addAttribute("bbProb", currentBbProb);
        model.addAttribute("rbProb", currentRbProb);
        model.addAttribute("totalProb", currentTotalProb);

        if (grape != null && grape > 0) {
            model.addAttribute("grapeProb", (double) total / grape);
        }

        accessLogService.saveLog("判別実行: " + type + " (" + total + "G)");

        return "result";
    }
}

result.html

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>判別結果 - スロットチェッカー</title>
    <style>
        body { font-family: 'Helvetica Neue', Arial, sans-serif; background-color: #1a1a1a; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; color: #fff; }
        .card { background: #2a2a2a; padding: 2rem; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.5); width: 90%; max-width: 400px; }
        h1 { font-size: 1.2rem; color: #f8c146; text-align: center; margin-bottom: 1.5rem; border-bottom: 2px solid #555; padding-bottom: 10px; }
        
        /* 確率の表示エリア */
        .prob-box { background: #333; padding: 15px; border-radius: 8px; margin-bottom: 20px; }
        .res-item { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #444; }
        .res-item:last-child { border-bottom: none; }
        .res-label { font-size: 0.9rem; color: #aaa; }
        .res-value { font-size: 1.1rem; font-weight: bold; color: #fff; }

        /* パーセンテージ表示エリア */
        .pct-container { margin-top: 10px; }
        .pct-row { display: flex; align-items: center; margin-bottom: 12px; transition: all 0.3s; }
        .setting-label { width: 50px; font-weight: bold; font-size: 0.9rem; color: #ccc; }
        .bar-bg { flex-grow: 1; background: #444; height: 12px; margin: 0 10px; border-radius: 6px; overflow: hidden; }
        .bar-fill { height: 100%; background-color: #007bff; border-radius: 6px; }
        .pct-value { width: 45px; text-align: right; font-weight: bold; font-size: 0.9rem; }

        /* 最有力設定の特別装飾 */
        .top-setting .setting-label { font-size: 1.4rem; color: #ff3b3b; text-shadow: 0 0 5px rgba(255, 59, 59, 0.5); }
        .top-setting .bar-fill { background: linear-gradient(90deg, #ff3b3b, #ff8e8e); box-shadow: 0 0 8px rgba(255, 59, 59, 0.8); }
        .top-setting .pct-value { font-size: 1.4rem; color: #ff3b3b; }

        .back-link { display: block; text-align: center; margin-top: 2rem; color: #aaa; text-decoration: none; font-size: 0.9rem; padding: 10px; background: #333; border-radius: 8px; }
        .back-link:hover { background: #444; color: #fff; }
    </style>
</head>
<body>

<div class="card">
    <h1 th:text="${machineName} + ' の判別結果'">機種名</h1>
    
    <div class="pct-container">
        <div th:each="pct, stat : ${percentages}" 
             th:class="${stat.count == estimate} ? 'pct-row top-setting' : 'pct-row'">
            
            <span class="setting-label">設定<span th:text="${stat.count}">1</span></span>
            
            <div class="bar-bg">
                <div class="bar-fill" th:style="'width: ' + ${pct} + '%;'"></div>
            </div>
            
            <span class="pct-value" th:text="${pct} + '%'">0%</span>
        </div>
    </div>

    <div class="prob-box" style="margin-top: 20px;">
        <div class="res-item">
    <span class="res-label">BB確率</span>
    <span class="res-value"
          th:text="${bbProb != null} ? '1/' + ${#numbers.formatDecimal(bbProb, 1, 1)} : '-'">
        1/--
    </span>
</div>

<div class="res-item">
    <span class="res-label">RB確率</span>
    <span class="res-value"
          th:text="${rbProb != null} ? '1/' + ${#numbers.formatDecimal(rbProb, 1, 1)} : '-'">
        1/--
    </span>
</div>
        <div class="res-item">
    <span class="res-label">合算確率</span>
    <span class="res-value"
          th:text="${totalProb != null} ? '1/' + ${#numbers.formatDecimal(totalProb, 1, 1)} : '-'">
        1/--
    </span>
</div>
        
        <div class="res-item" th:if="${grapeProb != null}">
            <span class="res-label">ブドウ確率</span>
            <span class="res-value" th:text="'1/' + ${grapeProb}">1/--</span>
        </div>
    </div>

    <a href="/" class="back-link">← 新しいデータで判別する</a>
</div>

</body>
</html>
