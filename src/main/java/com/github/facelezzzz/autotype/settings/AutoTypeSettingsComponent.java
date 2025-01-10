package com.github.facelezzzz.autotype.settings;

import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;

import javax.swing.*;

public class AutoTypeSettingsComponent {

    private final JPanel jPanel;

    @Getter
    private final JBTextField ollamaApiTextField = new JBTextField();

    @Getter
    private final JBTextField ollamaModelNameTextField = new JBTextField();

    public AutoTypeSettingsComponent() {
        this.jPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Ollama api:", ollamaApiTextField, 1, false)
                .addLabeledComponent("Ollama model name:", ollamaModelNameTextField, 1, false)
                .getPanel();
    }

    public JPanel getPanel() {
        return jPanel;
    }

}
