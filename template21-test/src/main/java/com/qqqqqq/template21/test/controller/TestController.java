package com.qqqqqq.template21.test.controller;


import com.alibaba.cola.dto.Response;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qqqqqq.template21.frame.response.ResponseBuilder;
import com.qqqqqq.template21.frame.retry.IRetryService;
import com.qqqqqq.template21.runpod.api.RunpodApi;
import com.qqqqqq.template21.runpod.request.RunRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qmf
 */
@Data
@Tag(name = "Test")
@RequestMapping("/web/test")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final RedissonClient redissonClient;
    private final RunpodApi runpodApi;
    private final IRetryService retryService;

    @JsonProperty("endpoint")
    private String endpoint;
    @JsonProperty("params")
    private ParamsDTO params;

    @Operation(summary = "test")
    @GetMapping("/test")
    public Response test() {
//        final RBucket<String> testBlock = redissonClient.getBucket("testBlock_" + SystemClock.now());
//        testBlock.set("test");

        String token = "Bearer " + "PCLTQZWH3UP9MXGUS5OWRJ3IXHVXCPRXUPZBUKJE";
        String endpointId = "yo5didn1kmrlfb";
        final RunRequest runRequest = new RunRequest();
        runRequest.setInput(new ParamsDTO());
        final com.qqqqqq.template21.runpod.response.Response runResponse = runpodApi.run(token, endpointId, runRequest);
        final com.qqqqqq.template21.runpod.response.Response statusResponse = runpodApi.status(token, endpointId, runResponse.getId());

//        final com.qqqqqq.template21.runpod.response.Response statusResponse = retryService.query(() -> runpodApi.status(token, endpointId, runResponse.getId()));
        return ResponseBuilder.success();
    }

    @NoArgsConstructor
    @Data
    public static class ParamsDTO {
        @JsonProperty("prompt")
        private String prompt;
        @JsonProperty("negative_prompt")
        private String negativePrompt;
        @JsonProperty("seed")
        private Integer seed;
        @JsonProperty("subseed")
        private Integer subseed;
        @JsonProperty("n_iter")
        private Integer nIter;
        @JsonProperty("batch_size")
        private Integer batchSize;
        @JsonProperty("save_images")
        private Boolean saveImages;
        @JsonProperty("base_model")
        private String baseModel;
        @JsonProperty("steps")
        private Integer steps;
        @JsonProperty("cfg_scale")
        private Integer cfgScale;
        @JsonProperty("width")
        private Integer width;
        @JsonProperty("height")
        private Integer height;
        @JsonProperty("sampler_scheduler")
        private String samplerScheduler;
        @JsonProperty("scheduler")
        private String scheduler;
        @JsonProperty("override_settings")
        private OverrideSettingsDTO overrideSettings;
        @JsonProperty("alwayson_scripts")
        private AlwaysonScriptsDTO alwaysonScripts;
        @JsonProperty("HD")
        private Boolean hd;
        @JsonProperty("lora_downloads")
        private List<LoraDownloadsDTO> loraDownloads;
        @JsonProperty("denoising_strength")
        private Double denoisingStrength;

        @NoArgsConstructor
        @Data
        public static class OverrideSettingsDTO {
            @JsonProperty("sd_vae")
            private String sdVae;
            @JsonProperty("sd_model_checkpoint")
            private String sdModelCheckpoint;
            @JsonProperty("CLIP_stop_at_last_layers")
            private Integer clipStopAtLastLayers;
        }

        @NoArgsConstructor
        @Data
        public static class AlwaysonScriptsDTO {
            @JsonProperty("ADetailer")
            private ADetailerDTO aDetailer;

            @NoArgsConstructor
            @Data
            public static class ADetailerDTO {
                @JsonProperty("args")
                private List<Boolean> args;
            }
        }

        @NoArgsConstructor
        @Data
        public static class LoraDownloadsDTO {
            @JsonProperty("filePath")
            private String filePath;
            @JsonProperty("fileDownloadUrl")
            private String fileDownloadUrl;
        }
    }
}
