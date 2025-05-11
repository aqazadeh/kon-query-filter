package az.kon.academ.example.service;

import az.kon.academ.example.model.entity.Product;
import az.kon.academ.example.repository.ProductRepository;
import az.kon.academ.example.specification.FilterSpecification;
import az.kon.academ.filter.core.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findProductsByFilter(Filter filter) {
        FilterSpecification<Product> spec = FilterSpecification.byFilter(filter);
        Pageable pageable = Pageable.unpaged();

        if (filter.getSize() != null) {
            pageable = Pageable.ofSize(filter.getSize());
        }

        if (filter.getPage() != null) {
            pageable = pageable.withPage(filter.getPage());
        }

        return productRepository.findAll(spec, pageable).getContent();
    }
}
