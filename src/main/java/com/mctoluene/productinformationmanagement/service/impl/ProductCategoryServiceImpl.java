package com.mctoluene.productinformationmanagement.service.impl;

import com.cloudinary.utils.StringUtils;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.CreateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.UpdateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponse;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithChildrenDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.filter.search.ProductCategoryFilter;
import com.mctoluene.productinformationmanagement.filter.search.QueryBuilder;
import com.mctoluene.productinformationmanagement.helper.ProductCategoryHelper;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductCategoryHierarchyService;
import com.mctoluene.productinformationmanagement.service.ProductCategoryService;
import com.mctoluene.productinformationmanagement.service.internal.*;
import com.mctoluene.commons.exceptions.NotFoundException;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.mctoluene.productinformationmanagement.helper.ProductCategoryHelper.buildCategoryResponse;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryInternalService productCategoryInternalService;

    private final ProductCategoryHierarchyService productCategoryHierarchyService;

    private final ProductCategoryHierarchyInternalService productCategoryHierarchyInternalService;

    private final MessageSourceService messageSourceService;

    private final PropertyInternalService propertyInternalService;
    private static final String PRODUCT_CATEGORY_DEPTH = "PRODUCT_CATEGORY_DEPTH";

    private final ProductInternalService productInternalService;

    private final ProductVariantInternalService productVariantInternalService;

    private final ImageInternalService imageInternalService;
    private final LocationCacheInterfaceService locationCacheInterfaceService;

    @Override
    public AppResponse createProductCategory(CreateProductCategoryRequestDto requestDto) {

        RequestHeaderContext context = RequestHeaderContextHolder.getContext();

        int categoryDepth = 0;

        if (productCategoryInternalService
                .findProductCategoryByNameIgnoreCase(requestDto.getProductCategoryName().trim()).isPresent()) {
            throw new ValidatorException(messageSourceService.getMessageByKey("product.category.name.is.not.unique"));
        }

        List<CountryDto> countryDtos = locationCacheInterfaceService.getLocationCache();

        CountryDto countryDetails = countryDtos.stream()
                .filter(countryDto -> context.countryCode().equals(countryDto.threeLetterCode())).findFirst()
                .orElse(null);

        // check if this is a parent category or child category
        if (requestDto.getProductCategoryParentPublicIds() != null
                && !requestDto.getProductCategoryParentPublicIds().isEmpty())
            categoryDepth = calculateCategoryDepthFromParent(requestDto.getProductCategoryParentPublicIds());

        String url = "";

        if (StringUtils.isNotBlank(requestDto.getImageUrl())) {
            url = imageInternalService.uploadBase64ImageString(requestDto.getImageUrl());
        }

        ProductCategory productCategory = ProductCategoryHelper.buildProductCategory(requestDto, url,
                countryDetails.publicId());

        setCategoryDepth(categoryDepth, productCategory);

        productCategoryInternalService.saveNewProductCategory(productCategory);

        createProductCategoryHierarchy(requestDto, productCategory);

        ProductCategoryResponseDto responseDto = ProductCategoryHelper.buildProductCategoryResponseDto(productCategory);

        log.info("Product category created successfully {}", responseDto);

        return new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("product.category.created.successfully"),
                messageSourceService.getMessageByKey("product.category.created.successfully"),
                responseDto, null);

    }

    @Override
    public AppResponse deleteProductCategory(UUID publicId) {
        ProductCategory productCategory = productCategoryInternalService.findProductCategoryByPublicId(publicId);

        if (Boolean.TRUE.equals(productInternalService.checkIfCategoryIsInUse(productCategory.getId())))
            throw new ValidatorException(messageSourceService.getMessageByKey("Category.is.in.use"));

        productCategory.setStatus(Status.DELETED);
        productCategoryInternalService.deleteProductCategory(productCategory);

        log.info("Product category deleted successfully");

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.deleted.successfully"),
                messageSourceService.getMessageByKey("product.category.deleted.successfully"),
                null, null);
    }

    @Override
    public AppResponse getProductCategoryByPublicId(UUID publicId) {

        ProductCategory productCategory = productCategoryInternalService.findProductCategoryByPublicId(publicId);

        ProductCategoryResponseDto responseDto = ProductCategoryHelper.buildProductCategoryResponseDto(productCategory);
        log.info("Product category with publicId {} retrieved successfully {}", publicId, responseDto);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                responseDto, null);

    }

    @Override
    public AppResponse getProductCategoryByCountryCode() {

        var countryUUID = locationCacheInterfaceService.getLocationCache().stream().filter(
                f -> f.threeLetterCode().equals(RequestHeaderContextHolder.getContext().countryCode())).findFirst()
                .get().publicId();

        List<ProductCategory> productCategory = productCategoryInternalService.findProductCategoryByCountryCode();

        if (productCategory.isEmpty()) {
            throw new NotFoundException(messageSourceService.getMessageByKey("product.category.not.found"));
        }
        log.info("Product category with publicId {} retrieved successfully {}", countryUUID, null);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                null, null);
    }

    @Override
    public AppResponse getProductCategoriesByPublicIds(List<UUID> productCategoryPublicIds) {
        final var productCategories = productCategoryInternalService
                .findProductCategoryByPublicIds(productCategoryPublicIds);

        log.info("Product categories {}", productCategories);
        if (productCategories == null || productCategories.isEmpty()) {
            throw new NotFoundException(messageSourceService.getMessageByKey("product.category.not.found"));
        }
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                productCategories, null);
    }

    @Override
    public AppResponse getAllProductCategories(Integer page, Integer size, Boolean isParent) {
        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);

        Page<ProductCategory> productCategories;

        if (Boolean.TRUE.equals(isParent)) {
            productCategories = productCategoryInternalService.getAllParentProductCategories(request);

        } else {
            productCategories = productCategoryInternalService.getAllProductCategories(request);
        }

        log.info("Retrieved product categories of size {} and page {}", size, page);
        PageImpl<ProductCategoryWithSubcategoryResponse> response = getProductCategoryWithSubcategory(productCategories,
                new ArrayList<>(), request);

        return new AppResponse(HttpStatus.OK.value(),
                "product categories fetched successfully",
                "product categories fetched successfully",
                response, null);
    }

    @Override
    public AppResponse getAllDirectSubcategoryOfProductCategory(UUID publicId, Integer page, Integer size) {
        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);

        productCategoryInternalService.findProductCategoryByPublicId(publicId);

        Page<ProductCategory> productCategories = productCategoryInternalService
                .getAllDirectChildrenOfProductCategory(publicId, request);

        return buildAppResponse(productCategories);
    }

    @Override
    public AppResponse getAllNestedSubcategoryOfProductCategory(UUID productCategoryProductId) {

        Pageable request = PageRequest.of(0, Integer.MAX_VALUE);

        productCategoryInternalService.findProductCategoryByPublicId(productCategoryProductId);

        final var childCategories = productCategoryInternalService
                .getAllDirectChildrenOfProductCategoryUnpaged(productCategoryProductId);

        MultiValueMap<UUID, ProductCategory> childrenCategoryMap = new LinkedMultiValueMap<>();

        final var allCategories = productCategoryInternalService.getAllProductCategories(request);
        final var allHierarchy = productCategoryHierarchyInternalService.getAllProductCategory();

        Map<UUID, ProductCategory> productCategoryMap = new HashMap<>();
        MultiValueMap<UUID, UUID> categoryHierachyMap = new LinkedMultiValueMap<>();
        allCategories.forEach(o -> productCategoryMap.put(o.getPublicId(), o));
        allHierarchy.forEach(
                o -> categoryHierachyMap.add(o.getProductCategoryParentPublicId(), o.getProductCategoryPublicId()));

        categoryHierachyMap.forEach((k, v) -> v.forEach(i -> {
            if (k != i)
                childrenCategoryMap.add(k, productCategoryMap.get(i));
        }));

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                recursivelyBuildCategoryWithChildren(childCategories, childrenCategoryMap), null);
    }

    List<ProductCategoryWithChildrenDto> recursivelyBuildCategoryWithChildren(List<ProductCategory> categories,
            MultiValueMap<UUID, ProductCategory> parentCategoryMaps) {
        if (categories == null)
            return List.of();
        if (categories.isEmpty())
            return List.of();

        return categories.stream().map(cat -> {
            ProductCategoryWithChildrenDto catChildren = ProductCategoryHelper.buildProductCategoryWithChildren(cat);
            catChildren.setChildren(recursivelyBuildCategoryWithChildren(parentCategoryMaps.get(cat.getPublicId()),
                    parentCategoryMaps));
            return catChildren;
        }).toList();
    }

    @Override
    public AppResponse<List<ProductCategory>> getAllSubcategoryOfProductCategory(UUID productCategoryPublicId) {
        List<ProductCategory> children = productCategoryInternalService
                .getAllChildrenOfProductCategory(productCategoryPublicId);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                children, null);
    }

    @Override
    public AppResponse getAllProductCategoriesFiltered(Integer page, Integer size, String productCategoryName,
            LocalDateTime startDate, LocalDateTime endDate) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 0 : size;
        Pageable request = PageRequest.of(page, size);

        startDate = Objects.isNull(startDate) ? LocalDateTime.now().minusYears(100) : startDate;
        endDate = Objects.isNull(endDate) ? LocalDateTime.now() : endDate;

        Page<ProductCategory> listOfCategories = productCategoryInternalService.findAllBySearchCrieteria(request,
                productCategoryName, startDate, endDate);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                listOfCategories, null);
    }

    @Override
    public AppResponse archiveProductCategory(UUID productCategoryPublicId) {

        ProductCategory productCategory = productCategoryInternalService.findByPublicIdAndStatus(
                productCategoryPublicId,
                Status.ACTIVE);

        if (Objects.isNull(productCategory))
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("product.category.not.found"));

        List<ProductCategory> listOfCategories = productCategoryInternalService
                .getAllChildrenOfProductCategory(productCategoryPublicId);

        List<UUID> listOfCategoryPublicIds = listOfCategories.stream()
                .map(BaseEntity::getPublicId).collect(Collectors.toList());
        listOfCategoryPublicIds.add(productCategory.getPublicId());

        List<ProductCategory> listOfArchivedProductCategories = productCategoryInternalService
                .archiveCategoryAndSubCategoriesByPublicIds(listOfCategoryPublicIds);

        List<Product> listOfArchivedProducts = productInternalService
                .archiveAllByproductCategoryIn(listOfArchivedProductCategories);

        List<ProductVariant> listOfArchivedProductVariants = productVariantInternalService
                .archiveAllByProductIdIn(listOfArchivedProducts);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("category.archived.successfully"),
                messageSourceService.getMessageByKey("category.archived.successfully"),
                null, null);
    }

    @Override
    public AppResponse updateProductCategory(UUID publicId, UpdateProductCategoryRequestDto requestDto) {
        ProductCategory productCategory = productCategoryInternalService.findProductCategoryByPublicId(publicId);

        // updates name, description and image url
        updateBasicProductCategoryDetails(requestDto, productCategory);

        // Adds a new parent/parents to a product category
        updateParentCategory(publicId, requestDto, productCategory);

        productCategoryInternalService.updateExistingProductCategory(productCategory);

        ProductCategoryResponseDto responseDto = ProductCategoryHelper.buildProductCategoryResponseDto(productCategory);

        log.info("Product category {} updated successfully", responseDto.productCategoryName());
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.updated.successfully"),
                messageSourceService.getMessageByKey("product.category.updated.successfully"),
                responseDto, null);
    }

    private void updateParentCategory(UUID publicId, UpdateProductCategoryRequestDto requestDto,
            ProductCategory productCategory) {
        // get the depth of the product category to be updated.
        // The goal here is to find out if the product category to be updated has a
        // larger depth compared to the new depth
        // it would get after becoming a child to the parent categories in requestDto
        int maxDepth = productCategory.getDepth();

        if (!Objects.isNull(requestDto.getProductCategoryParentPublicIds()) &&
                !requestDto.getProductCategoryParentPublicIds().isEmpty()) {
            int maxParentDepth = updateProductCategoryParents(publicId, requestDto);

            if (maxParentDepth > maxDepth)
                updateChildrenCategoryDepth(productCategory, new LinkedList<>(), new HashSet<>(), maxParentDepth);

            maxDepth = Math.max(maxDepth, maxParentDepth);
        }

        setCategoryDepth(maxDepth, productCategory);
    }

    private int getMaxDescendantDepth(ProductCategory productCategory, Queue<ProductCategory> productCategoryQueue,
            int maxDepth, Set<UUID> visited) {
        productCategoryQueue.addAll(productCategoryInternalService
                .getAllDirectChildrenOfProductCategoryUnpaged(productCategory.getPublicId()));

        while (!productCategoryQueue.isEmpty()) {
            ProductCategory child = productCategoryQueue.poll();
            if (child != null && !visited.contains(child.getPublicId())) {
                visited.add(child.getPublicId());

                maxDepth = Math.max(maxDepth, child.getDepth());
            }

            getMaxDescendantDepth(child, productCategoryQueue, maxDepth, visited);
        }

        return maxDepth;
    }

    private void updateChildrenCategoryDepth(ProductCategory productCategory,
            Queue<ProductCategory> categoriesToBeUpdated,
            Set<UUID> categoriesUpdatedIds, int newParentDepth) {
        // this method is to update children categories depth in the event that their
        // parent category depth is updated
        categoriesToBeUpdated.addAll(productCategoryInternalService
                .getAllDirectChildrenOfProductCategoryUnpaged(productCategory.getPublicId()));

        while (!categoriesToBeUpdated.isEmpty()) {
            ProductCategory child = categoriesToBeUpdated.poll();
            if (child != null && !categoriesUpdatedIds.contains(child.getPublicId())) {
                categoriesUpdatedIds.add(child.getPublicId());

                // if child category depth is larger that the parent new category depth + 1
                // then no need to update child depth.

                if (child.getDepth() >= newParentDepth + 1)
                    break;

                child.setDepth(newParentDepth + 1);

                productCategoryInternalService.saveProductCategoryToDb(child);

                updateChildrenCategoryDepth(child, categoriesToBeUpdated, categoriesUpdatedIds, child.getDepth());

            }
        }
    }

    private void updateBasicProductCategoryDetails(UpdateProductCategoryRequestDto requestDto,
            ProductCategory productCategory) {
        if (!Objects.isNull(requestDto.getProductCategoryName()) && !requestDto.getProductCategoryName().isEmpty()) {
            try {
                Optional<ProductCategory> productCategoryByNameExist = productCategoryInternalService
                        .findProductCategoryByNameIgnoreCase(requestDto.getProductCategoryName().trim());

                if (productCategoryByNameExist.isPresent()
                        && productCategoryByNameExist.get().getPublicId() != productCategory.getPublicId()) {
                    throw new ValidatorException(
                            messageSourceService.getMessageByKey("product.category.name.is.not.unique"));

                }
                productCategory
                        .setProductCategoryName(WordUtils.capitalizeFully(requestDto.getProductCategoryName().trim()));
            } catch (Exception e) {
                throw new UnProcessableEntityException("product.category.name.is.not.unique");
            }
        }
        if (!Objects.isNull(requestDto.getDescription()) && !requestDto.getDescription().isEmpty()) {
            productCategory.setDescription(requestDto.getDescription());
        }
        if (!Objects.isNull(requestDto.getImageUrl()) && !requestDto.getDescription().isEmpty()) {
            productCategory.setImageUrl(requestDto.getDescription());
        }

        if (!Objects.isNull(requestDto.getModifiedBy()) && !requestDto.getModifiedBy().isEmpty()) {
            productCategory.setLastModifiedBy(requestDto.getModifiedBy());
        }

    }

    private void createProductCategoryHierarchy(CreateProductCategoryRequestDto requestDto,
            ProductCategory productCategory) {
        if (requestDto.getProductCategoryParentPublicIds() != null
                && !requestDto.getProductCategoryParentPublicIds().isEmpty()) {
            buildAndSaveProductCategoryHierarchyWithParent(productCategory,
                    requestDto.getProductCategoryParentPublicIds());
        } else {
            buildAndSaveParentCategoryHierarchyWithoutParent(productCategory);
        }
    }

    private int updateProductCategoryParents(UUID publicId, UpdateProductCategoryRequestDto requestDto) {
        // get the new depth the product category will have after the process is
        // complete
        int calculatedDepth = calculateCategoryDepthFromParent(requestDto.getProductCategoryParentPublicIds());

        ProductCategory productCategory = productCategoryInternalService.findProductCategoryByPublicId(publicId);

        int maxDescendantDepth = 0;
        // check if any of the product category's descendant will violate the depth
        // rule. If yes, don't go ahead
        if (!productCategoryInternalService.getAllDirectChildrenOfProductCategoryUnpaged(publicId).isEmpty()) {
            maxDescendantDepth = getMaxDescendantDepth(productCategory, new LinkedList<>(),
                    productCategory.getDepth(), new HashSet<>());
        }

        Property maxDepthAllowed = propertyInternalService.findPropertyByName(PRODUCT_CATEGORY_DEPTH);

        if (calculatedDepth + productCategory.getDepth() > Integer.parseInt(maxDepthAllowed.getValue())
                || calculatedDepth + maxDescendantDepth > Integer.parseInt(maxDepthAllowed.getValue())) {
            throw new ValidatorException(messageSourceService.getMessageByKey("max.depth.will.be.passed"));
        }

        buildAndSaveProductCategoryHierarchyWithParent(productCategory, requestDto.getProductCategoryParentPublicIds());
        return calculatedDepth;
    }

    private void buildAndSaveParentCategoryHierarchyWithoutParent(ProductCategory productCategory) {
        productCategoryHierarchyService.createProductParentCategoryHierarchyWithoutParentCategory(productCategory);
    }

    private void setCategoryDepth(int categoryDepth, ProductCategory productCategory) {
        productCategory.setDepth(categoryDepth);

        log.info("category depth: {}", categoryDepth);
    }

    private int calculateCategoryDepthFromParent(List<UUID> requestDto) {
        List<Integer> parentDepths = new ArrayList<>();

        for (UUID parentId : requestDto) {
            ProductCategory parentCategory = productCategoryInternalService
                    .findProductCategoryByPublicId(parentId);
            parentDepths.add(parentCategory.getDepth());
        }

        Property maxDepthAllowed = propertyInternalService.findPropertyByName(PRODUCT_CATEGORY_DEPTH);

        int maxParentDepth = getMaxParentDepth(parentDepths);
        int maxParentDepthInt = Integer.parseInt(maxDepthAllowed.getValue());

        if (maxParentDepth + 1 >= maxParentDepthInt)
            throw new ValidatorException(messageSourceService.getMessageByKey("max.category.depth"));

        // as long as the flow uses this method, it has a parent, and so the lowest
        // depth it can have is 1
        return maxParentDepth + 1;
    }

    private int getMaxParentDepth(List<Integer> parentDepths) {
        int maxParentDepth = 0;
        for (int depth : parentDepths) {
            maxParentDepth = Math.max(maxParentDepth, depth);
        }

        return maxParentDepth;
    }

    private void buildAndSaveProductCategoryHierarchyWithParent(ProductCategory productCategory,
            List<UUID> requestDto) {
        for (UUID parentId : requestDto) {
            productCategoryHierarchyService.createProductCategoryHierarchyWithParentCategory(productCategory, parentId);
        }
    }

    private AppResponse buildAppResponse(Page<ProductCategory> productCategories) {

        Page<ProductCategoryResponseDto> pageResponse = productCategories
                .map(productCategory -> ProductCategoryHelper.buildProductCategoryResponseDto(productCategory));

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                pageResponse, null);
    }

    @Override
    public AppResponse unArchiveProductCategory(UUID productCategoryPublicId) {

        ProductCategory productCategory = productCategoryInternalService.findByPublicIdAndStatus(
                productCategoryPublicId,
                Status.INACTIVE);

        if (Objects.isNull(productCategory))
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("product.category.not.found"));

        productCategory.setStatus(Status.ACTIVE);
        productCategoryInternalService.saveProductCategoryToDb(productCategory);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.unarchived.successfully"),
                messageSourceService.getMessageByKey("product.category.unarchived.successfully"),
                null, null);
    }

    @Override
    public AppResponse getProductCategories(Integer page, Integer size) {
        page = page > 0 ? page : 1;
        size = size > 0 ? size : 10;
        List<ProductCategoryWithSubcategoryResponse> responseList = new ArrayList<>();
        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);

        Page<ProductCategory> allParentProductCategories = productCategoryInternalService
                .getAllParentProductCategories(request);

        PageImpl<ProductCategoryWithSubcategoryResponse> pagedResponse = getProductCategoryWithSubcategory(
                allParentProductCategories, responseList, request);

        return new AppResponse(HttpStatus.OK.value(),
                "product categories fetched successfully",
                "product categories fetched successfully",
                pagedResponse, null);
    }

    private PageImpl<ProductCategoryWithSubcategoryResponse> getProductCategoryWithSubcategory(
            Page<ProductCategory> allParentProductCategories,
            List<ProductCategoryWithSubcategoryResponse> responseList,
            Pageable request) {
        allParentProductCategories.stream().forEach(productCategory -> {
            List<ProductCategory> allChildrenOfProductCategory = productCategoryInternalService
                    .getAllChildrenOfProductCategory(productCategory.getPublicId());

            Set<String> childrenCategoryNames = allChildrenOfProductCategory.stream()
                    .map(ProductCategory::getProductCategoryName)
                    .collect(Collectors.toSet());

            childrenCategoryNames.remove(productCategory.getProductCategoryName());

            int count = childrenCategoryNames.size();

            String commaSeparatedNames = String.join(", ", childrenCategoryNames);

            ProductCategoryWithSubcategoryResponse categoryWithSubcategoryResponse = buildCategoryResponse(
                    productCategory, commaSeparatedNames, count);

            responseList.add(categoryWithSubcategoryResponse);
        });

        PageImpl<ProductCategoryWithSubcategoryResponse> pagedResponse = new PageImpl<>(responseList, request,
                allParentProductCategories.getTotalElements());
        return pagedResponse;
    }

    @Override
    public AppResponse<Page<ProductCategoryResponse>> filterProductCategory(ProductCategoryFilter productCategoryFilter,
            Pageable pageable) {
        QueryBuilder<ProductCategory, ProductCategoryResponse> queryBuilder = QueryBuilder.build(ProductCategory.class,
                ProductCategoryResponse.class);
        productCategoryFilter.filter(queryBuilder);
        queryBuilder.orderBy("createdDate", true);
        Page<ProductCategoryResponse> result = queryBuilder.getResult(pageable);
        return new AppResponse<>(HttpStatus.OK.value(), "success", "success", result, null);
    }
}
