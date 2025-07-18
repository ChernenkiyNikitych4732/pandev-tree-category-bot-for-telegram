package com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для обработки команд бота.
 */

public interface CommandHandler {
    void execute(Update update);
    void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update);
}