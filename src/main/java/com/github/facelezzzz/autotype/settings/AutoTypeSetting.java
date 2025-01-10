package com.github.facelezzzz.autotype.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.github.facelezzzz.autotype.settings.AutoTypeSetting",
        storages = @Storage("AutoTypePlugin.xml")
)
public class AutoTypeSetting implements PersistentStateComponent<AutoTypeSetting.State> {

    private State state = new State();

    public static AutoTypeSetting getInstance() {
        return ApplicationManager.getApplication().getService(AutoTypeSetting.class);
    }

    @Override
    public @Nullable AutoTypeSetting.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public static class State {
        public String ollamaModelName = "";
        public String ollamaApi = "";
    }

    public boolean isOllamaConfigured() {
        return StringUtils.isNotEmpty(state.ollamaApi) && StringUtils.isNotEmpty(state.ollamaModelName);
    }


}
