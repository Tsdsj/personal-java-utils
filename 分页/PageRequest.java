package com.tt.common.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author tt
 * @date 2024/11/1 08:55
 * 分页参数基类
 */
@Data
@Schema(description = "分页参数基类")
public class PageRequest {

    @Schema(description = "当前页")
    private int page;

    @Schema(description = "每页显示的条数")
    private int pageSize;

    @Schema(description = "排序的字段名")
    private String sortName;

    @Schema(description = "排序方式")
    private String sortOrder;

}