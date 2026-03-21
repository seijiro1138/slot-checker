package com.example.slotchecker;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime accessTime;
    private String memo;

    // コンストラクタ
    public AccessLog() {}
    public AccessLog(String memo) {
        this.accessTime = LocalDateTime.now();
        this.memo = memo;
    }
    
}
