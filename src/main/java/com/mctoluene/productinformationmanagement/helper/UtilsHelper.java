package com.mctoluene.productinformationmanagement.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.domain.enums.FileTypeExtension;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.commons.response.AppResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UtilsHelper {

    public static String parseErrorString(String error) {

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        AppResponse errorResponse = null;

        String formattedError = error.replace("//", "");

        String patternString = "\\{\"status\":400,\"message\":\"[^\"]+\",\"supportDescriptiveMessage\":\"[^\"]+\",\"error\":\"[^\"]+\"\\}";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(formattedError);

        if (matcher.find()) {
            json = matcher.group();
            log.info("json {}", json);
        }

        try {
            errorResponse = objectMapper.readValue(json, AppResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return errorResponse.getError().toString();
    }

    public static boolean validateFileType(String fileType) {

        return StringUtils.isNotBlank(fileType) &&
                FileTypeExtension.getFileTypeExtension(fileType).isPresent();
    }

    public static void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new UnProcessableEntityException("File cannot be null or empty");
        }

        int fileExtIndex = originalFilename.lastIndexOf(".");

        if (fileExtIndex == -1 || fileExtIndex == originalFilename.length() - 1) {
            throw new UnProcessableEntityException("File does not have a valid extension");
        }

        String fileType = originalFilename.substring(fileExtIndex + 1);

        if (!validateFileType(fileType)) {
            throw new UnProcessableEntityException("Invalid File Format :: " + fileType);
        }

    }

    public static String validateAndTrim(String columnValue) {
        return StringUtils.isNotBlank(columnValue) ? columnValue.trim() : columnValue;
    }

}
