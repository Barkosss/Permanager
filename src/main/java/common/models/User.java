package common.models;

public class User {

    long userId;

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
}