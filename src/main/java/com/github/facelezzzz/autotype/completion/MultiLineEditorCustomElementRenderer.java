package com.github.facelezzzz.autotype.completion;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.impl.FontInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.ui.DebuggerColors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class MultiLineEditorCustomElementRenderer implements EditorCustomElementRenderer {

    private static final Logger log = Logger.getInstance(MultiLineEditorCustomElementRenderer.class);

    private final Editor editor;

    private final CompleteResponse completeResponse;

    private final LogicalPosition caretLogicalPosition;

    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        return editor.getLineHeight() * completeResponse.getCompletions().size();
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        String content = getMaxLengthCompletion(completeResponse);
        FontMetrics fontMetrics = getFontMetrics(getFont(editor), editor);
        return fontMetrics.stringWidth(content);
    }

    private String getMaxLengthCompletion(CompleteResponse completeResponse) {
        return Collections.max(completeResponse.getCompletions(), Comparator.comparing(String::length));
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics2D g, @NotNull Rectangle2D targetRegion, @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();
        if (editor.isDisposed()) {
            return;
        }
        Graphics2D graphics = g;
        List<String> hints = completeResponse.getCompletions();
        Font font = getFont(editor);
        graphics.setFont(font);
        graphics.setColor(JBColor.GRAY);
        Point caretPoint = editor.logicalPositionToXY(caretLogicalPosition);
        int baseline = (int) (targetRegion.getY() + inlay.getEditor().getAscent()) - editor.getLineHeight();
        log.info(String.format("[AutoType] point:(%s,%s) region:(%s,%s)", caretPoint.x, caretPoint.y, targetRegion.getX(), targetRegion.getY()));
        for (int i = 0; i < hints.size(); i++) {
            if (i == 0) {
                graphics.drawString(hints.get(i), caretPoint.x, baseline);
            }else {
                graphics.drawString(hints.get(i), (int) targetRegion.getX(), baseline);
            }
            baseline += editor.getLineHeight();
        }
        graphics.dispose();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    private static Font getFont(@NotNull Editor editor) {
        EditorColorsScheme colorsScheme = editor.getColorsScheme();
        TextAttributes attributes = editor.getColorsScheme().getAttributes(DebuggerColors.INLINED_VALUES_EXECUTION_LINE);
        int fontStyle = attributes == null ? Font.PLAIN : attributes.getFontType();
        return UIUtil.getFontWithFallback(colorsScheme.getFont(EditorFontType.forJavaStyle(fontStyle)));
    }

    private static FontMetrics getFontMetrics(Font font, @NotNull Editor editor) {
        return FontInfo.getFontMetrics(font, FontInfo.getFontRenderContext(editor.getContentComponent()));
    }

}
