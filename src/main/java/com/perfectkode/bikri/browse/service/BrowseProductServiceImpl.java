package com.perfectkode.bikri.browse.service;

import com.perfectkode.bikri.admin.exception.ProductNotFoundException;
import com.perfectkode.bikri.browse.dto.response.BrowseProductResponse;

import com.perfectkode.bikri.common.dto.PagedResponse;
import com.perfectkode.bikri.admin.model.Product;
import com.perfectkode.bikri.admin.repository.ProductRepository;
import com.perfectkode.bikri.common.mapper.BrowseProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrowseProductServiceImpl implements BrowseProductService {

    private final ProductRepository productRepository;
    private final BrowseProductMapper browseProductMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BrowseProductResponse> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Page<Product> productPage = productRepository.findByActive(true, pageable);
        return mapToPagedResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BrowseProductResponse> searchProducts(String keyword, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Page<Product> productPage = productRepository.searchActiveProducts(keyword, pageable);
        return mapToPagedResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public BrowseProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndActive(slug, true)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with slug: " + slug));

        return browseProductMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    @Override
    public BrowseProductResponse getProductById(UUID id) {
        Product product = productRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        return browseProductMapper.toDto(product);
    }

    // Helper 1: Sort Builder
    private Sort buildSort(String sortBy, String sortDir) {
        // Validate sortBy to prevent invalid property references
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdAt"; // Default sort
        }
        
        return sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }

    // Helper 2: Page to PagedResponse Mapper
    private PagedResponse<BrowseProductResponse> mapToPagedResponse(Page<Product> productPage) {
        List<BrowseProductResponse> content = productPage.getContent()
                .stream()
                .map(browseProductMapper::toDto)
                .toList();

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }



}