package domain;

public class Category {

    private Long id;
    private Long userId;
    private String name;
    private CategoryType type;

    public Category(Long userId, String name, CategoryType type ) {
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    public Category(Long id, Long userId, String name, CategoryType type){
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public CategoryType getType() { return type; }
}
