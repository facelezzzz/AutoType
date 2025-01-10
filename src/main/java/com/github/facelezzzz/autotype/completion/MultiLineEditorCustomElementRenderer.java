package com.github.facelezzzz.autotype.completion;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class MultiLineEditorCustomElementRenderer implements EditorCustomElementRenderer {

    private final List<String> completions;

    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        return inlay.getEditor().getLineHeight() * completions.size();
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        String content = getMaxLengthCompletion(completions);
        Editor editor = inlay.getEditor();
        FontMetrics fontMetrics = EditorUtils.getFontMetrics(EditorUtils.getFont(editor), editor);
        return fontMetrics.stringWidth(content);
    }

    private String getMaxLengthCompletion(List<String> completions) {
        return Collections.max(completions, Comparator.comparing(String::length));
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics2D graphics, @NotNull Rectangle2D targetRegion, @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();
        if (editor.isDisposed()) {
            return;
        }
        Font font = EditorUtils.getFont(editor);
        graphics.setFont(font);
        graphics.setColor(JBColor.GRAY);
        int baseline = (int) (targetRegion.getY() + inlay.getEditor().getAscent());
        for (String hint : completions) {
            graphics.drawString(hint, (float) targetRegion.getX(), baseline);
            baseline += editor.getLineHeight();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }



}
