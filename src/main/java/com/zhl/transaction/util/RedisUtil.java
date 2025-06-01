package com.zhl.transaction.util;

import com.zhl.transaction.exception.TransactionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisUtil {

    private final Map<String, LocalDateTime> redisLockKeyToExpiryTimeMap = new ConcurrentHashMap<>();


    public boolean tryLock(String key, int expiryTime){
        if (redisLockKeyToExpiryTimeMap.containsKey(key)) {
            AssertUtil.isTrue(redisLockKeyToExpiryTimeMap.get(key).isAfter(LocalDateTime.now()), new TransactionException("该笔交易正在进行其他操作，请稍后再试"));
        }
        redisLockKeyToExpiryTimeMap.put(key, LocalDateTime.now().plusMinutes(expiryTime));
        return true;
    }

    public void releaseLock(String key) {
        redisLockKeyToExpiryTimeMap.remove(key);
    }
}
