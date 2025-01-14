package com.github.facelezzzz.autotype.completion;

import org.apache.commons.collections4.map.LRUMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AutoTypeCompletionCacheImpl implements AutoTypeCompletionCache {


    private static final LRUMap<CompletePrompt, CompleteResponse> LRU_MAP = new LRUMap<>(20);

    @Override
    public void put(CompletePrompt prompt, CompleteResponse completeResponse) {
        LRU_MAP.put(prompt, completeResponse);
    }


    @Override
    public CompleteResponse get(CompletePrompt prompt) {
        for (Map.Entry<CompletePrompt, CompleteResponse> entry : LRU_MAP.entrySet()) {
            CompletePrompt cachedPrompt = entry.getKey();
            CompleteResponse cachedResponse = entry.getValue();

            String acceptedPrefix = getAcceptedPrefix(cachedPrompt, cachedResponse);
            if (acceptedPrefix.contains(prompt.getPrefix())) {
                //trim response
                String newContent = trimmed(acceptedPrefix, prompt.getPrefix());
                return AutpTypeCompleteResponse.build(newContent);
            }
        }
        return null;
    }

    private static String trimmed(String content, String str) {
        int i = content.indexOf(str);
        if (i < 0) {
            return "";
        }
        int beginIndex = i + str.length();
        if (beginIndex < content.length()) {
            return content.substring(beginIndex);
        }
        return "";
    }

    private static @NotNull String getAcceptedPrefix(CompletePrompt cachedPrompt, CompleteResponse cachedResponse) {
        return cachedPrompt.getPrefix() + cachedResponse.getContent();
    }


}
