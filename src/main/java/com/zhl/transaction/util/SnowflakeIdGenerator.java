package com.zhl.transaction.util;

/**
 * 雪花算法生成unique key
 */
public class SnowflakeIdGenerator {
    // 起始时间戳（2020-01-01 00:00:00）
    private final long epoch = 1577836800000L;

    // 各部分占用的位数
    private final long datacenterIdBits = 5L;
    private final long machineIdBits = 5L;
    private final long sequenceBits = 12L;

    // 各部分的最大值
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits); // 31
    private final long maxMachineId = -1L ^ (-1L << machineIdBits);       // 31
    private final long maxSequence = -1L ^ (-1L << sequenceBits);         // 4095

    // 各部分向左的位移
    private final long machineIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + machineIdBits;
    private final long timestampShift = sequenceBits + machineIdBits + datacenterIdBits;

    private final long datacenterId;
    private final long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    // 双重校验锁单例模式
    private static volatile SnowflakeIdGenerator instance;

    public static SnowflakeIdGenerator getInstance(long datacenterId, long machineId) {
        if (instance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (instance == null) {
                    instance = new SnowflakeIdGenerator(datacenterId, machineId);
                }
            }
        }
        return instance;
    }

    private SnowflakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID must be between 0 and " + maxDatacenterId);
        }
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + maxMachineId);
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    // 生成下一个 ID
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // 处理时钟回拨
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + 
                    (lastTimestamp - currentTimestamp) + " milliseconds");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // 当前毫秒内序列号用尽，等待下一毫秒
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，重置序列号
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - epoch) << timestampShift) |
                (datacenterId << datacenterIdShift) |
                (machineId << machineIdShift) |
                sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    // 解析 ID 结构（用于调试）
    public static void parseId(long id) {
        long sequence = id & 0xFFF;
        long machineId = (id >> 12) & 0x1F;
        long datacenterId = (id >> 17) & 0x1F;
        long timestamp = (id >> 22) + 1577836800000L;

        System.out.printf("ID: %d\n", id);
        System.out.printf("时间戳: %tF %<tT\n", new java.util.Date(timestamp));
        System.out.printf("数据中心: %d\n", datacenterId);
        System.out.printf("机器: %d\n", machineId);
        System.out.printf("序列号: %d\n", sequence);
    }

    // 示例用法
    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance(1, 1);
        for (int i = 0; i < 5; i++) {
            long id = idGenerator.nextId();
            System.out.println("生成 ID: " + id);
            parseId(id);
            System.out.println("----------------");
        }
    }
}