package com.chernenkiy.pandev_tree_category_bot_for_telegram.service.implementations;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.entity.Category;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.exceptions.CategoryAlreadyExists;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.exceptions.CategoryIsNotFound;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.repository.CategoryRepository;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Реализация сервиса для управления деревом категорий.
 */

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public String viewTree() {
        List<Category> parentCategories = categoryRepository.findAll().stream()
                .filter(category -> category.getParent() == null)
                .toList();

        if (parentCategories.isEmpty()) {
            return "Дерево категорий пусто.";
        }

        StringBuilder builder = new StringBuilder();
        for (Category parentCategory : parentCategories) {
            Hibernate.initialize(parentCategory.getChildren());
            buildTreeView(builder, parentCategory, 0);
        }
        return builder.toString();
    }

    private void buildTreeView(StringBuilder builder, Category category, int depth) {
        builder.append("  ".repeat(depth)).append("--").append(category.getName()).append("\n");

        if (!category.getChildren().isEmpty()) {
            for (Category child : category.getChildren()) {
                buildTreeView(builder, child, depth + 1);
            }
        }
    }

    @Transactional
    @Override
    public Category addElement(String parent, String child) {
        if (child == null) {
            if (categoryRepository.existsByName(parent)) {
                throw new CategoryAlreadyExists();
            }
            Category newParent = new Category();
            newParent.setName(parent);
            return categoryRepository.save(newParent);
        }

        Category parentCategory = categoryRepository.findByName(parent)
                .orElseGet(() -> {
                    Category newParent = new Category();
                    newParent.setName(parent);
                    return categoryRepository.save(newParent);
                });

        if (categoryRepository.existsByName(child)) {
            throw new CategoryAlreadyExists();
        }

        Category childCategory = new Category();
        childCategory.setName(child);
        childCategory.setParent(parentCategory);
        parentCategory.getChildren().add(childCategory);

        categoryRepository.save(childCategory);

        return parentCategory;
    }

    @Transactional
    @Override
    public String removeElement(String element) {
        Category category = categoryRepository.findByName(element)
                .orElseThrow(CategoryIsNotFound::new);

        categoryRepository.delete(category);

        return "Категория и её подкатегории удалены: " + element;
    }

    /**
     * Возвращает список строк с доступными командами бота и их описанием.
     * Можно дополнить по мере добавления новых команд.
     */
    @Transactional
    @Override
    public List<String> getHelp() {
        return Arrays.asList(
                "/add_parent <ИмяКатегории> - Добавить новую родительскую категорию",
                "/add_child <ИмяРодителя> <ИмяДочернейКатегории> - Добавить дочернюю категорию к родительской",
                "/view_tree - Показать текущее дерево категорий",
                "/remove <ИмяКатегории> - Удалить категорию и все её подкатегории",
                "/help - Показать это сообщение с доступными командами"
        );
    }

    @Transactional
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

}