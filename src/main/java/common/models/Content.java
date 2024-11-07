package common.models;

import java.util.List;

public record Content(long chatId, String message, long createdAt, List<String> arguments) {

}