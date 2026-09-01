package com.perfectkode.bikri.common.utils;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");
    private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private SlugUtils() {
        // Utility class constructor prevention
    }

    /**
     * Converts a string into a base URL-friendly slug.
     */
    public static String toSlug(String input) {
        if (Objects.isNull(input) || input.isBlank()) {
            return "";
        }

        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = MULTI_HYPHEN.matcher(slug).replaceAll("-");

        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Generates a unique slug by resolving collisions against a database existence check.
     *
     * @param input The raw product name.
     * @param existsFunction A function/lambda (e.g., productRepository::existsBySlug) returning true if slug exists.
     * @return A unique slug guaranteed not to exist in DB.
     */
    public static String generateUniqueSlug(String input, Predicate<String> existsFunction) {
        String baseSlug = toSlug(input);

        if (baseSlug.isBlank()) {
            baseSlug = "product";
        }

        String candidateSlug = baseSlug;

        // If base slug does not exist in DB, use it directly
        if (!existsFunction.test(candidateSlug)) {
            return candidateSlug;
        }

        // Loop until a unique collision-free slug is generated
        while (existsFunction.test(candidateSlug)) {
            String randomSuffix = generateRandomSuffix(4);
            candidateSlug = baseSlug + "-" + randomSuffix;
        }

        return candidateSlug;
    }

    private static String generateRandomSuffix(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
