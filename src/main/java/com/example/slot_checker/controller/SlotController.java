package com.example.slot_checker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SlotController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/calculate")
    public String calculate(
            @RequestParam(required = false) String type,
            @RequestParam int total,
            @RequestParam int bb,
            @RequestParam int rb,
            @RequestParam(required = false) Integer grape,
            Model model) {

        // バリデーション：機種が未選択ならトップへ戻す
        if (type == null || type.isEmpty()) {
            return "redirect:/";
        }

        double[] bbTargets;
        double[] rbTargets;
        String machineName;

        // いただいた全9機種の正確な公表値データ（設定1〜6）
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
                // 万が一予期せぬ値が来た場合のフォールバック（アイムEXをデフォルトに）
                machineName = "アイムジャグラーEX";
                bbTargets = new double[]{273.1, 269.7, 269.7, 259.0, 259.0, 255.0};
                rbTargets = new double[]{439.8, 399.6, 331.0, 315.1, 255.0, 255.0};
                break;
        }

        double currentBbProb = (bb > 0) ? (double) total / bb : 0;
        double currentRbProb = (rb > 0) ? (double) total / rb : 0;
        double currentTotalProb = (bb + rb > 0) ? (double) total / (bb + rb) : 0;

        // 判定ロジック：各設定とのズレを計算
        double[] diffs = new double[6];
        double minDiff = Double.MAX_VALUE;
        int estimatedSetting = 1;

        for (int i = 0; i < 6; i++) {
            double bbDiff = (bb > 0) ? Math.abs(currentBbProb - bbTargets[i]) : 0;
            double rbDiff = (rb > 0) ? Math.abs(currentRbProb - rbTargets[i]) : 0;
            
            double totalDiff = bbDiff + rbDiff;
            diffs[i] = totalDiff;
            
            if (totalDiff < minDiff) {
                minDiff = totalDiff;
                estimatedSetting = i + 1;
            }
        }

        // パーセンテージ（期待度）の算出
        // ズレが少ないほどスコアが高くなるように「逆数」を使う
        double[] scores = new double[6];
        double totalScore = 0;
        for (int i = 0; i < 6; i++) {
            // ズレを累乗して差を強調（+1はゼロ除算エラー防止）
            scores[i] = 1.0 / (Math.pow(diffs[i], 1.5) + 1.0);
            totalScore += scores[i];
        }

        // 合計スコアに対する割合を計算して％（整数）にする
        int[] percentages = new int[6];
        for (int i = 0; i < 6; i++) {
            percentages[i] = (int) Math.round((scores[i] / totalScore) * 100);
        }

        // 画面表示用のデータをModelにセット
        model.addAttribute("type", type);
        model.addAttribute("machineName", machineName);
        model.addAttribute("percentages", percentages); // 期待度％の配列を追加
        model.addAttribute("estimate", estimatedSetting);
        model.addAttribute("bbProb", String.format("%.1f", currentBbProb));
        model.addAttribute("rbProb", String.format("%.1f", currentRbProb));
        model.addAttribute("totalProb", String.format("%.1f", currentTotalProb));

        if (grape != null && grape > 0) {
            model.addAttribute("grapeProb", String.format("%.2f", (double) total / grape));
        }

        return "result";
    }
}