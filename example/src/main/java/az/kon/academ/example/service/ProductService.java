package az.kon.academ.example.service;

import az.kon.academ.example.model.entity.Product;
import az.kon.academ.example.repository.ProductRepository;
import az.kon.academ.filter.evaluator.jpa.JpaSpecification;
import az.kon.academ.filter.core.*;
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
        JpaSpecification<Product> spec = JpaSpecification.byFilter(filter);
        Pageable pageable = Pageable.unpaged();

        if (filter.getSize() != null) {
            pageable = Pageable.ofSize(filter.getSize());
        }

        if (filter.getPage() != null) {
            pageable = pageable.withPage(filter.getPage());
        }

        return productRepository.findAll(spec, pageable).getContent();
    }

    public List<Product> findAll(Filter filter) {
        var criteria = new Criteria(new ComparisonExpression("userId", ComparisonOperator.EQ, "1"));
        filter.setSystemCriteria(criteria);

        return findProductsByFilter(filter);
    }
}
