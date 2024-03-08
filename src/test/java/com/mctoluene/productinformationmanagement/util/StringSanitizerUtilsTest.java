package com.mctoluene.productinformationmanagement.util;

import org.junit.jupiter.api.Test;

import com.mctoluene.productinformationmanagement.helper.VariantHelper;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StringSanitizerUtilsTest {

    @Test
    void sanitizeInput() {
        String input = "New Brand's \nTest ";
        String result = StringSanitizerUtils.sanitizeInput(input);
        assertEquals("New Brand's Test", result.trim());
    }

    @Test
    void sanitizeInput_null() {
        String input = null;
        String result = StringSanitizerUtils.sanitizeInput(input);
        assertNull(result);
    }
}