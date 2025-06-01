package com.zhl.transaction.service;

import com.zhl.transaction.controller.vo.TransactionCreateRequest;
import com.zhl.transaction.controller.vo.TransactionPagingQueryRequest;
import com.zhl.transaction.controller.vo.TransactionUpdateRequest;
import com.zhl.transaction.dao.TransactionDao;
import com.zhl.transaction.domain.Transaction;
import com.zhl.transaction.exception.TransactionException;
import com.zhl.transaction.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {
    private final HashSet<String> uniqueKeys = new HashSet<>();

    @Autowired
    private TransactionDao transactionDao;

    /**
     * Create Transaction
     * @param transactionCreateRequest TransactionCreateRequest
     * @return Transaction
     */
    public Transaction createTransaction(TransactionCreateRequest transactionCreateRequest,String uniqueKey) {
        AssertUtil.isTrue(!uniqueKeys.contains(uniqueKey), new TransactionException("该交易已存在"));
        uniqueKeys.add(uniqueKey);
        Transaction transaction = transactionCreateRequest.convertToTransaction();
        return transactionDao.createTransaction(transaction);
    }

    /**
     * Modify Transaction
     * @param transactionUpdateRequest TransactionUpdateRequest
     * @return Transaction after modify
     */
    public Transaction modifyTransaction(TransactionUpdateRequest transactionUpdateRequest) {
        Transaction transactionUpdate = transactionUpdateRequest.convertToTransaction();
        Transaction transaction = transactionDao.getTransactionByTransactionId(transactionUpdate.getTransactionId());
        AssertUtil.notNull(transaction, new TransactionException("交易信息不存在"));
        AssertUtil.isTrue(Objects.equals(transactionUpdate.getLastUpdateVersion(), transaction.getLastUpdateVersion()), new TransactionException("交易信息已变更,请重新确认"));
        AssertUtil.isTrue(!transaction.isEqual(transactionUpdate), new TransactionException("交易信息未发生变化，请确认"));
        uniqueKeys.remove(transaction.getUniqueKey());
        transaction.modify(transactionUpdate);
        uniqueKeys.add(transaction.getUniqueKey());
        return transactionDao.updateTransaction(transaction);
    }

    /**
     * remove Transaction
     * @param transactionIdStr Transaction unique
     * @return result of remove action
     */
    public Boolean removeTransaction(String transactionIdStr) {
        Long transactionId = Long.valueOf(transactionIdStr);
        Transaction transaction = transactionDao.getTransactionByTransactionId(transactionId);
        AssertUtil.notNull(transaction, new TransactionException("交易信息不存在"));
        uniqueKeys.remove(transaction.getUniqueKey());
        transactionDao.removeTransaction(transactionId);
        return true;
    }


    /**
     * remove Transaction
     * @param transactionIdStr Transaction unique
     * @return result of remove action
     */
    public Transaction getTransaction(String transactionIdStr) {
        Long transactionId = Long.valueOf(transactionIdStr);
        return transactionDao.getTransactionByTransactionId(transactionId);
    }

    /**
     * paging query transaction
     * @param transactionPagingQueryRequest TransactionPagingQueryRequest
     * @return page of transaction
     */
    public Page<Transaction> queryTransactionsByPagingQuery(TransactionPagingQueryRequest transactionPagingQueryRequest) {
        return transactionDao.queryTransactionListByPagingQuery(transactionPagingQueryRequest);
    }

}
