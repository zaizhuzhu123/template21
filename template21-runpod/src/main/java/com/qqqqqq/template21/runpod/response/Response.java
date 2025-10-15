package com.qqqqqq.template21.runpod.response;

import com.qqqqqq.template21.runpod.enums.StatusEnums;
import lombok.Data;

/**
 * @author qmf
 */
@Data
public class Response {

    private Long delayTime;
    private Long executionTime;
    private String id;
    private StatusEnums status;
    private String output;
}
