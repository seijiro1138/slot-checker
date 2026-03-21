package com.example.slot_checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class AccessLogService {

    @Autowired
    private AccessLogRepository repository;

    @Transactional
    public void saveLog(String memo) {
        // 1. 新しいログを作成
        AccessLog log = new AccessLog();
        log.setAccessTime(LocalDateTime.now());
        log.setMemo(memo);
        
        // 2. 保存実行
        repository.save(log);

        // 3. 20件より古いデータを掃除する
        repository.deleteOldLogs();
    }
}
