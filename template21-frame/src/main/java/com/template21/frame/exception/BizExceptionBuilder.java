package com.template21.frame.exception;

import com.alibaba.cola.exception.BizException;

/**
 * @author qmf
 */
public class BizExceptionBuilder {

    public static BizException build(IBizExceptionEnumDefine errorDefine) {
        return new BizException(errorDefine.getErrorCode(), errorDefine.getErrorMessage());
    }

}
