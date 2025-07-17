package com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.entity.Category;

import java.util.List;

public interface CategoryService {
    String viewTree();

    Category addElement(String parent, String child);

    String removeElement(String element);

    List<String> getHelp();

    List<Category> getAllCategories();

}
