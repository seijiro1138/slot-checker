import org.springframework.stereotype.Controller; // 追加が必要
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.Arrays;

@Controller
public class SlotController {

    @Autowired // これを追加
    private AccessLogService accessLogService;

    @PostMapping("/calculate")
    public String calculate(@RequestParam("type") String type,
                            @RequestParam("total") int total,
                            @RequestParam("bb") int bb,
                            @RequestParam("rb") int rb,
                            @RequestParam(value = "grape", required = false) Integer grape,
                            Model model) {

        // 1. 各確率の計算 (doubleにキャストして計算)
        String bbProb = (bb > 0) ? String.format("%.1f", (double) total / bb) : "0.0";
        String rbProb = (rb > 0) ? String.format("%.1f", (double) total / rb) : "0.0";
        String totalProb = (bb + rb > 0) ? String.format("%.1f", (double) total / (bb + rb)) : "0.0";
        
        // ブドウ確率
        String grapeProb = (grape != null && grape > 0) ? String.format("%.2f", (double) total / grape) : "0.0";

        // 2. 画面に渡すデータの準備（HTML側の ${名前} と一致させる）
        model.addAttribute("machineName", getMachineName(type));
        model.addAttribute("bbProb", bbProb);
        model.addAttribute("rbProb", rbProb);
        model.addAttribute("totalProb", totalProb);
        model.addAttribute("grapeProb", grapeProb);

        // 3. グラフ用のダミーデータ（設定1〜6のパーセンテージ）
        model.addAttribute("percentages", Arrays.asList(10.0, 15.0, 20.0, 15.0, 10.0, 30.0)); 
        model.addAttribute("estimate", 6); // 設定6を強調表示（赤く光る）

        accessLogService.saveLog("判別実行: " + type + " (" + total + "G)");

        return "result"; 
    }

    // 機種コードを名前に変換する補助メソッド
    private String getMachineName(String type) {
        switch(type) {
            case "my": return "マイジャグラーV";
            case "im": return "アイムジャグラーEX";
            case "gogo3": return "ゴーゴージャグラー3";
            case "happy": return "ハッピージャグラーVⅢ";
            case "funky": return "ファンキージャグラー2";
            case "girls": return "ジャグラーガールズSS";
            case "mr": return "ミスタージャグラー";
            case "ultra": return "ウルトラミラクルジャグラー";
            case "neo": return "ネオアイムジャグラーEX";
            default: return "ジャグラーシリーズ";
        }
    }
}
