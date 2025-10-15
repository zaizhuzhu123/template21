package com.template21.frame.mybatisflex;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ReflectUtil;
import com.template21.frame.entity.BaseModel;

import java.time.LocalDateTime;

/**
 * @author qmf
 */
public class UpdateListener implements com.mybatisflex.annotation.UpdateListener {

    @Override
    public void onUpdate(Object o) {
        ReflectUtil.setFieldValue(o, LambdaUtil.getFieldName(BaseModel::getUpdateTime), LocalDateTime.now());
    }
}
