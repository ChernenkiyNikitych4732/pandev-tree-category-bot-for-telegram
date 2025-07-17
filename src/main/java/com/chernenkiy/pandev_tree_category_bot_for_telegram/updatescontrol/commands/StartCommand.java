package com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Команда для отображения главного меню.
 */

public class StartCommand implements CommandHandler {
    private final TelegramBotUpdatesControl bot;
    public StartCommand(TelegramBotUpdatesControl bot) {
        this.bot = bot;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        bot.sendMessage(chatId, "Привет! Добро пожаловать в Telegram-бот PanDev Tree Category Bot от компании PanDev. Я бот, который позволяет вам работать с деревом категорий. Введите команду /help, чтобы получить список доступных для вас команд");
    }

    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update) {}
}
