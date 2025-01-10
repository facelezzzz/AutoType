package com.github.facelezzzz.autotype.listeners;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

public class AutoTypeEditorCreatedListener implements EditorFactoryListener {

    private static final String AUTO_TYPE_EDITOR_LISTENER_DISPOSABLE_DEBUG_NAME = "autoTypeEditorListener";

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        Project project = editor.getProject();
        if (project != null && !project.isDisposed()) {
            Disposable editorDisposable = Disposer.newDisposable(AUTO_TYPE_EDITOR_LISTENER_DISPOSABLE_DEBUG_NAME);
            EditorUtil.disposeWithEditor(editor, editorDisposable);
            editor.getCaretModel().addCaretListener(new AutoTypeCaretListener(), editorDisposable);
            editor.getDocument().addDocumentListener(new AutoTypeDocumentListener(editor), editorDisposable);
        }
    }

}
