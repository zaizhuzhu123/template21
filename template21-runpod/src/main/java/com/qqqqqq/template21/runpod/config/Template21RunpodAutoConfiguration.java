package com.qqqqqq.template21.runpod.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @author qmf
 */
@Configuration
@ComponentScan(value = "com.qqqqqq.template21.runpod")
@RequiredArgsConstructor
public class Template21RunpodAutoConfiguration {

}
