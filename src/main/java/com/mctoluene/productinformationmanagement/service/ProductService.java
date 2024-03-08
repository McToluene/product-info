package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.queuemessage.CategoryUploadTemplateRequest;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ImageUploadTemplateRequest;
import com.mctoluene.productinformationmanagement.domain.queuemessage.PriceTemplateRequest;
import com.mctoluene.productinformationmanagement.domain.queuemessage.StockUpdateTemplateRequest;
import com.mctoluene.productinformationmanagement.domain.request.product.CreateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.UpdateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.product.ProductResponse;
import com.mctoluene.productinformationmanagement.filter.search.ProductFilter;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    AppResponse createNewProduct(CreateProductRequestDto requestDto, Boolean createWithoutVariants, String countryCode);

    AppResponse getProductsByProductCategoryId(UUID productCategoryId, Integer page, Integer size);

    AppResponse getAllProducts(Integer page, Integer size, String searchParam, String fromDate, String toDate,
            List<UUID> categoryPublicIds,
            List<UUID> brandPublicIds, List<UUID> manufacturerPublicIds, List<UUID> warrantyTypePublicIds,
            List<UUID> measuringUnitPublicIds);

    AppResponse getProductsByProductCategoryIds(List<UUID> categoriesPublicList, Integer page, Integer size);

    AppResponse deleteProduct(UUID publicId);

    AppResponse updateProductArchiveStatus(UUID publicId, String status);

    AppResponse updateProductByPublicId(UUID publicId, UpdateProductRequestDto updateProductRequestDto);

    AppResponse getProductsByProductCategory(UUID categoryId);

    AppResponse getApprovedProductsByPublicIdList(List<UUID> prodPublicId);

    AppResponse getApprovedProductsPublicIdListUsingSku(List<String> skuList);

    AppResponse getProductsByBrand(UUID brandPublicId);

    AppResponse getProductByPublicId(UUID publicId);

    List<ImageUploadTemplateRequest> saveUploadProductVariants(List<ImageUploadTemplateRequest> imageTemplateRequests,
            String createdBy, UUID countryId);

    List<ImageUploadTemplateRequest> saveUploadProductVariants(List<ImageUploadTemplateRequest> imageTemplateRequests,
            String createdBy, UUID countryId, Product product);

    void savePriceTemplateRequest(List<PriceTemplateRequest> priceTemplateRequests, String createdBy);

    void saveStockUpdateTemplateRequest(List<StockUpdateTemplateRequest> stockUpdateTemplateRequests, String createdBy);

    AppResponse uploadProductUsingExcel(MultipartFile fluxFilePart, String createdBy, UUID traceId) throws IOException;

    void saveCategoryUploadTemplateRequest(List<CategoryUploadTemplateRequest> categoryUploadTemplateRequests,
            String createdBy);

    AppResponse createProductCatalogue(UUID traceId, UUID warehouseId, UUID stateId, UUID cityId, UUID lgaId,
            String searchValue, Integer page, Integer size);

    ByteArrayResource download(Integer page, Integer size, String searchParam, String fromDate, String toDate,
            List<UUID> categoryPublicIds, List<UUID> brandPublicIds, List<UUID> manufacturerPublicIds,
            List<UUID> warrantyTypePublicIds, List<UUID> measuringUnitPublicIds);

    AppResponse<Page<ProductResponse>> filterProduct(ProductFilter productFilter, Pageable pageable);
}