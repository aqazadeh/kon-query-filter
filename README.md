# KON Query Filter

A flexible and powerful Java library for building dynamic query filters with support for various comparison operators and logical combinations.

## Project Structure

The project consists of two main modules:

1. **kon-query-filter**: The core library that provides the filter API
2. **example**: A Spring Boot application demonstrating how to use the filter library with Spring Data JPA

## Core Library Features

- Build complex filter criteria with logical operators (AND, OR, NOT)
- Support for various comparison operators:
  - EQ: Equal
  - NEQ: Not Equal
  - GT: Greater Than
  - GTE: Greater Than or Equal
  - LT: Less Than
  - LTE: Less Than or Equal
  - CONTAINS: Contains substring
  - STARTS: Starts with
  - ENDS: Ends with
  - IN: Value is in a list
  - BETWEEN: Value is between two values
- Pagination support with page and size parameters
- Recursive criteria structure for building complex filters

## Installation

### Maven

Add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>az.kon.academ</groupId>
    <artifactId>kon-query-filter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Creating Simple Filters

```java
// Create a simple filter for a single field
Criteria criteria = new Criteria(
    new ComparisonExpression("name", ComparisonOperator.EQ, "Laptop")
);
Filter filter = new Filter(criteria, 0, 10); // page 0, size 10
```

### Creating Complex Filters with Logical Operators

```java
// Create a filter with OR logic
Criteria orCriteria = new Criteria(LogicalOperator.OR, List.of(
    new Criteria(new ComparisonExpression("name", ComparisonOperator.EQ, "Laptop")),
    new Criteria(new ComparisonExpression("name", ComparisonOperator.EQ, "Smartphone"))
));
Filter orFilter = new Filter(orCriteria, 0, 100);

// Create a filter with AND logic
Criteria andCriteria = new Criteria(LogicalOperator.AND, List.of(
    new Criteria(new ComparisonExpression("stock", ComparisonOperator.GT, "150")),
    new Criteria(new ComparisonExpression("category", ComparisonOperator.EQ, "Electronics"))
));
Filter andFilter = new Filter(andCriteria, 0, 100);
```

## Integration with Spring Data JPA

The example module demonstrates how to integrate the filter library with Spring Data JPA:

1. Create a `FilterSpecification` class that implements Spring Data JPA's `Specification` interface:

```java
public class FilterSpecification<T> implements Specification<T> {
    private final Filter filter;
    private final JpaEvaluator jpaEvaluator;

    public FilterSpecification(Filter filter, JpaEvaluator jpaEvaluator) {
        this.filter = filter;
        this.jpaEvaluator = jpaEvaluator;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return jpaEvaluator.evaluate(filter, root, criteriaBuilder);
    }

    public static <T> FilterSpecification<T> byFilter(Filter filter, JpaEvaluator jpaEvaluator) {
        return new FilterSpecification<>(filter, jpaEvaluator);
    }
}
```

2. Create a `JpaEvaluator` class to convert filter criteria to JPA predicates:

```java
@Component
public class JpaEvaluator {
    public Predicate evaluate(Filter filter, Root<?> root, CriteriaBuilder criteriaBuilder) {
        if (filter == null || filter.getCriteria() == null) {
            return criteriaBuilder.conjunction();
        }
        return buildRecursive(filter.getCriteria(), root, criteriaBuilder);
    }

    // Implementation details...
}
```

3. Use the `FilterSpecification` in your service:

```java
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final JpaEvaluator jpaEvaluator;

    public ProductService(ProductRepository productRepository, JpaEvaluator jpaEvaluator) {
        this.productRepository = productRepository;
        this.jpaEvaluator = jpaEvaluator;
    }

    public List<Product> findProductsByFilter(Filter filter) {
        FilterSpecification<Product> spec = FilterSpecification.byFilter(filter, jpaEvaluator);
        return productRepository.findAll(spec);
    }
}
```

## REST API Example

The example module includes a REST controller that demonstrates how to use the filter library in a REST API:

```java
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/search")
    public List<Product> searchProducts(@RequestBody Filter filter) {
        return productService.findProductsByFilter(filter);
    }

    // Example endpoints...
}
```

## Example JSON Request

```json
{
  "criteria": {
    "logicalOperator": "AND",
    "criteria": [
      {
        "comparison": {
          "field": "category",
          "operator": "EQ",
          "value": "Electronics"
        }
      },
      {
        "comparison": {
          "field": "price",
          "operator": "LT",
          "value": "1000"
        }
      }
    ]
  },
  "page": 0,
  "size": 10
}
```