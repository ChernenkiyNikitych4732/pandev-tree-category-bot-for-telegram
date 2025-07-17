package com.chernenkiy.pandev_tree_category_bot_for_telegram.commands;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.chatStatus.MainChatStatus;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.exceptions.CategoryAlreadyExists;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands.AddElementCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddElementCommandTest {

    @Mock
    private TelegramBotUpdatesControl bot;

    @Mock
    private CategoryService categoryService;

    private Map<Long, MainChatStatus> chatStates;

    @InjectMocks
    private AddElementCommand addElementCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatStates = new HashMap<>();
        addElementCommand = new AddElementCommand(bot, categoryService, chatStates);
    }

    @Test
    void testExecute() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);
        addElementCommand.execute(update);
        verify(bot).sendMessage(12345L, "Введите название новой категории или укажите родителя и подкатегорию через пробел. Пример: 'категория подкатегория'.");
        assertEquals(MainChatStatus.Add_element, chatStates.get(12345L));
    }

    @Test
    void testHandle_AddNewCategory() {
        long chatId = 12345L;
        String messageText = "Новая категория";
        Update update = mock(Update.class);
        assertDoesNotThrow(() -> addElementCommand.handle(chatId, messageText, bot, update));
    }

    @Test
    void testHandle_AddNewSubCategory() {
        long chatId = 12345L;
        String messageText = "Родитель Подкатегория";
        Update update = mock(Update.class);
        addElementCommand.handle(chatId, messageText, bot, update);
        try {
            verify(categoryService).addElement("Родитель", "Подкатегория");
            verify(bot).sendMessage(chatId, "Подкатегория добавлена: Подкатегория к родителю Родитель");
        } catch (CategoryAlreadyExists e) {
            fail("Exception should not have been thrown");
        }

        assertFalse(chatStates.containsKey(chatId));
    }

    @Test
    void testHandle_InvalidInput() {
        long chatId = 12345L;
        String messageText = "Неверный Ввод Текста";
        Update update = mock(Update.class);
        addElementCommand.handle(chatId, messageText, bot, update);
        verify(bot).sendMessage(chatId, "Ошибка! Введите название(-я) корректно!");
        assertFalse(chatStates.containsKey(chatId));
    }

    @Test
    void testHandle_CategoryAlreadyExists() {
        long chatId = 12345L;
        String messageText = "Существующая Категория";
        Update update = mock(Update.class);
        doThrow(CategoryAlreadyExists.class).when(categoryService).addElement("Существующая" ,"Категория");
        addElementCommand.handle(chatId, messageText, bot, update);
        verify(bot).sendMessage(chatId, "Ошибка: такая категория уже существует!");
        assertFalse(chatStates.containsKey(chatId));
    }
}
