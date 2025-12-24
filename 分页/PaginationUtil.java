package com.tt.paginate;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @author TJ Yuan
 * @date 2024/8/21 09:33
 * 分页工具类
 */
@Slf4j
public class PaginationUtil {

    private final static String ASC = "asc";
    private final static String DESC = "desc";
    private final static String DEFAULT_SORT_NAME = "id";

    @FunctionalInterface
    public interface PageQuery<T> {
        Page<T> execute();
    }

    /**
     * 执行分页查询并返回分页结果
     *
     * @param pageRequest 分页请求，包含当前页码、每页条数、排序字段、排序方式
     * @param query       查询方法，返回分页数据
     * @param <T>         返回的数据类型
     * @return 分页结果，包括总数、总页数、当前页、数据记录等
     */
    public static <T> PageResult<T> paginate(PageRequest pageRequest, PageQuery<T> query) {
        if (pageRequest == null) {
            throw new IllegalArgumentException("分页请求参数不能为空");
        }
        int page = Optional.of(pageRequest.getPage()).filter(p -> p > 0).orElse(1);
        int pageSize = Optional.of(pageRequest.getPageSize()).filter(ps -> ps > 0).orElse(10);
        String sortName = Optional.ofNullable(pageRequest.getSortName()).filter(sn -> !sn.isEmpty()).orElse(DEFAULT_SORT_NAME);

        String sortOrder = Optional.ofNullable(pageRequest.getSortOrder())
                .map(String::trim)
                .filter(so -> !so.isEmpty())
                .orElse(ASC);
        // 仅允许 asc/desc，避免非法 SQL 片段
        String normalizedSortOrder = sortOrder.equalsIgnoreCase(DESC) ? DESC : ASC;

        // 排序
        String orderBy = sortName + " " + normalizedSortOrder;

        // 判断是否需要记录执行时间，只有在debug级别下才记录时间
        boolean shouldLogExecutionTime = log.isDebugEnabled();
        long startTime = 0;
        if (shouldLogExecutionTime) {
            startTime = System.currentTimeMillis();
        }

        // 使用try-with-resources来确保分页信息的清除
        long total;
        long totalPage;
        long currentPage;
        List<T> records;
        try (AutoCloseable ignored = PageHelper.startPage(page, pageSize, orderBy)) {
            // 执行查询
            Page<T> pageResult = query.execute();

            if (shouldLogExecutionTime) {
                long endTime = System.currentTimeMillis();
                log.debug("查询耗时：{}ms", endTime - startTime);
            }

            total = pageResult.getTotal();
            totalPage = pageResult.getPages();
            currentPage = pageResult.getPageNum();
            records = pageResult.getResult();
        } catch (Exception e) {
            // 这里不能只打印 message（可能为 null），要把堆栈打出来，便于定位真实原因
            log.error("分页查询失败", e);
            // 保留原始异常作为 cause，避免 e.getCause() 为 null 导致问题丢失
            throw new RuntimeException("分页查询失败", e);
        } finally {
            PageHelper.clearPage();
        }

        // 设置分页结果
        PageResult<T> res = new PageResult<>();
        res.setTotal(total); // 总数
        res.setTotalPage(totalPage);  // 总页数
        res.setCurrentPage(currentPage);  // 当前页
        res.setRecords(records);  // 数据

        return res;
    }

}