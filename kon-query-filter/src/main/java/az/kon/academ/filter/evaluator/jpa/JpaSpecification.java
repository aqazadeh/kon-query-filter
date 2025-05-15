package az.kon.academ.filter.evaluator.jpa;

import az.kon.academ.filter.core.Filter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public class JpaSpecification<T> implements Specification<T> {

    private final Filter filter;

    public JpaSpecification(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return new JpaEvaluator(root, criteriaBuilder).evaluate(filter);
    }

    public static <T> JpaSpecification<T> byFilter(Filter filter) {
        return new JpaSpecification<>(filter);
    }
}
