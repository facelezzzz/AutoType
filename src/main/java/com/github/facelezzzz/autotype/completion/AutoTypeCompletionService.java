package com.github.facelezzzz.autotype.completion;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;

public interface AutoTypeCompletionService extends Disposable {

    static AutoTypeCompletionService getInstance() {
        return ApplicationManager.getApplication().getService(AutoTypeCompletionService.class);
    }

    void showComplete(Editor editor);

}
