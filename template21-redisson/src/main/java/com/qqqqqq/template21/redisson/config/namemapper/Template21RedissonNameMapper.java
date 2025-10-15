package com.qqqqqq.template21.redisson.config.namemapper;

import com.qqqqqq.template21.frame.constant.StrConstants;
import org.redisson.api.NameMapper;

/**
 * @author qmf
 */
public class Template21RedissonNameMapper implements NameMapper {

    private final String prefix;

    public Template21RedissonNameMapper(String prefix) {
        this.prefix = prefix.toUpperCase() + StrConstants.COMMA;
    }

    @Override
    public String map(String name) {
        return prefix + name;
    }

    @Override
    public String unmap(String name) {
        if (name.startsWith(prefix)) {
            return name.substring(prefix.length());
        }
        return name;
    }
}
