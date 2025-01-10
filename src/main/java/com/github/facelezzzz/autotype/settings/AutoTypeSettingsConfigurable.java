package com.github.facelezzzz.autotype.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class AutoTypeSettingsConfigurable implements Configurable {

    private AutoTypeSettingsComponent component;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "AutoType";
    }

    @Override
    public boolean isModified() {
        AutoTypeSetting.State state = AutoTypeSetting.getInstance().getState();
        Objects.requireNonNull(state);

        String ollamaModelName = component.getOllamaModelNameTextField().getText();
        String ollamaApi = component.getOllamaApiTextField().getText();
        return modified(state.ollamaApi, ollamaApi) || modified(state.ollamaModelName, ollamaModelName);
    }

    private boolean modified(String a, String b) {
        return !StringUtils.equals(a, b);
    }

    @Override
    public void apply() throws ConfigurationException {
        AutoTypeSetting.State state = AutoTypeSetting.getInstance().getState();
        Objects.requireNonNull(state);
        state.ollamaApi = component.getOllamaApiTextField().getText();
        state.ollamaModelName = component.getOllamaModelNameTextField().getText();
    }

    @Override
    public void reset() {
        AutoTypeSetting.State state = AutoTypeSetting.getInstance().getState();
        Objects.requireNonNull(state);
        state.ollamaApi = "";
        state.ollamaModelName = "";
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }


    @Nullable
    @Override
    public JComponent createComponent() {
        component = new AutoTypeSettingsComponent();
        return component.getPanel();
    }
}
