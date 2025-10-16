package com.qqqqqq.template21.s3.service.impl;

import cn.hutool.core.codec.Base62;
import cn.hutool.core.util.StrUtil;
import com.qqqqqq.template21.frame.constant.StrConstants;
import com.qqqqqq.template21.frame.util.FileDownloader;
import com.qqqqqq.template21.s3.config.Template21S3Config;
import com.qqqqqq.template21.s3.request.S3UploadByBytesReq;
import com.qqqqqq.template21.s3.request.S3UploadByUrlReq;
import com.qqqqqq.template21.s3.response.S3UploadRes;
import com.qqqqqq.template21.s3.service.IS3Service;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author qmf
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements IS3Service {

    public static final String INLINE_FILENAME_UTF_8 = "inline; filename*=utf-8''";
    public static final String NAME = "?name=";
    private final Template21S3Config s3Config;
    private final S3AsyncClient s3AsyncClient;

    @Override
    public S3UploadRes uploadByBytes(S3UploadByBytesReq req) {
        return uploadByBytes(req, null);
    }

    @Override
    public S3UploadRes uploadByBytes(S3UploadByBytesReq req, BiConsumer<CompletedEvent, Throwable> action) {
        return uploadByBytes(List.of(req), action).getFirst();
    }

    @Override
    public List<S3UploadRes> uploadByBytes(List<S3UploadByBytesReq> reqList) {
        return uploadByBytes(reqList, null);
    }

    @Override
    public List<S3UploadRes> uploadByBytes(List<S3UploadByBytesReq> reqList, BiConsumer<CompletedEvent, Throwable> action) {
        return getLionS3UploadRes(reqList, action);
    }

    private String getFileKey(String fileRealName, String fileNameKey) {
        if (StrUtil.isNotBlank(fileNameKey)) {
            return StrUtil.concat(true, fileNameKey);
        }
        return StrUtil.concat(true, UUID.randomUUID().toString(), ".", StrUtil.subAfter(fileRealName, StrConstants.DOT, true));
    }

    private String getS3Key(String dirKey, String fileKey) {
        return StrUtil.concat(true, dirKey, StrConstants.SLASH, fileKey);
    }

    @SneakyThrows
    @Override
    public S3UploadRes uploadByUrl(S3UploadByUrlReq req) {
        return uploadByUrl(req, null);
    }

    @SneakyThrows
    @Override
    public S3UploadRes uploadByUrl(S3UploadByUrlReq req, BiConsumer<CompletedEvent, Throwable> action) {
        final FileDownloader.FileBytes downloadObj = FileDownloader.download(req.getDownloadUrl());
        return uploadByBytes(S3UploadByBytesReq.builder().bytes(downloadObj.bytes()).fileRealName(downloadObj.fileName()).contentType(downloadObj.contentType()).dirKey(req.getDirKey()).build(), action);
    }

    @Override
    public List<S3UploadRes> uploadByUrl(List<S3UploadByUrlReq> reqList) {
        return uploadByUrl(reqList, null);
    }

    @Override
    public List<S3UploadRes> uploadByUrl(List<S3UploadByUrlReq> reqList, BiConsumer<CompletedEvent, Throwable> action) {
        return reqList.stream().map(s -> this.uploadByUrl(s, action)).toList();
    }

    private List<S3UploadRes> getLionS3UploadRes(List<S3UploadByBytesReq> reqList, BiConsumer<CompletedEvent, Throwable> action) {
        List<CompletableFuture<CompletedEvent>> allCompleteableFuture = new ArrayList<>();
        for (S3UploadByBytesReq uploadReq : reqList) {
            String fileKey = getFileKey(uploadReq.getFileRealName(), uploadReq.getFileNameKey());
            String s3Key = getS3Key(uploadReq.getDirKey(), fileKey);
            // 1. 创建PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(s3Config.getBucketName()).contentType(uploadReq.getContentType()).contentDisposition(getContentDisposition(fileKey)).key(s3Key).build();
            // 2. 创建异步请求体
            AsyncRequestBody requestBody = AsyncRequestBody.fromBytes(uploadReq.getBytes());
            // 3. 执行异步上传
            CompletableFuture<CompletedEvent> future = s3AsyncClient.putObject(putObjectRequest, requestBody).thenApply(r -> new CompletedEvent().setReq(uploadReq).setPutObjectResponse(r).setS3Key(s3Key)).whenComplete((response, exception) -> {
                if (exception != null) {
                    log.error("Upload failed: {}", exception.getMessage());
                }
                if (action != null) {
                    action.accept(response, exception);
                }
            });
            // 4. 添加回调处理
            allCompleteableFuture.add(future);
        }
        // 等待异步操作完成（实际应用中不需要这行，这里只是为了演示）
        CompletableFuture<Void> all = CompletableFuture.allOf(allCompleteableFuture.toArray(new CompletableFuture[0]));
        // 等待所有完成（阻塞）
        all.join();
        // 获取每一个任务的结果
        return allCompleteableFuture.stream().map(c -> {
            final CompletedEvent r = c.join();
            return new S3UploadRes().setFullUrl(getFullUrl(r.getS3Key(), r.getReq().getFileRealName()));
        }).toList();
    }

    @SneakyThrows
    private String getContentDisposition(String fileKey) {
        return INLINE_FILENAME_UTF_8 + URLEncoder.encode(fileKey, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public String getFullUrl(String s3key, String realName) {
        return StrUtil.concat(true, s3Config.getBucketProxy(), s3key, getUrlQuery(realName));
    }

    private String getUrlQuery(String realName) {
        return NAME + Base62.encode(realName);
    }

    @Data
    @Accessors(chain = true)
    public static class CompletedEvent {
        S3UploadByBytesReq req;
        String s3Key;
        PutObjectResponse putObjectResponse;
    }
}
