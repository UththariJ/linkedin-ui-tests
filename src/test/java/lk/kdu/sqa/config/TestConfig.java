package lk.kdu.sqa.config;

public final class TestConfig {
    private TestConfig() { }
    public static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing environment variable: " + key + ". Use scripts/run-tests.ps1.");
        }
        return value;
    }
    public static boolean hasCredentials() {
        return hasValue("LINKEDIN_USERNAME") && hasValue("LINKEDIN_PASSWORD");
    }
    private static boolean hasValue(String key) {
        String value = System.getenv(key);
        return value != null && !value.isBlank();
    }
    public static String searchTerm() {
        String value = System.getenv("LINKEDIN_SEARCH_TERM");
        return value == null || value.isBlank() ? "software testing" : value;
    }
}
