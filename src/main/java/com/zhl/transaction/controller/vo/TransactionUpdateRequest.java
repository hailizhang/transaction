package com.zhl.transaction.controller.vo;

import com.zhl.transaction.domain.Transaction;
import com.zhl.transaction.util.SnowflakeIdGenerator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdateRequest {

    /**
     * 交易ID
     */
    @NotEmpty(message = "交易Id不能为空")
    @Pattern(regexp = "^[1-9]\\d{17}$", message = "交易Id格式不正确")
    private String transactionId;

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

    /**
     * 修改版本
     */
    @NotNull(message = "版本号不能为空")
    private Integer lastUpdateVersion;

    public Transaction convertToTransaction(){
        Transaction transaction = new Transaction();
        transaction.setTransactionId(Long.valueOf(transactionId));
        transaction.setType(this.type);
        transaction.setAmount(new BigDecimal(this.amount));
        transaction.setTransactionAccount(transactionAccount);
        transaction.setTransactionTargetAccount(transactionTargetAccount);
        transaction.setLastUpdateVersion(lastUpdateVersion);
        return transaction;
    }
}
