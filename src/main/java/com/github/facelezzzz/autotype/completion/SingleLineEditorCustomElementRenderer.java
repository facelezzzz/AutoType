package com.github.facelezzzz.autotype.completion;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.impl.FontInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.font.FontRenderContext;

@RequiredArgsConstructor
public class SingleLineEditorCustomElementRenderer implements EditorCustomElementRenderer {
    private final Editor editor;
    private final String content;

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        FontRenderContext fontRenderContext = FontInfo.getFontRenderContext(inlay.getEditor().getContentComponent());
        FontMetrics fontMetrics = FontInfo.getFontMetrics(getFont(editor, content), fontRenderContext);
        return fontMetrics.stringWidth(content);
    }

    private static Font getFont(Editor editor, String text) {
        Font font = editor.getColorsScheme().getFont(EditorFontType.PLAIN).deriveFont(Font.ITALIC);
        Font fallbackFont = UIUtil.getFontWithFallbackIfNeeded(font, text);
        return fallbackFont.deriveFont(fontSize(editor));
    }

    static float fontSize(Editor editor) {
        EditorColorsScheme scheme = editor.getColorsScheme();
        return (float)scheme.getEditorFontSize();
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        // 获取编辑器实例
        Editor editor = inlay.getEditor();
        if (editor.isDisposed()) {
            return;
        }
        // 获取要渲染的文本
        Graphics graphics = g.create();
        graphics.setFont(getFont(editor, content));
        graphics.setColor(JBColor.GRAY);
        int baseline = targetRegion.y + inlay.getEditor().getAscent();
        graphics.drawString(content, targetRegion.x, baseline);
        graphics.dispose();

    }
}
