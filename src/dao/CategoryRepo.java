package dao;

import domain.Category;

import java.util.List;

public interface CategoryRepo {
    void save(Category category);

    List<Category> listActiveCategories(Long userId);
}
