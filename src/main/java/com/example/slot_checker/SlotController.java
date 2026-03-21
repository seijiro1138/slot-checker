import org.springframework.ui.Model; // これを追加！
import org.springframework.web.bind.annotation.RequestParam;

@PostMapping("/calculate")
public String calculate(
    @RequestParam("type") String type,
    @RequestParam("total") int total,
    @RequestParam("bb") int bb,
    @RequestParam("rb") int rb,
    @RequestParam(value = "grape", required = false) Integer grape,
    Model model) { // Modelを追加

    // 1. 確率の計算（(double)を付けて小数点を有効にする）
    double bbProbability = (bb > 0) ? (double) total / bb : 0;
    double rbProbability = (rb > 0) ? (double) total / rb : 0;

    // 2. ログ保存
    accessLogService.saveLog("判別実行: " + type + " (" + total + "G)");

    // 3. 画面（result.html）に値を渡す
    model.addAttribute("type", type);
    model.addAttribute("bbProb", String.format("%.1f", bbProbability)); // 「1/150.5」の「150.5」部分
    model.addAttribute("rbProb", String.format("%.1f", rbProbability));

    return "result"; 
}
