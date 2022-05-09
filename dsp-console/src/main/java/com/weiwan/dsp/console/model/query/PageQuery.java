package com.weiwan.dsp.console.model.query;

import lombok.Data;

/**
 * @author: xiaozhennan
 */
@Data
public class PageQuery {
    private int pageNo;
    private int pageSize;
    private String sortField;
    private SortOrder sortOrder;

    public static enum SortOrder {
        ascend,
        descend
    }
}
