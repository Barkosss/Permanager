package common.models;

public class User {

    long userId;

    // Объект с информацией ожидаемых ответов
    InputExpectation userInputExpectation;

    Interaction.Platform platform;

    public User(long userId) {
        this.userId = userId;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Interaction.Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Interaction.Platform platform) {
        this.platform = platform;
    }

    public InputExpectation getUserInputExpectation() {
        if (userInputExpectation == null) {
            userInputExpectation = new InputExpectation();
        }
        return userInputExpectation;
    }
}