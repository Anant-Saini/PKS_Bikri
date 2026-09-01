package com.perfectkode.bikri.admin.repository;


import com.perfectkode.bikri.admin.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySlug(String slug);

    Optional<Product> findBySlugAndActive(String slug, boolean active);

    Optional<Product> findByIdAndActive(UUID productId, boolean active);

    List<Product> findByActive(boolean active);
}
