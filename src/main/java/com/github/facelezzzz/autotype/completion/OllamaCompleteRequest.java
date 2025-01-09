package com.github.facelezzzz.autotype.completion;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OllamaCompleteRequest {
    private String model;
    private String prompt;
    private boolean stream;
}
