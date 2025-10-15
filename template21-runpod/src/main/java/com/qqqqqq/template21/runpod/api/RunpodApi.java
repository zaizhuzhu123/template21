package com.qqqqqq.template21.runpod.api;

import com.qqqqqq.template21.frame.retry.annotation.QueryRetryable;
import com.qqqqqq.template21.runpod.request.RunRequest;
import com.qqqqqq.template21.runpod.response.Response;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @author qmf
 */
@HttpExchange("https://api.runpod.ai/v2")
public interface RunpodApi {

    @PostExchange("/{endpointId}/run")
    Response run(@RequestHeader("Authorization") String authorization, @PathVariable String endpointId, @RequestBody RunRequest runRequest);

    @PostExchange("/{endpointId}/runsync")
    Response runsync(@RequestHeader("Authorization") String authorization, @PathVariable String endpointId, @RequestBody RunRequest runRequest);

    @GetExchange("/{endpointId}/status/{jobId}")
    @QueryRetryable
    Response status(@RequestHeader("Authorization") String authorization, @PathVariable String endpointId, @PathVariable String jobId);

    @PostExchange("/{endpointId}/cancel/{jobId}")
    Response cancel(@RequestHeader("Authorization") String authorization, @PathVariable String endpointId, @PathVariable String jobId);
}