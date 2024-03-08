package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.model.Property;
import com.mctoluene.productinformationmanagement.repository.PropertyRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.PropertyInternalService;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertyInternalServiceImpl implements PropertyInternalService {

    private final PropertyRepository propertyRepository;

    private final MessageSourceService messageSourceService;

    @Override
    public Property findPropertyByName(String name) {
        return propertyRepository.findByName(name)
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("property.not.found")));
    }
}
