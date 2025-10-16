package com.qqqqqq.template21.s3.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
public class S3UploadByUrlReq {

    @Schema(description = "目录")
    @NotEmpty
    private String dirKey;

    @Schema(description = "文件内容")
    @NotEmpty
    private String downloadUrl;
}
