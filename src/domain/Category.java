package domain;

import java.sql.Timestamp;

public class Category {

    private Long id;
    private Long userId;
    private String name;
    private CategoryType type;
    private Boolean isActive;
    private Timestamp createdAt;

    public Category(Long userId, String name, CategoryType type ) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.isActive = true;
    }

    public Category(Long id, Long userId, String name, CategoryType type, Boolean isActive, Timestamp createdAt){
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public CategoryType getType() { return type; }
    public Boolean getIsActive() { return isActive; }
    public Timestamp getCreatedAt() { return createdAt; }
}
