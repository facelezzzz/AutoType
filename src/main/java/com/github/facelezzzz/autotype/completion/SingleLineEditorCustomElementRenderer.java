package com.github.facelezzzz.autotype.completion;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@RequiredArgsConstructor
public class SingleLineEditorCustomElementRenderer implements EditorCustomElementRenderer {
    private final String content;

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        Editor editor = inlay.getEditor();
        FontMetrics fontMetrics = EditorUtils.getFontMetrics(EditorUtils.getFont(editor), editor);
        return fontMetrics.stringWidth(content);
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics graphics, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();
        if (editor.isDisposed()) {
            return;
        }
        graphics.setFont(EditorUtils.getFont(editor));
        graphics.setColor(JBColor.GRAY);
        int baseline = targetRegion.y + inlay.getEditor().getAscent();
        graphics.drawString(content, targetRegion.x, baseline);

    }
}
