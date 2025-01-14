package com.github.facelezzzz.autotype.completion;

import com.intellij.openapi.application.ApplicationManager;

public interface AutoTypeCompletionCache {

    static AutoTypeCompletionCache getInstance() {
        return ApplicationManager.getApplication().getService(AutoTypeCompletionCache.class);
    }

    void put(CompletePrompt prompt, CompleteResponse completeResponse);

    CompleteResponse get(CompletePrompt prompt);


}
