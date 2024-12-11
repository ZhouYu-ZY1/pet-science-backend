package edu.ace.infinite.pojo;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
public final class PageResult<T> {
    private final Integer pageNum;
    private final Integer pageSize;
    private final Long total;
    private final List<T> list;
    private PageResult(@NotNull Integer pageNum, @NotNull
    Integer pageSize, @NotNull Long total, @Nullable List<T> list)
    {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }
    public static <T> PageResult<T> restPage(@NotNull Integer
                                                     pageNum, @NotNull Integer pageSize, @NotNull Long total,
                                             @Nullable List<T> list) {
        return new PageResult<>(pageNum, pageSize, total,
                Optional.ofNullable(list).orElse(Collections.emptyList()));
    }
    /**
     * 将 MyBatis 分页结果转化为通用结果
     *
     */
    public static <T> PageResult<T> restPage(@NotNull Page<T> page) {
        return new PageResult<>(page.getPageNum(),
                page.getPageSize(), page.getTotal(), page.getResult());
    }
    /**
     * 将 MyBatis 分页结果转化为通用结果
     *
     */
    public static <T> PageResult<T> restPage(@NotNull
                                             PageInfo<T> page) {
        return new PageResult<>(page.getPageNum(),
                page.getPageSize(), page.getTotal(), page.getList());
    }
}