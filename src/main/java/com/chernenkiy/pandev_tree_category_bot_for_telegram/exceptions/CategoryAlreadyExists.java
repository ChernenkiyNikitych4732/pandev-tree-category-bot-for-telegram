package com.chernenkiy.pandev_tree_category_bot_for_telegram.exceptions;

public class CategoryAlreadyExists extends RuntimeException {
    public CategoryAlreadyExists() {
        super("Категория уже существует !");
    }
}
