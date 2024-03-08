package com.mctoluene.productinformationmanagement.util;

import org.apache.poi.ss.usermodel.*;

import com.mctoluene.productinformationmanagement.helper.UtilsHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

public class PoiExcelParser {

    public static <T> List<T> parseFromExcel(Sheet sheet, Class<T> modelClass) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        List<T> models = new ArrayList<>();

        if (sheet != null) {
            int i = 0;

            Map<Integer, String> nameColumnIndex = new HashMap<>();

            Constructor<T> constructor = modelClass.getConstructor();
            constructor.setAccessible(true);

            T model = constructor.newInstance();
            Field[] fields = model.getClass().getDeclaredFields();
            Map<String, Field> poiNameField = new HashMap<>();

            for (Field field : fields) {
                PoiCell poiCell = field.getDeclaredAnnotation(PoiCell.class);
                poiNameField.put(poiCell.name(), field);
            }

            for (Row row : sheet) {

                if (i == 0) {
                    int col = 0;
                    for (Cell cell : row) {
                        nameColumnIndex.put(col, cell.getStringCellValue());
                        col++;
                    }
                }

                if (row == null)
                    continue;
                if (isRowEmpty(row))
                    continue;

                if (i != 0) {

                    T newModel = constructor.newInstance();

                    int col = 0;
                    for (Cell cell : row) {
                        if (col >= 0) {

                            Field field = poiNameField.getOrDefault(nameColumnIndex.getOrDefault(col, null), null);
                            if (field == null) {
                                col++;
                                continue;
                            }
                            field.setAccessible(true);
                            PoiCell poiCell = field.getDeclaredAnnotation(PoiCell.class);

                            if (poiCell != null) {
                                Class<?> type = field.getType();

                                switch (cell.getCellTypeEnum()) {
                                    case NUMERIC -> {
                                        if (type == String.class) {
                                            field.set(newModel, UtilsHelper
                                                    .validateAndTrim(String.valueOf(cell.getNumericCellValue())));
                                            break;
                                        }
                                        if (type == Date.class) {
                                            field.set(newModel, cell.getDateCellValue());
                                            break;
                                        }
                                        if (DateUtil.isCellDateFormatted(cell))
                                            field.set(newModel, cell.getDateCellValue());
                                        else {
                                            double value = cell.getNumericCellValue();

                                            if (type.isPrimitive()) {
                                                if (type == int.class)
                                                    field.set(newModel, (int) value);
                                                else if (type == float.class)
                                                    field.set(newModel, (float) value);
                                                else if (type == long.class)
                                                    field.set(newModel, (long) value);
                                                else if (type == double.class)
                                                    field.set(newModel, (double) value);
                                                else
                                                    field.set(newModel, value);
                                            } else {
                                                if (type == Integer.class)
                                                    field.set(newModel, (int) value);
                                                else if (type == BigDecimal.class)
                                                    field.set(newModel, BigDecimal.valueOf(value));
                                                else if (type == Float.class)
                                                    field.set(newModel, (float) value);
                                                else if (type == Long.class)
                                                    field.set(newModel, (long) value);
                                                else if (type == Double.class)
                                                    field.set(newModel, (double) value);
                                                else
                                                    field.set(newModel, value);
                                            }
                                        }
                                    }
                                    case BOOLEAN -> field.set(newModel, cell.getBooleanCellValue());
                                    case STRING -> {
                                        if (type == String.class)
                                            field.set(newModel, UtilsHelper.validateAndTrim(cell.getStringCellValue()));
                                    }
                                }
                            }
                        }
                        col++;
                    }
                    models.add(newModel);
                }
                i++;
            }
        }

        return models;
    }

    public static boolean isRowEmpty(Row row) {
        boolean isEmpty = true;
        DataFormatter dataFormatter = new DataFormatter();
        if (row != null) {
            for (Cell cell : row) {
                if (dataFormatter.formatCellValue(cell).trim().length() > 0) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }
}
