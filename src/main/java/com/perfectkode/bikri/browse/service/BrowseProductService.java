package com.perfectkode.bikri.browse.service;

import com.perfectkode.bikri.browse.dto.response.BrowseProductResponse;
import com.perfectkode.bikri.common.dto.PagedResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface BrowseProductService {

    PagedResponse<BrowseProductResponse> getAllProducts(int page, int size, String sortBy, String sortDir);

    PagedResponse<BrowseProductResponse> searchProducts(String query, int page, int size, String sortBy, String sortDir);

    BrowseProductResponse getProductBySlug(String slug);

    BrowseProductResponse getProductById(UUID id);
}
