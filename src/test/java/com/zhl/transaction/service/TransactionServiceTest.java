package com.zhl.transaction.service;

import com.zhl.transaction.controller.vo.TransactionCreateRequest;
import com.zhl.transaction.controller.vo.TransactionPagingQueryRequest;
import com.zhl.transaction.controller.vo.TransactionUpdateRequest;
import com.zhl.transaction.dao.TransactionDao;
import com.zhl.transaction.domain.Transaction;
import com.zhl.transaction.exception.TransactionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionDao transactionDao;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction buildTrans(){
        Transaction transaction = new Transaction();
        transaction.setType("1");
        transaction.setAmount(BigDecimal.TEN);
        transaction.setTransactionAccount("A");
        transaction.setTransactionId(12345L);
        transaction.setLastUpdateVersion(0);
        return transaction;
    }


    private TransactionUpdateRequest buildUpdateTrans(String transactionId) {
        TransactionUpdateRequest transactionUpdateRequest = new TransactionUpdateRequest();
        transactionUpdateRequest.setTransactionId(transactionId);
        transactionUpdateRequest.setLastUpdateVersion(0);
        transactionUpdateRequest.setType("1");
        transactionUpdateRequest.setAmount("10");
        transactionUpdateRequest.setTransactionAccount("A");
        return transactionUpdateRequest;
    }
    @Test
    @DisplayName("创建交易成功")
    void createTransaction_succ() {
        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest();
        transactionCreateRequest.setType("1");
        transactionCreateRequest.setAmount("2");
        transactionCreateRequest.setTransactionAccount("A");
        transactionService.createTransaction(transactionCreateRequest, transactionCreateRequest.getUniqueKey());
        verify(transactionDao, times(1)).createTransaction(any());
    }


    @Test
    @DisplayName("创建交易_交易已存在")
    void createTransaction_fail() {
        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest();
        transactionCreateRequest.setType("1");
        transactionCreateRequest.setAmount("2");
        transactionCreateRequest.setTransactionAccount("A");
        transactionService.createTransaction(transactionCreateRequest, transactionCreateRequest.getUniqueKey());
        TransactionException transactionException = assertThrows(TransactionException.class, () ->
                transactionService.createTransaction(transactionCreateRequest, transactionCreateRequest.getUniqueKey()));
        assertEquals("该交易已存在", transactionException.getMessage());

    }

    @Test
    @DisplayName("更新交易_交易不存在")
    void modifyTransaction_transactionNotExist() {
        TransactionUpdateRequest transactionUpdateRequest = buildUpdateTrans("123456");
        TransactionException transactionException = assertThrows(TransactionException.class, () ->
                transactionService.modifyTransaction(transactionUpdateRequest));
        assertEquals("交易信息不存在", transactionException.getMessage());
    }

    @Test
    @DisplayName("更新交易_交易信息有变更")
    void modifyTransaction_transactionHasChanged() {
        Transaction transaction = buildTrans();
        transaction.setLastUpdateVersion(1);
        when(transactionDao.getTransactionByTransactionId(any())).thenReturn(transaction);
        TransactionUpdateRequest transactionUpdateRequest = buildUpdateTrans(String.valueOf(transaction.getTransactionId()));
        TransactionException transactionException = assertThrows(TransactionException.class, () ->
                transactionService.modifyTransaction(transactionUpdateRequest));
        assertEquals("交易信息已变更,请重新确认", transactionException.getMessage());
    }

    @Test
    @DisplayName("更新交易_交易信息未发生变化")
    void modifyTransaction_transactionNoChanged() {
        Transaction transaction = buildTrans();
        when(transactionDao.getTransactionByTransactionId(any())).thenReturn(transaction);
        TransactionUpdateRequest transactionUpdateRequest = buildUpdateTrans(String.valueOf(transaction.getTransactionId()));
        TransactionException transactionException = assertThrows(TransactionException.class, () ->
                transactionService.modifyTransaction(transactionUpdateRequest));
        assertEquals("交易信息未发生变化，请确认", transactionException.getMessage());
    }

    @Test
    @DisplayName("更新交易_成功")
    void modifyTransaction_succ() {
        Transaction transaction = buildTrans();
        when(transactionDao.getTransactionByTransactionId(any())).thenReturn(transaction);
        TransactionUpdateRequest transactionUpdateRequest = buildUpdateTrans(String.valueOf(transaction.getTransactionId()));
        transactionUpdateRequest.setTransactionTargetAccount("B");
        transactionService.modifyTransaction(transactionUpdateRequest);
        verify(transactionDao, times(1)).updateTransaction(any());
    }

    @Test
    @DisplayName("删除交易_成功")
    void removeTransaction_succ() {
        Transaction transaction = buildTrans();
        when(transactionDao.getTransactionByTransactionId(any())).thenReturn(transaction);
        transactionService.removeTransaction(String.valueOf(transaction.getTransactionId()));
        verify(transactionDao, times(1)).removeTransaction(any());
    }

    @Test
    @DisplayName("删除交易_交易不存在")
    void removeTransaction_transNoExist() {
        when(transactionDao.getTransactionByTransactionId(1245L)).thenReturn(null);
        TransactionException transactionException = assertThrows(TransactionException.class, () ->
                transactionService.removeTransaction("1245"));
        assertEquals("交易信息不存在", transactionException.getMessage());
    }

    @Test
    void getTransaction() {
        when(transactionDao.getTransactionByTransactionId(1245L)).thenReturn(null);
        Transaction transaction = transactionService.getTransaction("1245");
        assertTrue(Objects.isNull(transaction));
    }

    @Test
    void queryTransactionsByPagingQuery() {
        when(transactionDao.queryTransactionListByPagingQuery(any())).thenReturn(Page.empty());
        TransactionPagingQueryRequest transactionPagingQueryRequest = new TransactionPagingQueryRequest();
        Page<Transaction> transactions = transactionService.queryTransactionsByPagingQuery(transactionPagingQueryRequest);
        assertEquals(0,transactions.getContent().size());
    }
}