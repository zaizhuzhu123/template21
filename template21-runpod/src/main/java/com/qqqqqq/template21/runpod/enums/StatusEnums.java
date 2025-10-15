package com.qqqqqq.template21.runpod.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author qmf
 */
@AllArgsConstructor
@Getter
public enum StatusEnums {

    IN_QUEUE("IN_QUEUE"),
    RUNNING("RUNNING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED"),
    TIMED_OUT("TIMED_OUT");

    private final String name;
}
