package com.mctoluene.productinformationmanagement.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringSanitizerUtils {

    private static String removeNewLinesTabsAndSpaces(String input) {
        if (Objects.isNull(input)) {
            return null;
        }
        return input.replaceAll("\\s+", " ");
    }

    private static String removeNewLinesAndTabs(String input) {
        if (Objects.isNull(input)) {
            return null;
        }
        return input.replaceAll("[\\n\\r\\t]+", "");
    }

    public static String sanitizeInput(String input) {
        String result = removeNewLinesTabsAndSpaces(input);
        return removeNewLinesAndTabs(result);
    }

}
