package com.template21.frame.mybatisflex;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ReflectUtil;
import com.template21.frame.entity.BaseModel;

import java.time.LocalDateTime;

/**
 * <p>数据库插入、修改时的扩展点</p>
 *
 * @author apple
 */
public class InsertListener implements com.mybatisflex.annotation.InsertListener {

    @Override
    public void onInsert(Object o) {
        final LocalDateTime now = LocalDateTime.now();
        ReflectUtil.setFieldValue(o, LambdaUtil.getFieldName(BaseModel::getCreateTime), now);
        ReflectUtil.setFieldValue(o, LambdaUtil.getFieldName(BaseModel::getUpdateTime), now);
    }
}
