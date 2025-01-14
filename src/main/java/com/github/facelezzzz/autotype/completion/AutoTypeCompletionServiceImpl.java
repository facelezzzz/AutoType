package com.github.facelezzzz.autotype.completion;

import com.github.facelezzzz.autotype.settings.AutoTypeSetting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.InlayModel;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.Alarm;
import okhttp3.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AutoTypeCompletionServiceImpl implements AutoTypeCompletionService {

    private static final Logger log = Logger.getInstance(AutoTypeCompletionServiceImpl.class);

    private final Alarm completeRequestAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);

    private final Object completeRequestAlarmLock = new Object();

    private static final Key<List<Inlay<?>>> INLAY_LIST_KEY = Key.create("inlay list");

    private static final Set<String> PAIR_END_CHARS = Set.of(")", "]", "}");

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    private static final OkHttpClient okhttpClient = new OkHttpClient.Builder()
            .build();

    private boolean shouldComplete(Editor editor) {
        int caretCount = editor.getCaretModel().getCaretCount();
        if (caretCount > 1) {
            return false;
        }
        String filepath = editor.getVirtualFile().getPath();
        String extension = FilenameUtils.getExtension(filepath);
        if (!StringUtils.equalsIgnoreCase(extension, "java")) {
            return false;
        }
        CaretModel caretModel = editor.getCaretModel();
        int caretModelOffset = caretModel.getOffset();
        if (caretModelOffset > editor.getDocument().getTextLength() - 1) {
            return false;
        }

        int lineStartOffset = editor.getDocument().getLineStartOffset(caretModel.getLogicalPosition().line);
        int lineEndOffset = editor.getDocument().getLineEndOffset(caretModel.getLogicalPosition().line);

        //caret at last
        if (caretModelOffset >= lineEndOffset) {
            return true;
        }

        //caret in mid of () [] {}
        if (caretModelOffset > lineStartOffset) {
            String text = editor.getDocument().getText(new TextRange(caretModelOffset, caretModelOffset + 1));
            return PAIR_END_CHARS.contains(text);
        }
        return false;
    }




    @Override
    public void showComplete(Editor editor) {
        clearInlays(editor);
        AutoTypeSetting setting = AutoTypeSetting.getInstance();
        if (!setting.isOllamaConfigured()) {
            log.warn("[AutoType] Ollama is not config...");
            return;
        }
        if (!shouldComplete(editor)) {
            return;
        }
        AutoTypeSetting.State settingState = setting.getState();
        Objects.requireNonNull(settingState);
        EditorSnapshot editorSnapshot = EditorSnapshot.getEditorSnapshot(editor);
        CompletePrompt prompt = genPrompt(editor);
        scheduleComplete(() -> {
            CompleteResponse completeResponse = getCompleteResponse(prompt, settingState);
            log.info(String.format("[AutoType]Completion:\n%s", completeResponse.getContent()));
            if (StringUtils.isBlank(completeResponse.getContent())) {
                return;
            }
            ApplicationManager.getApplication().invokeLater(() -> {
                WriteAction.run(() -> {
                    if (editorUnchanged(editor, editorSnapshot)) {
                        onCompleteResponse(editor, completeResponse);
                    }
                });
            });
        }, true);
    }

    private @NotNull CompleteResponse getCompleteResponse(CompletePrompt prompt, AutoTypeSetting.State settingState) {
        AutoTypeCompletionCache completionCache = AutoTypeCompletionCache.getInstance();
        CompleteResponse cacheCompleteResponse = completionCache.get(prompt);
        if (cacheCompleteResponse != null) {
            log.info(String.format("[AutoType] Hit cache completion:\n%s", cacheCompleteResponse.getContent()));
            return cacheCompleteResponse;
        }
        CompleteRequest completeRequest = getCompleteRequest(prompt, settingState);
        CompleteResponse completeResponse = requestComplete(completeRequest, settingState.ollamaApi);
        completionCache.put(prompt, completeResponse);
        return completeResponse;
    }

    private boolean editorUnchanged(Editor editor, EditorSnapshot oldEditorSnapshot) {
        return oldEditorSnapshot.equals(EditorSnapshot.getEditorSnapshot(editor));
    }

    public void clearInlays(Editor editor) {
        List<Inlay<?>> inlays = INLAY_LIST_KEY.get(editor);
        if (ObjectUtils.isNotEmpty(inlays)) {
            inlays.forEach(Disposable::dispose);
            inlays.clear();
        }
    }

    private void initInlayListKeyIfNotIn(Editor editor) {
        if (!INLAY_LIST_KEY.isIn(editor)) {
            INLAY_LIST_KEY.set(editor, new ArrayList<>());
        }
    }

    private void onCompleteResponse(Editor editor,
                                    CompleteResponse completeResponse) {
        initInlayListKeyIfNotIn(editor);
        List<Inlay<?>> inlays = INLAY_LIST_KEY.get(editor);
        InlayModel inlayModel = editor.getInlayModel();
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        log.info(String.format("[AutoType]on completion response:%s", completeResponse.getContent()));
        switch (completeResponse.getCompleteType()) {
            case LINE -> {
                SingleLineEditorCustomElementRenderer lineRenderer = new SingleLineEditorCustomElementRenderer(completeResponse.getContent());
                inlays.add(inlayModel.addInlineElement(offset, true, lineRenderer));
            }
            case BLOCK -> {
                List<String> completions = completeResponse.getCompletions();
                //first line
                SingleLineEditorCustomElementRenderer firstLineRenderer = new SingleLineEditorCustomElementRenderer(completions.get(0));
                inlays.add(inlayModel.addInlineElement(offset, true, firstLineRenderer));

                //remain lines
                MultiLineEditorCustomElementRenderer renderer = new MultiLineEditorCustomElementRenderer(completions.subList(1, completions.size()));
                inlays.add(inlayModel.addBlockElement(offset, true, false, Integer.MAX_VALUE, renderer));
            }
            default -> {
            }
        }
    }

    private CompleteResponse requestComplete(CompleteRequest request, String ollamaApi) {
        String requestBody = gson.toJson(request);
        log.info(String.format("[AutoType][OllamaCompleteRequest] request:%s", requestBody));
        try(Response response = okhttpClient.newCall(new Request.Builder()
                .url(ollamaApi)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build()).execute()) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String responseStr = responseBody.string();
                    log.info(String.format("[AutoType][OllamaCompleteResponse] response:%s", responseStr));
                    OllamaResponse ollamaResponse = gson.fromJson(responseStr, OllamaResponse.class);
                    return AutpTypeCompleteResponse.build(ollamaResponse.getResponse());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("补全失败");
    }

    private void scheduleComplete(Runnable runnable, boolean debounce) {
        synchronized (completeRequestAlarmLock) {
            this.completeRequestAlarm.cancelAllRequests();
            this.completeRequestAlarm.addRequest(runnable, debounce ? 200 : 0);
        }
    }

    private OllamaCompleteRequest getCompleteRequest(CompletePrompt prompt, AutoTypeSetting.State settingState) {
        //TODO 根据当前内容生成提示
        log.info(String.format("[AutoType] prompt:%s", prompt));
        return OllamaCompleteRequest.builder()
                .model(settingState.ollamaModelName)
                .prompt(prompt.getPrompt())
                .options(ImmutableMap.of("temperature", 0))
                .stream(false)
                .build();
    }

    private CompletePrompt genPrompt(Editor editor) {
        CaretModel caretModel = editor.getCaretModel();
        int caretModelOffset = caretModel.getOffset();

        int startOffset = Math.max(caretModelOffset - 500, 0);
        int endOffset = Math.min(caretModelOffset + 500, editor.getDocument().getTextLength() - 1);

        String prefix = editor.getDocument().getText(new TextRange(startOffset, caretModelOffset));
        String suffix = editor.getDocument().getText(new TextRange(caretModelOffset, endOffset));

        int lineStartOffset = editor.getDocument().getLineStartOffset(caretModel.getLogicalPosition().line);
        int lineEndOffset = editor.getDocument().getLineEndOffset(caretModel.getLogicalPosition().line);

        String currentLine = editor.getDocument().getText(new TextRange(lineStartOffset, lineEndOffset));
        //TODO 上下文级别的提示
        String prompt = "<|fim_prefix|>" +
                prefix +
                "<|fim_suffix|>" +
                suffix +
                "<|fim_middle|>";

        return CompletePrompt.builder()
                .prefix(prefix)
                .suffix(suffix)
                .prompt(prompt)
                .currentLine(currentLine)
                .build();
    }

    @Override
    public void dispose() {

    }
}
