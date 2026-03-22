package com.example.slot_checker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM access_log WHERE id NOT IN (" +
                   "  SELECT id FROM (" +
                   "    SELECT id FROM access_log ORDER BY access_time DESC LIMIT 20" +
                   "  ) AS temp" + // サブクエリに 'temp' という名前をつけて一度切り離す
                   ")", 
           nativeQuery = true)
    void deleteOldLogs();
}
