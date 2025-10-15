package com.qqqqqq.template21.frame.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.qqqqqq.template21.frame.entity.BaseModel;
import com.qqqqqq.template21.frame.mybatisflex.InsertListener;
import com.qqqqqq.template21.frame.mybatisflex.UpdateListener;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author qmf
 */
@Configuration
@ComponentScan("com.qqqqqq.template21.frame")
public class FrameAutoConfiguration {

    @PostConstruct
    public void register() {
        FlexGlobalConfig.getDefaultConfig().registerInsertListener(new InsertListener(), BaseModel.class);
        FlexGlobalConfig.getDefaultConfig().registerUpdateListener(new UpdateListener(), BaseModel.class);
    }
}
