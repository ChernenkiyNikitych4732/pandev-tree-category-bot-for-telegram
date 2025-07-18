package com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления командами бота.
 */

@Component
public class CommandRegistry {

    private final Map<String, CommandHandler> commands = new HashMap<>();

    /**
     * Регистрация команды.
     * @param commandName Имя команды ("/start" и т.д.).
     * @param handler Обработчик команды.
     */

    public void registerCommand(String commandName, CommandHandler handler) {
        commands.put(commandName, handler);
    }

    /**
     * Получение обработчика команды.
     * @param commandName Имя команды.
     * @return Обработчик команды или null, если команда не зарегистрирована.
     */

    public CommandHandler getCommand(String commandName) {
        return commands.get(commandName);
    }
}
