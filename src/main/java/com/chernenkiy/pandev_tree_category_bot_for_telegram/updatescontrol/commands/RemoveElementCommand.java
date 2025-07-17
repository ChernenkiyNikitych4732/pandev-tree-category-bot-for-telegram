package com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.chatStatus.MainChatStatus;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.exceptions.CategoryIsNotFound;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Команда для удаления категории из дерева категорий.
 * Обрабатывает ввод пользователя, удаляя указанную категорию и все её подкатегории.
 */

public class RemoveElementCommand implements CommandHandler {
    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;
    private final Map<Long, MainChatStatus> chatStatus;

    /**
     * Конструктор команды RemoveElementCommand.
     * @param bot Экземпляр бота для отправки сообщений и управления состояниями.
     * @param categoryService Сервис для работы с категориями.
     * @param chatStatus Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
     */


    public RemoveElementCommand(TelegramBotUpdatesControl bot, CategoryService categoryService, Map<Long, MainChatStatus> chatStatus) {
        this.bot = bot;
        this.categoryService = categoryService;
        this.chatStatus = chatStatus;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        bot.sendMessage(chatId, "Введите название категории, которую хотите удалить. Все её подкатегории также будут удалены.");
        chatStatus.put(chatId, MainChatStatus.Remove_element);
    }

    /**
     * Обрабатывает удаление категории на основе пользовательского ввода.
     * @param chatId Идентификатор чата, в котором выполняется удаление.
     * @param messageText Название категории для удаления, отправленное пользователем.
     * @param bot Экземпляр бота для отправки сообщений пользователю.
     * @param update Последний update в боте.
     */

    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update) {
        try {
            String result = categoryService.removeElement(messageText);
            bot.sendMessage(chatId, "Категория и её подкатегории (при наличии) удалены: " + result);
        } catch (CategoryIsNotFound e) {
            bot.sendMessage(chatId, "Категория не найдена или не может быть удалена.");
        } finally {
            chatStatus.remove(chatId);
        }
    }
}
