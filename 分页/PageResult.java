package com.tt.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author TJ Yuan
 * @date 2024/7/22 15:09
 * description 分页结果封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -7913051165120324091L;

    private long total; //总记录数

    private long totalPage; //总页数

    private long currentPage; //当前页码

    private List<T> records; //当前页数据集合

}
