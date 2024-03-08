package com.mctoluene.productinformationmanagement.service.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.model.Property;
import com.mctoluene.productinformationmanagement.repository.PropertyRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.PropertyInternalServiceImpl;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PropertyInternalServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private PropertyInternalServiceImpl propertyInternalService;

    @Test
    void findPropertyByNameTest() {
        Property property = new Property();
        property.setPublicId(UUID.randomUUID());
        property.setName("Property");

        when(propertyRepository.findByName(any())).thenReturn(Optional.of(property));

        var result = propertyInternalService.findPropertyByName("Property");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(property);
    }

    @Test
    void findPropertyByNameExceptionTest() {
        Property property = new Property();
        property.setPublicId(UUID.randomUUID());
        property.setName("Property");

        when(propertyRepository.findByName(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> propertyInternalService.findPropertyByName("Property"));

    }
}
