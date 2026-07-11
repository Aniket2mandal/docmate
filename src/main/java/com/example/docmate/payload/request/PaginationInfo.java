package com.example.docmate.payload.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationInfo {

    public PaginationInfo(Long total, Integer pageNo, Integer pageSize, Integer totalPages) {
        super();
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    private Long total;
    private Integer pageNo;
    private Integer pageSize;
    private Integer totalPages;
}
