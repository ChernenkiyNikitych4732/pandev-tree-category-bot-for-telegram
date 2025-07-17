package com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.chatStatus.MainChatStatus;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.exceptions.CategoryAlreadyExists;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Команда для добавления новой категории в дерево категорий.
 */

public class AddElementCommand implements CommandHandler {
    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;
    private final Map<Long, MainChatStatus> chatStatus;


    public AddElementCommand(TelegramBotUpdatesControl bot, CategoryService categoryService, Map<Long, MainChatStatus> chatStates) {
        this.bot = bot;
        this.categoryService = categoryService;
        this.chatStatus = chatStates;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        bot.sendMessage(chatId, "Введите название новой категории или укажите родителя и подкатегорию через пробел. Пример: 'категория подкатегория'.");
        chatStatus.put(chatId, MainChatStatus.Add_element);
    }

    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update) {
        String[] parts = messageText.split(" ", 2);
        try {
            if (parts.length == 1) {
                categoryService.addElement(parts[0], null);
                bot.sendMessage(chatId, "Новая категория добавлена: " + parts[0]);
            } else if (parts.length == 2 && !parts[1].contains(" ")) { // Убедимся, что подкатегория — одно слово
                categoryService.addElement(parts[0], parts[1]);
                bot.sendMessage(chatId, "Подкатегория добавлена: " + parts[1] + " к родителю " + parts[0]);
            } else {
                bot.sendMessage(chatId, "Ошибка! Введите название(-я) корректно!");
            }
        } catch (CategoryAlreadyExists e) {
            bot.sendMessage(chatId, "Ошибка: такая категория уже существует!");
        }
        chatStatus.remove(chatId);
    }
}
