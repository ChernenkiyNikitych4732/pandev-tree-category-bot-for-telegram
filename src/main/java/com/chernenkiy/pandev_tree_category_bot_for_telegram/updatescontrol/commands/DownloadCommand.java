package com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.commands;

import com.chernenkiy.pandev_tree_category_bot_for_telegram.entity.Category;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.service.services.CategoryService;
import com.chernenkiy.pandev_tree_category_bot_for_telegram.updatescontrol.TelegramBotUpdatesControl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DownloadCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;

    public DownloadCommand(TelegramBotUpdatesControl bot, CategoryService categoryService) {
        this.bot = bot;
        this.categoryService = categoryService;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        File file = null;
        try {
            file = generateExcelFile();
            SendDocument document = SendDocument.builder()
                    .chatId(String.valueOf(chatId))
                    .document(new InputFile(file, "categories_tree.xlsx"))
                    .caption("Дерево категорий")
                    .build();

            bot.execute(document);

        } catch (Exception e) {
            bot.sendMessage(chatId, "Ошибка при генерации или отправке файла: " + e.getMessage());
            e.printStackTrace(); // Optional: use a proper logger in production
        } finally {
            if (file != null && file.exists()) {
                if (!file.delete()) {
                    System.err.println("Не удалось удалить временный файл: " + file.getAbsolutePath());
                }
            }
        }
    }

    public File generateExcelFile() throws IOException {
        File file = File.createTempFile("categories_tree", ".xlsx");
        List<Category> categories = categoryService.getAllCategories();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Categories");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Категория");
            headerRow.createCell(1).setCellValue("Родительская категория");

            int rowIndex = 1;
            for (Category category : categories) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(category.getName());
                String parentName = category.getParent() != null ? category.getParent().getName() : "";
                row.createCell(1).setCellValue(parentName);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }

        return file;
    }

    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update) {
        execute(update);
    }
}
