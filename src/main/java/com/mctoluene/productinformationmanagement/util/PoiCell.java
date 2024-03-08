package com.mctoluene.productinformationmanagement.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PoiCell {
    String USE_FIELD_NAME = "@USE_FIELD_NAME";

    String name() default USE_FIELD_NAME;

    int columnWidth() default 6000;

    String fallbackValue() default "";

    boolean autoSize() default true;

    PoiCellStyle headerStyle() default @PoiCellStyle;
}
