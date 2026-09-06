package parser;

import java.time.LocalDateTime;

public record ParsedTask(String title, LocalDateTime deadline, String category) {}
