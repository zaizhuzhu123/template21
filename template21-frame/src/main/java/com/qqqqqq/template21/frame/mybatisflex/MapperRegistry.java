package com.qqqqqq.template21.frame.mybatisflex;

import com.mybatisflex.core.BaseMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qmf
 */
@Component
public class MapperRegistry implements ApplicationContextAware {

    private static final Map<Class<?>, Object> MAPPER_MAP = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, BaseMapper> mappers = context.getBeansOfType(BaseMapper.class);
        for (BaseMapper mapper : mappers.values()) {
            Class<?> entityType = resolveEntityClass(mapper.getClass());
            MAPPER_MAP.put(entityType, mapper);
        }
    }

    public static <T> BaseMapper<T> getMapper(Class<T> entityClass) {
        return (BaseMapper<T>) MAPPER_MAP.get(entityClass);
    }

    // 解析泛型里的实体类型（你可以根据 BaseMapper<T> 接口来推断）
    private Class<?> resolveEntityClass(Class<?> mapperClass) {
        final Type type = ((Class<?>) mapperClass.getGenericInterfaces()[0]).getGenericInterfaces()[0];
        if (type instanceof ParameterizedType parameterizedType) {
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            if (actualTypes.length > 0) {
                return (Class<?>) actualTypes[0];
            }
        }
        return null;
    }
}
