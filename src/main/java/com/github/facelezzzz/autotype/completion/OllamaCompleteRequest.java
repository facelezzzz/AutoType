package com.github.facelezzzz.autotype.completion;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class OllamaCompleteRequest implements CompleteRequest {
    private String model;
    private String prompt;
    private Map<String, Object> options;
    private boolean stream;
}
