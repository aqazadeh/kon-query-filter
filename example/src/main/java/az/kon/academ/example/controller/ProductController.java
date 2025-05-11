package az.kon.academ.example.controller;

import az.kon.academ.example.model.entity.Product;
import az.kon.academ.example.service.ProductService;
import az.kon.academ.filter.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping("/search")
    public List<Product> searchProducts(@RequestBody Filter filter) {
        log.info("Received request: {}", filter);
        return productService.findProductsByFilter(filter);
    }

    @GetMapping("/example1")
    public List<Product> getExample1() {
        Criteria criteria = new Criteria(LogicalOperator.OR, List.of(
                new Criteria(new ComparisonExpression("name", ComparisonOperator.EQ, "Laptop")),
                new Criteria(new ComparisonExpression("name", ComparisonOperator.EQ, "Smartphone"))
        ));

        Filter filter = new Filter(criteria, 0, 100);
        return productService.findProductsByFilter(filter);
    }

    @GetMapping("/example2")
    public List<Product> getExample2() {
        Criteria criteria = new Criteria(LogicalOperator.AND, List.of(
                new Criteria(new ComparisonExpression("stock", ComparisonOperator.GT, "150")),
                new Criteria(new ComparisonExpression("category", ComparisonOperator.EQ, "Electronics"))
        ));
        Filter filter = new Filter(criteria, 0, 100);
        return productService.findProductsByFilter(filter);
    }

    @GetMapping("/example3")
    public List<Product> getExample3() {
        Criteria criteria = new Criteria(new ComparisonExpression("category", ComparisonOperator.EQ, "Electronics"));
        Filter filter = new Filter(criteria, 0, 100);
        return productService.findProductsByFilter(filter);
    }
}
