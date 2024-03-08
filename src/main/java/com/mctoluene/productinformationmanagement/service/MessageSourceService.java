package com.mctoluene.productinformationmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSourceService {

    private final MessageSource messageSource;

    public String getMessageByKey(String messageKey, String... params) {
        return messageSource.getMessage(messageKey,
                params,
                LocaleContextHolder.getLocale());
    }
}
