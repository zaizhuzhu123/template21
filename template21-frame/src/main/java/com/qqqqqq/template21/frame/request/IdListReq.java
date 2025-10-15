package com.qqqqqq.template21.frame.request;

import com.alibaba.cola.dto.Command;
import com.qqqqqq.template21.frame.constant.NumberConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * @author qmf
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "id list")
public class IdListReq extends Command {

    @Schema(description = "id list", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = NumberConstants.ONE)
    private List<Long> idList;

}