package com.qqqqqq.template21.s3.service;

import com.qqqqqq.template21.s3.request.S3UploadByBytesReq;
import com.qqqqqq.template21.s3.request.S3UploadByUrlReq;
import com.qqqqqq.template21.s3.response.S3UploadRes;
import com.qqqqqq.template21.s3.service.impl.S3ServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author qmf
 */
public interface IS3Service {

    S3UploadRes uploadByBytes(S3UploadByBytesReq req);

    S3UploadRes uploadByBytes(@NotNull @Valid S3UploadByBytesReq req, BiConsumer<S3ServiceImpl.CompletedEvent, Throwable> action);

    List<S3UploadRes> uploadByBytes(List<S3UploadByBytesReq> reqList);

    List<S3UploadRes> uploadByBytes(@NotEmpty List<@NotNull @Valid S3UploadByBytesReq> reqList, BiConsumer<S3ServiceImpl.CompletedEvent, Throwable> action);

    S3UploadRes uploadByUrl(S3UploadByUrlReq req);

    @SneakyThrows
    S3UploadRes uploadByUrl(@NotNull @Valid S3UploadByUrlReq req, BiConsumer<S3ServiceImpl.CompletedEvent, Throwable> action);

    List<S3UploadRes> uploadByUrl(List<S3UploadByUrlReq> reqList);

    List<S3UploadRes> uploadByUrl(@NotEmpty List<@NotNull @Valid S3UploadByUrlReq> reqList, BiConsumer<S3ServiceImpl.CompletedEvent, Throwable> action);
}
