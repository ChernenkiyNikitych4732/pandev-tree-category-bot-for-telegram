package com.chernenkiy.pandev_tree_category_bot_for_telegram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Категория не найдена!")
public class CategoryIsNotFound extends RuntimeException {
    public CategoryIsNotFound() {
        super("Такой категории не существует!");
    }
}