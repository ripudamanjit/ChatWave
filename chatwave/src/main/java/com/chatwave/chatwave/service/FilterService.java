package com.chatwave.chatwave.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class FilterService {

    private List<String> badWords = new ArrayList<>();

    private Node root;

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        Node fail;
        String output; // Stores the word if this node is the end of a bad word

        boolean isEndOfWord() {
            return output != null;
        }
    }

    @PostConstruct
    public void init() {
        loadBadWords();
        buildTrie();
        buildFailLinks();
    }

    private void loadBadWords() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/bad_words.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    badWords.add(line.trim().toLowerCase());
                }
            }
            System.out.println("Loaded " + badWords.size() + " bad words from file.");
        } catch (Exception e) {
            System.err.println("Could not load bad_words.txt: " + e.getMessage());
            // Fallback if file is missing
            badWords = Arrays.asList("idiot", "stupid"); 
        }
    }

    private void buildTrie() {
        root = new Node();
        for (String word : badWords) {
            Node current = root;
            for (char c : word.toLowerCase().toCharArray()) {
                current = current.children.computeIfAbsent(c, k -> new Node());
            }
            current.output = word;
        }
    }

    private void buildFailLinks() {
        Queue<Node> queue = new LinkedList<>();
        
        // Level 1 nodes' fail links point to root
        for (Node node : root.children.values()) {
            node.fail = root;
            queue.add(node);
        }

        while (!queue.isEmpty()) {
            Node u = queue.poll();

            for (Map.Entry<Character, Node> entry : u.children.entrySet()) {
                char c = entry.getKey();
                Node v = entry.getValue();
                Node f = u.fail;

                while (f != null && !f.children.containsKey(c)) {
                    f = f.fail;
                }
                
                v.fail = (f == null) ? root : f.children.get(c);
                
                // If the fail link points to another bad word, "merge" them
                if (v.fail.output != null && v.output == null) {
                    // This is an optimization for overlapping words
                }
                
                queue.add(v);
            }
        }
    }

    public String filterMessage(String content) {
        if (content == null || content.isEmpty()) return "";

        StringBuilder result = new StringBuilder(content);
        Node current = root;
        char[] chars = content.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = Character.toLowerCase(chars[i]);

            while (current != root && !current.children.containsKey(c)) {
                current = current.fail;
            }

            current = current.children.getOrDefault(c, root);

            // Check if we found a match (or multiple overlapping matches)
            Node temp = current;
            while (temp != root) {
                if (temp.isEndOfWord()) {
                    int wordLen = temp.output.length();
                    int start = i - wordLen + 1;
                    // Replace with asterisks
                    for (int k = start; k <= i; k++) {
                        result.setCharAt(k, '*');
                    }
                }
                temp = temp.fail;
            }
        }

        return result.toString();
    }
}
