package com.zhl.transaction.controller;

import com.zhl.transaction.controller.vo.ApiResponse;
import com.zhl.transaction.controller.vo.TransactionPagingQueryRequest;
import com.zhl.transaction.domain.Transaction;
import com.zhl.transaction.controller.vo.TransactionCreateRequest;
import com.zhl.transaction.controller.vo.TransactionUpdateRequest;
import com.zhl.transaction.exception.TransactionException;
import com.zhl.transaction.service.TransactionService;
import com.zhl.transaction.util.RedisUtil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 创建交易
     *
     * @param transactionCreateRequest 交易创建参数
     * @return 创建成功的交易
     */
    @RequestMapping(value = "/createTransaction", method = RequestMethod.POST)
    public ApiResponse<String> createTransacation(@RequestBody @Validated TransactionCreateRequest transactionCreateRequest) {
        String redisKey = transactionCreateRequest.getUniqueKey();
        redisUtil.tryLock(redisKey, 3);
        Transaction transaction = transactionService.createTransaction(transactionCreateRequest,redisKey);
        redisUtil.releaseLock(redisKey);
        return ApiResponse.successWithData(String.valueOf(transaction.getTransactionId()));
    }

    /**
     * 修改交易信息
     *
     * @param transactionUpdateRequest 交易信息修改参数（主键、lastUpdateVersion必须存在）
     * @return 修改后的交易信息
     */
    @RequestMapping(value = "/modifyTransaction", method = RequestMethod.POST)
    public ApiResponse<Transaction> modifyTransaction(@RequestBody @Validated TransactionUpdateRequest transactionUpdateRequest) throws TransactionException {
        redisUtil.tryLock(transactionUpdateRequest.getTransactionId(),3);
        Transaction modifyTransaction = transactionService.modifyTransaction(transactionUpdateRequest);
        redisUtil.releaseLock(transactionUpdateRequest.getTransactionId());
        return ApiResponse.successWithData(modifyTransaction);
    }

    /**
     * 删除交易信息
     *
     * @param transactionId 交易信息主键Id
     * @return 删除成功返回True, 删除失败返回False
     */
    @RequestMapping(value = "/removeTransaction/{transactionId}", method = RequestMethod.GET)
    public ApiResponse<Boolean> removeTransaction(@PathVariable @NotEmpty @Pattern(regexp = "^[1-9]\\d{17}$", message = "交易ID格式不正确")  String transactionId) {
        redisUtil.tryLock(transactionId,3);
        Boolean removeResult = transactionService.removeTransaction(transactionId);
        redisUtil.releaseLock(transactionId);
        return ApiResponse.successWithData(removeResult);
    }

    /**
     * 删除交易信息
     *
     * @param transactionId 交易信息主键Id
     * @return 删除成功返回True, 删除失败返回False
     */
    @RequestMapping(value = "/queryTransaction/{transactionId:^[1-9]\\d{17}$}", method = RequestMethod.GET)
    public ApiResponse<Transaction> queryTransaction(@PathVariable String transactionId) {
        return ApiResponse.successWithData(transactionService.getTransaction(transactionId));
    }

    /**
     * 分页查询分页信息
     *
     * @param transactionPagingQueryRequest 交易信息分页查询参数
     * @return 查询结果
     */
    @RequestMapping(value = "/queryTransactionsByPagingQuery", method = RequestMethod.POST)
    public ApiResponse<Page<Transaction>> queryTransactionsByPagingQuery(@RequestBody @Validated TransactionPagingQueryRequest transactionPagingQueryRequest) {
        return ApiResponse.successWithData(transactionService.queryTransactionsByPagingQuery(transactionPagingQueryRequest));
    }

}
