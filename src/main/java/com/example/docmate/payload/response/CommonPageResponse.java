package com.example.docmate.payload.response;

import com.example.docmate.payload.request.PaginationInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CommonPageResponse<T> {

    private PaginationInfo paginationInfo;
    private List<T> data;
}
