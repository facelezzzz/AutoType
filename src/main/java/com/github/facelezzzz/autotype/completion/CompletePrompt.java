package com.github.facelezzzz.autotype.completion;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class CompletePrompt {

    private String prefix;
    private String suffix;
    private String prompt;
    private String currentLine;

}
