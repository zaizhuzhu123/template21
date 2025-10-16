package com.qqqqqq.template21.s3.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author qmf
 */
@Data
@Accessors(chain = true)
@Schema(description = "上传文件返回信息")
public class S3UploadRes {

    @Schema(description = "完整的URL访问地址")
    private String fullUrl;
}