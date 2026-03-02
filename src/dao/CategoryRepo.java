package dao;

import domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo {
    void save(Category category);

    List<Category> listActiveCategories(Long userId);

    Optional<Category> findByNameAndUser(String name, Long userId);
}
