package com.qqqqqq.template21.s3.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author qmf
 */
@Data
@Schema(description = "s3上传对象")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class S3UploadByBytesReq {

    @Schema(description = "目录")
    @NotEmpty
    private String dirKey;

    @Schema(description = "文件真实名称")
    @NotEmpty
    private String fileRealName;

    @Schema(description = "文件内容")
    @NotNull
    private byte[] bytes;

    @Schema(description = "文件contentType")
    @NotEmpty
    private String contentType;

    @Schema(description = "自定义文件的名称")
    private String fileNameKey;

}
