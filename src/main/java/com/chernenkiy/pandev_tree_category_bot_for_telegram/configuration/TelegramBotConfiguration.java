package com.chernenkiy.pandev_tree_category_bot_for_telegram.configuration;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands.CommandRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfiguration {


    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public TelegramBotUpdatesControl telegramBot(TelegramBotsApi botsApi, CommandRegistry commandRegistry, CategoryService categoryService, @Value("${telegram.bot.username}") String username, @Value("${telegram.bot.token}") String token) throws TelegramApiException {
        TelegramBotUpdatesControl bot = new TelegramBotUpdatesControl(username, token, categoryService, commandRegistry);
        botsApi.registerBot(bot);
        System.out.println("Бот был успешно зарегистрирован!");
        return bot;
    }
}
