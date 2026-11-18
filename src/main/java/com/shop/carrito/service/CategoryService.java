package com.shop.carrito.service;

import com.shop.carrito.dto.CategoryResponse;
import com.shop.carrito.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesWithActiveProductCount() {
        var projections = categoryRepository.findCategoriesWithProductCount();
        return projections.stream()
                .map(p -> new CategoryResponse(
                        p.getId(),
                        p.getName(),
                        p.getSlug(),
                        p.getDescription(),
                        p.getImageUrl(),
                        p.getProductCount() != null ? p.getProductCount() : 0L
                ))
                .toList();
    }
}
