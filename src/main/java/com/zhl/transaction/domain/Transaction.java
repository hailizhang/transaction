package com.zhl.transaction.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    /**
     * 交易ID
     */
    private Long transactionId;

    /**
     * 交易类型
     */
    private String type;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 交易时间
     */
    private LocalDateTime transactionTime;

    /**
     * 交易账户
     */
    private String transactionAccount;

    /**
     * 交易目标账户
     */
    private String transactionTargetAccount;

    /**
     * 修改版本
     */
    private Integer lastUpdateVersion;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public boolean isEqual(Transaction transaction) {
        if (!StringUtils.equals(this.type, transaction.type)) {
            return false;
        }
        if (this.amount.compareTo(transaction.amount) != 0) {
            return false;
        }
        if (!StringUtils.equals(this.transactionAccount, transaction.transactionAccount)) {
            return false;
        }
        if (!StringUtils.equals(this.transactionTargetAccount, transaction.transactionTargetAccount)) {
            return false;
        }
        return true;
    }

    public void modify(Transaction transactionUpdate) {
        this.type = transactionUpdate.type;
        this.amount = transactionUpdate.amount;
        this.transactionAccount = transactionUpdate.getTransactionAccount();
        this.transactionTargetAccount = transactionUpdate.getTransactionTargetAccount();
        this.lastUpdateVersion += 1;
    }

    public String getUniqueKey() {
        StringBuilder stringBuilder = new StringBuilder(this.type).append("_").append(this.amount).append("_").append(this.transactionAccount);
        if (StringUtils.isNotBlank(this.transactionTargetAccount)) {
            stringBuilder.append("_").append(this.transactionTargetAccount);
        }
        return stringBuilder.toString();
    }
}
