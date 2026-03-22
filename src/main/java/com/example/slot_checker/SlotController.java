package com.example.slot_checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        if (type == null || type.isEmpty()) return "redirect:/";

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

        // 確率計算（null対応）
        Double bbProb = (bb > 0) ? (double) total / bb : null;
        Double rbProb = (rb > 0) ? (double) total / rb : null;
        Double totalProb = (bb + rb > 0) ? (double) total / (bb + rb) : null;

        // 合成確率
        double[] combinedTargets = new double[6];
        for (int i = 0; i < 6; i++) {
            combinedTargets[i] = 1 / (1 / bbTargets[i] + 1 / rbTargets[i]);
        }

        // diffs 計算
        double[] diffs = new double[6];
        int estimatedSetting = 1;
        double minDiff = Double.MAX_VALUE;

        for (int i = 0; i < 6; i++) {
            double bbDiff = (bbProb != null) ? Math.abs(bbProb - bbTargets[i]) : 0;
            double rbDiff = (rbProb != null) ? Math.abs(rbProb - rbTargets[i]) : 0;
            double totalDiff = (totalProb != null) ? Math.abs(totalProb - combinedTargets[i]) : 0;

            double score = bbDiff * 0.4 + rbDiff * 0.6 + totalDiff * 0.5;
            diffs[i] = score;

            if (score < minDiff) {
                minDiff = score;
                estimatedSetting = i + 1;
            }
        }

        // パーセンテージ化
        double totalScore = 0;
        double[] scores = new double[6];
        for (int i = 0; i < 6; i++) {
            scores[i] = 1.0 / (Math.pow(diffs[i], 1.5) + 1.0);
            totalScore += scores[i];
        }

        List<Integer> percentages = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            percentages.add((int)Math.round((scores[i] / totalScore) * 100));
        }

        // 配列 → List 変換（Thymeleaf安全対応）
        List<Double> diffsList = Arrays.stream(diffs).boxed().collect(Collectors.toList());
        List<Double> combinedList = Arrays.stream(combinedTargets).boxed().collect(Collectors.toList());

        model.addAttribute("type", type);
        model.addAttribute("machineName", machineName);
        model.addAttribute("percentages", percentages);
        model.addAttribute("estimate", estimatedSetting);
        model.addAttribute("bbProb", bbProb);
        model.addAttribute("rbProb", rbProb);
        model.addAttribute("totalProb", totalProb);
        model.addAttribute("diffs", diffsList);
        model.addAttribute("combinedTargets", combinedList);

        if (grape != null && grape > 0) {
            model.addAttribute("grapeProb", (double) total / grape);
        }

        accessLogService.saveLog("判別実行: " + type + " (" + total + "G)");

        return "result";
    }
}
