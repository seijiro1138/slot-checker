package com.example.slot_checker;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime accessTime;
    private String memo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getAccessTime() { return accessTime; }
    public void setAccessTime(LocalDateTime accessTime) { this.accessTime = accessTime; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
