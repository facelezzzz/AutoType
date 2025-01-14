package com.github.facelezzzz.autotype.completion;

import com.google.common.base.Splitter;

import java.util.List;

public class AutpTypeCompleteResponse implements CompleteResponse {

    private CompleteType completeType;

    private String content;

    private List<String> completions;

    public static AutpTypeCompleteResponse build(String content) {
        AutpTypeCompleteResponse response = new AutpTypeCompleteResponse();
        List<String> completions = Splitter.on("\n").omitEmptyStrings().splitToList(content);
        response.completions = completions;
        response.completeType = completions.size() > 1 ?
                CompleteResponse.CompleteType.BLOCK : CompleteResponse.CompleteType.LINE;
        response.content = content;
        return response;

    }

    @Override
    public CompleteType getCompleteType() {
        return completeType;
    }

    @Override
    public List<String> getCompletions() {
        return completions;
    }

    @Override
    public String getContent() {
        return content;
    }
}
