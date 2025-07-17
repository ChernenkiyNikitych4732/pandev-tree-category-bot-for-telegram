package com.chernenkiy.pandev_tree_category_bot_for_telegram.commands;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.chatStatus.MainChatStatus;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.exceptions.CategoryIsNotFound;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands.RemoveElementCommand;
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

class RemoveElementCommandTest {

    @Mock
    private TelegramBotUpdatesControl bot;

    @Mock
    private CategoryService categoryService;

    private Map<Long, MainChatStatus> chatStates;

    @InjectMocks
    private RemoveElementCommand removeElementCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatStates = new HashMap<>();
        removeElementCommand = new RemoveElementCommand(bot, categoryService, chatStates);
    }

    @Test
    void testExecute_FirstCall() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);

        removeElementCommand.execute(update);

        verify(bot).sendMessage(12345L, "Введите название категории, которую хотите удалить. Все её подкатегории также будут удалены.");
        assertEquals(MainChatStatus.Remove_element, chatStates.get(12345L));
    }

    @Test
    void testHandle_SuccessfulDeletion() {
        long chatId = 12345L;
        String categoryName = "КатегорияДляУдаления";

        when(categoryService.removeElement(categoryName)).thenReturn(categoryName);

        removeElementCommand.handle(chatId, categoryName, bot, null);

        verify(categoryService).removeElement(categoryName);
        verify(bot).sendMessage(chatId, "Категория и её подкатегории (при наличии) удалены: " + categoryName);
        assertNull(chatStates.get(chatId));
    }

    @Test
    void testHandle_CategoryNotFound() {
        long chatId = 12345L;
        String categoryName = "НесуществующаяКатегория";

        doThrow(CategoryIsNotFound.class).when(categoryService).removeElement(categoryName);

        removeElementCommand.handle(chatId, categoryName, bot, null);

        verify(categoryService).removeElement(categoryName);
        verify(bot).sendMessage(chatId, "Категория не найдена или не может быть удалена.");
        assertNull(chatStates.get(chatId));
    }

    @Test
    void testHandle_StateClearedAfterExecution() {
        long chatId = 12345L;
        String categoryName = "Категория";

        chatStates.put(chatId, MainChatStatus.Remove_element);

        when(categoryService.removeElement(categoryName)).thenReturn(categoryName);

        removeElementCommand.handle(chatId, categoryName, bot, null);

        verify(categoryService).removeElement(categoryName);
        verify(bot).sendMessage(chatId, "Категория и её подкатегории (при наличии) удалены: " + categoryName);

        assertFalse(chatStates.containsKey(chatId));
    }
}
