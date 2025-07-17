package com.chernenkiy.pandev_tree_category_bot_for_telegram.utils;

import java.io.*;

/**
 * Утилитный класс для работы с файлами.
 */

public class FileUtils {

    /**
     * Копирует файл с автоматическим закрытием потоков.
     * @param sourcePath путь к исходному файлу
     * @param destPath   путь к целевому файлу
     * @throws IOException если возникает ошибка при работе с файлами
     */

    public static void copyFile(String sourcePath, String destPath) throws IOException {
        try (InputStream inputStream = new FileInputStream(sourcePath);
             OutputStream outputStream = new FileOutputStream(destPath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Потоки автоматически закрываются здесь
    }
}