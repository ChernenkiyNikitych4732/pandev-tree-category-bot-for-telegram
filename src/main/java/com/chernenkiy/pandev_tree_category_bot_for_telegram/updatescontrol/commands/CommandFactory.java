package com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.chatStatus.MainChatStatus;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;

import java.util.Map;

/**
 * Фабрика для создания обработчиков команд в зависимости от текущего состояния чата.
 */

public class CommandFactory {
    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;
    private final Map<Long, MainChatStatus> chatStatus;

    /**
     * Конструктор для создания фабрики команд.
     * @param commandRegistry Реестр команд.
     * @param bot Экземпляр бота для взаимодействия с Telegram API.
     * @param categoryService Сервис для управления категориями.
     * @param chatStatus Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
     */

    public CommandFactory(
            CommandRegistry commandRegistry,
            TelegramBotUpdatesControl bot,
            CategoryService categoryService,
            Map<Long, MainChatStatus> chatStatus) {
        this.bot = bot;
        this.categoryService = categoryService;
        this.chatStatus = chatStatus;
    }

    public CommandHandler getCommandHandler(String commandName, MainChatStatus chatState) {
        switch (chatState) {
            case Add_element -> {
                return new AddElementCommand(bot,categoryService,chatStatus);
            }
            case Remove_element -> {
                return new RemoveElementCommand(bot, categoryService, chatStatus);
            }
            case Upload_file -> {
                return new UploadCommand(bot, chatStatus, categoryService);
            }
            default -> {
                return null;
            }
        }
    }
}
