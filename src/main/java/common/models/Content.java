package common.models;

import java.util.List;

public record Content(Long userId, String message, long createdAt, List<String> arguments, Interaction.Platform platform) {}