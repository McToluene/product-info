package com.mctoluene.productinformationmanagement.service.strategy;

import com.google.gson.Gson;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.repository.BrandRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.BrandInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ManufacturerInternalService;
import com.mctoluene.productinformationmanagement.service.strategy.BrandMessageQueueConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BrandMessageQueueConverterTest {

    static MessageContentEvent contentEvent;
    @InjectMocks
    private static BrandMessageQueueConverter brandMessageQueueConverter;
    @Mock
    private MessageSourceService messageSourceService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private BrandInternalService brandService;
    @Mock
    private ManufacturerInternalService manufacturerInternalService;
    Manufacturer manufacturer;
    CreateBrandRequestDto createBrandRequestDto;

    @Mock
    static Gson gson;

    @BeforeEach
    public void init() {
        contentEvent = MessageContentEvent.builder()
                .typeMessage(TypeMessage.BRAND)
                .data(createBrandRequestDto)
                .build();
        manufacturer = Manufacturer.builder()
                .manufacturerName("Mock manufacturer name")
                .description("mock description manufacturer")
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void whenGetNotificationServiceInvoked_ThenException() {
        contentEvent = MessageContentEvent.builder().data(new Object()).build();
        assertThrows(ValidatorException.class, () -> brandMessageQueueConverter.sendNotification(contentEvent));
    }

    /*
     * @Test
     * void whenExecuteProccess_ThenSaveBrand() {
     * Brand brandToSave = Brand.builder()
     * .brandName("Mock name 1")
     * .description("Mock description 1")
     * .manufacturer(manufacturer)
     * .status(Status.ACTIVE)
     * .build();
     * 
     * when(brandService.findByBrandName(brandToSave.getBrandName())).thenReturn(
     * Optional.empty());
     * when(manufacturerInternalService.findByPublicId(UUID.randomUUID())).
     * thenReturn(Optional.of(manufacturer));
     * 
     * CreateBrandRequestDto dto = CreateBrandRequestDto.builder()
     * .brandName(brandToSave.getBrandName())
     * .description(brandToSave.getDescription())
     * .createdBy("test")
     * .status(Status.ACTIVE)
     * .build();
     * List<CreateBrandRequestDto> listSent = Arrays.asList(dto);
     * brandMessageQueueConverter.executeProcess(listSent);
     * 
     * verify(brandService, times(1)).saveBrandToDb(any());
     * 
     * }
     */

    /*
     * @Test
     * void whenExecuteProccess_ThenException() {
     * List<CreateBrandRequestDto> list = new ArrayList<>();
     * Brand mock = Brand.builder()
     * .brandName("Mock name 2")
     * .description("Mock description 2")
     * .manufacturer(manufacturer)
     * .status(Status.ACTIVE)
     * .build();
     * 
     * CreateBrandRequestDto dto = CreateBrandRequestDto.builder()
     * .brandName("Mock name 2")
     * .description("Mock description 2")
     * .manufacturerId(UUID.randomUUID())
     * .status(Status.ACTIVE)
     * .build();
     * list.add(dto);
     * 
     * when(brandService.findByBrandName(mock.getBrandName())).thenReturn(Optional.
     * of(mock));
     * brandMessageQueueConverter.executeProcess(list);
     * 
     * assertEquals(brandService.findByBrandName(mock.getBrandName()).get().
     * getBrandName(), mock.getBrandName());
     * }
     */
}
