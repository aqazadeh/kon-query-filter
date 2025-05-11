package az.kon.academ.example.specification;

import az.kon.academ.filter.core.Filter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public class FilterSpecification<T> implements Specification<T> {

    private final Filter filter;

    public FilterSpecification(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return new JpaEvaluator(root, criteriaBuilder).evaluate(filter);
    }

    public static <T> FilterSpecification<T> byFilter(Filter filter) {
        return new FilterSpecification<>(filter);
    }
}
