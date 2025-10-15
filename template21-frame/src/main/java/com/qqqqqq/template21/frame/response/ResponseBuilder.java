package com.qqqqqq.template21.frame.response;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.mybatisflex.core.paginate.Page;

import java.util.Collection;
import java.util.List;

/**
 * @author qmf
 */
public class ResponseBuilder {

    private ResponseBuilder() {
        // 禁止实例化
    }

    public static Response success() {
        return Response.buildSuccess();
    }

    public static Response failure(String errCode, String errMessage) {
        return Response.buildFailure(errCode, errMessage);
    }

    public static <T> SingleResponse<T> success(T data) {
        return SingleResponse.of(data);
    }

    public static <T> MultiResponse<T> success(List<T> data) {
        return MultiResponse.of(data);
    }

    public static <T> PageResponse<T> success(Collection<T> data, int totalCount, int pageSize, int pageIndex) {
        return PageResponse.of(data, totalCount, pageSize, pageIndex);
    }

    public static <T> PageResponse<T> success(Page<T> page) {
        return PageResponse.of(page.getRecords(), (int) page.getTotalRow(), (int) page.getPageSize(), (int) page.getPageNumber());
    }
}
