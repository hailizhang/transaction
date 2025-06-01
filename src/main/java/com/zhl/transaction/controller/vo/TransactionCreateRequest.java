package com.zhl.transaction.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhl.transaction.domain.Transaction;
import com.zhl.transaction.util.SnowflakeIdGenerator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
public class TransactionCreateRequest {

    /**
     * 交易类型
     */
    @NotEmpty(message = "Transaction Type can not be empty")
    private String type;

    /**
     * 交易金额
     */
    @NotEmpty(message = "Transaction amount can not be empty")
    @Pattern(regexp = "^[1-9]\\d*$", message = "交易金额不合法")
    private String amount;

    /**
     * 交易账户
     */
    @NotEmpty(message = "Transaction account can not be empty")
    private String transactionAccount;

    /**
     * 交易目标账户
     */
    private String transactionTargetAccount;

    public String getUniqueKey() {
        StringBuilder stringBuilder = new StringBuilder(this.type).append("_").append(this.amount).append("_").append(this.transactionAccount);
        if (StringUtils.isNotBlank(this.transactionTargetAccount)) {
            stringBuilder.append("_").append(this.transactionTargetAccount);
        }
        return stringBuilder.toString();
    }

    public Transaction convertToTransaction(){
        Transaction transaction = new Transaction();
        transaction.setType(this.type);
        transaction.setAmount(new BigDecimal(this.amount));
        transaction.setTransactionAccount(transactionAccount);
        transaction.setTransactionTargetAccount(transactionTargetAccount);
        transaction.setTransactionId(SnowflakeIdGenerator.getInstance(1,1).nextId());
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setLastUpdateVersion(0);
        return transaction;
    }

}
