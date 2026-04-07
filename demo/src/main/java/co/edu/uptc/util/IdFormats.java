package co.edu.uptc.util;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Formatos obligatorios de identificadores: I000 (inversionistas), INV000 (inversiones), A000 (activos).
 */
public final class IdFormats {

    public static final Pattern INVESTOR_ID = Pattern.compile("^I\\d{3}$");
    public static final Pattern INVESTMENT_ID = Pattern.compile("^INV\\d{3}$");
    public static final Pattern ASSET_ID = Pattern.compile("^A\\d{3}$");

    private IdFormats() {
    }

    public static String normalizeInvestorId(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
    }

    public static String normalizeInvestmentId(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
    }

    public static String normalizeAssetId(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isValidInvestorId(String normalized) {
        return normalized != null && INVESTOR_ID.matcher(normalized).matches();
    }

    public static boolean isValidInvestmentId(String normalized) {
        return normalized != null && INVESTMENT_ID.matcher(normalized).matches();
    }

    public static boolean isValidAssetId(String normalized) {
        return normalized != null && ASSET_ID.matcher(normalized).matches();
    }
}
