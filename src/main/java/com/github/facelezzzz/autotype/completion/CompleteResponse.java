package com.github.facelezzzz.autotype.completion;

import java.util.List;

public interface CompleteResponse {

    CompleteType getCompleteType();

    List<String> getCompletions();

    String getContent();

    enum CompleteType{
        LINE,
        BLOCK,
        ;
    }
}
