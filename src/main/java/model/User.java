package model;

public class User {
    private Integer id;
    private String vkId;
    private String name;
    private boolean censored; // true - не ругается матом, false - ругается

    public User(Integer id, String vkId, String name, boolean censored) {
        this.id = id;
        this.vkId = vkId;
        this.name = name;
        this.censored = censored;
    }
}
