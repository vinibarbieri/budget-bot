package domain;

public class Category {

    private Long id;
    private Long userId;
    private String name;

    public Category(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Category(Long id, Long userId, String name){
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
}
