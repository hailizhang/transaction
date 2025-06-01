package com.zhl.transaction.dao;

import com.zhl.transaction.controller.vo.TransactionPagingQueryRequest;
import com.zhl.transaction.domain.Transaction;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionDao {
    private final Map<Long, Transaction> transactionIdToTransactionMap = new ConcurrentHashMap<>();

    /**
     *
     * @param transaction param
     * @return transaction
     */
    @CachePut(value = "transaction", key = "#result.transactionId")
    public Transaction createTransaction(Transaction transaction) {
        transactionIdToTransactionMap.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    @CachePut(value = "transaction", key = "#result.transactionId")
    public Transaction updateTransaction(Transaction transaction) {
        return transactionIdToTransactionMap.put(transaction.getTransactionId(), transaction);
    }

    @Cacheable(value = "transaction", key = "#transactionId")
    public Transaction getTransactionByTransactionId(Long transactionId) {
        return transactionIdToTransactionMap.getOrDefault(transactionId, null);
    }

    @CacheEvict(value = "transaction", key = "#transactionId")
    public Transaction removeTransaction(Long transactionId) {
        return transactionIdToTransactionMap.remove(transactionId);
    }

    public Page<Transaction> queryTransactionListByPagingQuery(TransactionPagingQueryRequest transactionPagingQueryRequest) {
        ArrayList<Transaction> transactions = new ArrayList<>(transactionIdToTransactionMap.values());
        int start = Math.max(transactionPagingQueryRequest.getPageSize() * (transactionPagingQueryRequest.getPageSize() - 1),0);
        if (CollectionUtils.isEmpty(transactions) || start > transactions.size()) {
            return Page.empty();
        }
        int end = Math.min(transactions.size(), start + transactionPagingQueryRequest.getPageSize());
        return new PageImpl<>(transactions.subList(start,end),
                PageRequest.of(transactionPagingQueryRequest.getPageNumber(), transactionPagingQueryRequest.getPageNumber()),
                transactions.size());
    }
}
