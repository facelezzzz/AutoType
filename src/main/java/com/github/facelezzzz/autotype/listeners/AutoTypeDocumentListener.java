package com.github.facelezzzz.autotype.listeners;

import com.github.facelezzzz.autotype.completion.AutoTypeCompletionService;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class AutoTypeDocumentListener implements DocumentListener {

    private final Editor editor;

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        Project project = editor.getProject();
        if (project != null && project.isInitialized()) {
            AutoTypeCompletionService autoTypeCompletionService = AutoTypeCompletionService.getInstance();
            autoTypeCompletionService.showComplete(editor);
        }
    }
}
