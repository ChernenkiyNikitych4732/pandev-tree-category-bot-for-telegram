package com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.chatStatus.MainChatStatus;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramBotUpdatesControl extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesControl.class);

    private final String botUsername;
    private final String botToken;
    private final CategoryService categoryService;
    private final CommandRegistry commandRegistry;
    private final Map<Long, MainChatStatus> chatStatus = new HashMap<>();
    private Update lastUpdate;

    public TelegramBotUpdatesControl(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken,
            CategoryService categoryService,
            CommandRegistry commandRegistry) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.categoryService = categoryService;
        this.commandRegistry = commandRegistry;
        logger.info("Initializing TelegramBotUpdatesControl");
        registerCommands();
    }

    private void registerCommands() {
        logger.info("Registering commands");
        commandRegistry.registerCommand("/start", new StartCommand(this));
        commandRegistry.registerCommand("/addElement", new AddElementCommand(this, categoryService, chatStatus));
        commandRegistry.registerCommand("/removeElement", new RemoveElementCommand(this, categoryService, chatStatus));
        commandRegistry.registerCommand("/viewTree", new ViewTreeCommand(this, categoryService));
        commandRegistry.registerCommand("/help", new HelpCommand(this));
        commandRegistry.registerCommand("/download", new DownloadCommand(this, categoryService));
        commandRegistry.registerCommand("/upload", new UploadCommand(this, chatStatus, categoryService));
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.debug("Received update: {}", update);
        if (update.hasMessage() && (update.getMessage().hasText() || update.getMessage().hasDocument())) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            MainChatStatus status = chatStatus.getOrDefault(chatId, MainChatStatus.Default); // Fix here
            CommandHandler command = commandRegistry.getCommand(messageText);
            if (command != null) {
                logger.info("Executing command: {} for chatId: {}", messageText, chatId);
                command.execute(update);
            } else {
                logger.info("Handling command: {} for chatId: {}", messageText, chatId);
                handleCommand(chatId, status, messageText, update); // Updated here
            }
        }
    }


    private void handleCommand(long chatId, MainChatStatus chatState, String messageText, Update update) {
        logger.debug("Handling command for chatId: {}, state: {}, message: {}", chatId, chatState, messageText);
        CommandHandler commandHandler = new CommandFactory(commandRegistry, this, categoryService, chatStatus)
                .getCommandHandler(messageText, chatState);
        if (commandHandler != null) {
            logger.info("Executing handler for chatId: {}", chatId);
            commandHandler.handle(chatId, messageText, this, update);
        } else {
            logger.warn("No handler found for chatId: {} and message: {}", chatId, messageText);
            sendMessage(chatId, "Команда не распознана или не поддерживается в текущем состоянии.");
        }
    }

    public void setChatState(long chatId, MainChatStatus status) {
        logger.debug("Setting chat state for chatId: {} to state: {}", chatId, status);
        chatStatus.put(chatId, status);
    }

    public void sendMessage(long chatId, String text) {
        logger.debug("Sending message to chatId: {}: {}", chatId, text);
        try {
            execute(org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            logger.error("Error sending message to chatId: {}: {}", chatId, e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        logger.debug("Getting bot username");
        return botUsername;
    }

    @Override
    public String getBotToken() {
        logger.debug("Getting bot token");
        return botToken;
    }


    public Update getUpdate() {
        logger.debug("Getting last update");
        return lastUpdate;
    }

    public File getFile(String fileId) throws TelegramApiException {
        logger.debug("Getting file with fileId: {}", fileId);
        GetFile getFile = new GetFile(fileId);
        return execute(getFile);
    }
}
