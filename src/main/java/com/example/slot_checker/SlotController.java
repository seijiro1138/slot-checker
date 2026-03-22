import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.Arrays;

@PostMapping("/calculate")
public String calculate(
    @RequestParam("type") String type,
    @RequestParam("total") int total,
    @RequestParam("bb") int bb,
    @RequestParam("rb") int rb,
    @RequestParam(value = "grape", required = false) Integer grape,
    Model model) {

    // 1. 各確率の計算（0除算を防ぐ）
    String bbProb = (bb > 0) ? String.format("%.1f", (double) total / bb) : "0.0";
    String rbProb = (rb > 0) ? String.format("%.1f", (double) total / rb) : "0.0";
    String totalProb = (bb + rb > 0) ? String.format("%.1f", (double) total / (bb + rb)) : "0.0";
    
    String grapeProb = null;
    if (grape != null && grape > 0) {
        grapeProb = String.format("%.2f", (double) total / grape);
    }

    // 2. 画面に渡すデータの準備（HTML側の ${名前} と一致させる！）
    model.addAttribute("machineName", getMachineName(type)); // 下の補助メソッドを使用
    model.addAttribute("bbProb", bbProb);
    model.addAttribute("rbProb", rbProb);
    model.addAttribute("totalProb", totalProb);
    model.addAttribute("grapeProb", grapeProb);

    // 3. グラフ用のダミーデータ（今は仮の数値を入れます）
    model.addAttribute("percentages", java.util.Arrays.asList(10, 15, 20, 15, 10, 30)); 
    model.addAttribute("estimate", 6); // 設定6を赤く光らせる

    // 4. ログ保存
    accessLogService.saveLog("判別実行: " + type + " (" + total + "G)");

    return "result"; 
}

// 機種コードを名前に変換する補助メソッド（クラス内のどこかに貼ってください）
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
