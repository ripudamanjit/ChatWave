package com.chatwave.chatwave.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class FilterService {


    private static final List<String> BAD_WORDS = Arrays.asList("idiot", "stupid", "badword");

    public String filterMessage(String content) {
        if (content == null) return "";

        String filteredContent = content;
        for (String word : BAD_WORDS) {
            // (?i)(ignores uppercase/lowercase)
            filteredContent = filteredContent.replaceAll("(?i)" + word, "*".repeat(word.length()));
        }
        return filteredContent;
    }
}