package io.github.bumasello.financas.config;

/**
 * Settings
 */
public class Settings {
    private static final String TELEGRAM_KEY = System.getenv("TELEGRAM_KEY");
    private static final String CHAT_ID = System.getenv("CHAT_ID");
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASS = System.getenv("DB_PASS");

    public static String getDbUrl() {
        if (DB_URL == null || DB_URL.isBlank()) {
            throw new IllegalStateException("O DB_URL não pode estar vazio");
        }
        return DB_URL;
    }

    public static String getDbUser() {
        if (DB_USER == null || DB_USER.isBlank()) {
            throw new IllegalStateException("O DB_USER não pode estar vazio");
        }
        return DB_USER;
    }

    public static String getDbPass() {
        if (DB_PASS == null || DB_PASS.isBlank()) {
            throw new IllegalStateException("O DB_PASS não pode estar vazio");
        }
        return DB_PASS;
    }

    public static long getChatId() {
        if (CHAT_ID == null || CHAT_ID.isBlank()) {
            throw new IllegalStateException("O CHAT_ID não pode estar vazio");
        }

        return Long.parseLong(CHAT_ID);
    }

    public static String getTelegramKey() {
        if (TELEGRAM_KEY == null || TELEGRAM_KEY.isBlank()) {
            throw new IllegalStateException("O token TELEGRAM_KEY não pode estar vazio");
        }

        return TELEGRAM_KEY;
    }

}
