package com.github.facelezzzz.autotype.completion;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.impl.FontInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EditorUtils {

    public static Font getFont(@NotNull Editor editor) {
        return editor.getColorsScheme().getFont(EditorFontType.PLAIN).deriveFont(fontSize(editor));
    }

    public static float fontSize(Editor editor) {
        EditorColorsScheme scheme = editor.getColorsScheme();
        return (float) scheme.getEditorFontSize();
    }


    public static FontMetrics getFontMetrics(Font font, @NotNull Editor editor) {
        return FontInfo.getFontMetrics(font, FontInfo.getFontRenderContext(editor.getContentComponent()));
    }

}
