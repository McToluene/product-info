package com.mctoluene.productinformationmanagement.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PoiCellStyle {
    String fontName() default "Arial";

    short fontHeight() default 12;

    boolean bold() default true;
}
