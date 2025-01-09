package com.github.facelezzzz.autotype.completion;


import com.github.weisj.jsvg.S;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.TextRange;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class EditorSnapshot {
    private Long docModificationStamp;
    private String docPath;
    private Integer caretCount;
    private LogicalPosition caretPosition;

    public static EditorSnapshot getEditorSnapshot(Editor editor) {
        LogicalPosition logicalPosition = editor.getCaretModel().getLogicalPosition();
        Document document = editor.getDocument();
        return EditorSnapshot.builder()
                .docModificationStamp(document.getModificationStamp())
                .caretCount(editor.getCaretModel().getCaretCount())
                .caretPosition(logicalPosition)
                .build();
    }
}
