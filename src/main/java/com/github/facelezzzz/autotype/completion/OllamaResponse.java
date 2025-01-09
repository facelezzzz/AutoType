package com.github.facelezzzz.autotype.completion;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OllamaResponse {
    private String model;
    private String created_at;
    private String response;
    private boolean done;
    private String done_reason;
    private List<Object> context = new ArrayList<>();
    private float total_duration;
    private float load_duration;
    private float prompt_eval_count;
    private float prompt_eval_duration;
    private float eval_count;
    private float eval_duration;
}
