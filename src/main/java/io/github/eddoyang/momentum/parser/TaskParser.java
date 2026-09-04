package io.github.eddoyang.momentum.parser;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClientAsync;
import com.anthropic.models.beta.messages.MessageCreateParams;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class TaskParser {

    private static final int TITLE_MAX = 255;
    
    @Value("${momentum.parser.model}")
    private String model;

    private AnthropicClient client;

    // ---------------- Tool schema ----------------

    @JsonClassDescription("Record the structured task extracted from the user's phrase")
    static class RecordTask {
        @JsonPropertyDescription("The task itself, with time and category words removed")
        public String title;

        @JsonPropertyDescription("Local ISO-8601 datetime with no offset, or null if no time was mentioned")
        public String deadline;

        @JsonPropertyDescription("An existing category name, or null")
        public String category;
    }

    // ---------------- Public API ----------------

    public ParsedTask parse(String input, List<String> categories, ZonedDateTime now) {
        var params = MessageCreateParams.builder()
                .model(model)
                .maxTokens(512L)
                .system(systemPrompt(now, categories))
                .addUserMessage(input)
                .addTool(RecordTask.class)
                .build();

        RecordTask out = client.beta().messages().create(params).content().stream()
                .flatMap(block -> block.toolUse().stream())
                .findFirst()
                .map(block -> block.input(RecordTask.class))
                .orElseThrow(() -> new IllegalStateException("Model returned no structured tasks"));
        
        return new ParsedTask(clampTitle(out.title), parseDeadline(out.deadline), matchCategory(out.category, categories));
    }

    // ---------------- Helpers ----------------

    private AnthropicClient client() {
        if (client == null) client = AnthropicOkHttpClient.fromEnv();
        return client;
    }
    
    private String systemPrompt(ZonedDateTime now, List<String> categories) {
        return """
                You convert a short phrase into a structured task.

                Current local data and time: %s (%s)
                Today is %s

                Existing categories (choose one of these exactly, or null):
                %s

                Rules:
                - title: the task itself, with the time and category words remove. Keep the user's own wording. Do not add words they did not write.
                - deadline: local ISO-8601, no timezone offset, e.g. 2026-09-04T13:00:00. Null if the text mentions no time at all. Do not invent a deadline.
                - A bare time with no date means the next occurence of that time.
                - A bare date with no time means 09:00 on that date.
                - category: one of the existing categories above, matched loosely (a user typing "work" matches "Work"). Null if nothing fits. Never invent a new category.
                """.formatted(
                    now.toLocalDateTime(),
                    now.getZone(),
                    now.getDayOfWeek(),
                    categories.isEmpty() ? "(none yet)" : String.join("\n", categories)
                );
    }

    static String clampTitle(String raw) {
        if (raw == null || raw.isBlank())
            throw new IllegalStateException("No task title found");

        String title = raw.strip();
        
        return title.length() > TITLE_MAX ? title.substring(0, TITLE_MAX) : title;
    }

    static LocalDateTime parseDeadline(String raw) {
        if (raw == null || raw.isBlank() || raw.equalsIgnoreCase("null")) 
            return null;

        try {
            return LocalDateTime.parse(raw);

        } catch (DateTimeParseException e) {
            return null;
        }
    }

    static String matchCategory(String raw, List<String> categories) {
        if (raw == null)
            return null;

        return categories.stream().filter(c -> c.equalsIgnoreCase(raw.strip())).findFirst().orElse(null);
    }

    
}
