package com.zhl.transaction.controller.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
public class TransactionPagingQueryRequest {
    /**
     * 每页展示条数，默认10
     */
    private int pageSize = 10;

    /**
     * 展示页码，默认1
     */
    private int pageNumber = 1;

}
