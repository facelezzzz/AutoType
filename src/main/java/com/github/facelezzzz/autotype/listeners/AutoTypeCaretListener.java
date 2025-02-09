package com.github.facelezzzz.autotype.listeners;

import com.github.facelezzzz.autotype.completion.AutoTypeCompletionService;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AutoTypeCaretListener implements CaretListener {

    @Override
    public void caretPositionChanged(@NotNull CaretEvent event) {
        Editor editor = event.getEditor();
        Project project = editor.getProject();
        if (project != null && project.isInitialized()) {
            AutoTypeCompletionService autoTypeCompletionService = AutoTypeCompletionService.getInstance();
            autoTypeCompletionService.showComplete(editor);
        }
    }

}
