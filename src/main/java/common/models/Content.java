package common.models;

import java.util.List;

public record Content(String message, long createdAt, List<String> arguments) {}