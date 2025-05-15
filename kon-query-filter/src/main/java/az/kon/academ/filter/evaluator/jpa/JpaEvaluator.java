package az.kon.academ.filter.evaluator.jpa;


import az.kon.academ.filter.core.ComparisonExpression;
import az.kon.academ.filter.core.Criteria;
import az.kon.academ.filter.core.Filter;
import az.kon.academ.filter.evaluator.FilterEvaluator;
import az.kon.academ.filter.exception.FilterException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class JpaEvaluator implements FilterEvaluator<Predicate> {

    private final Root<?> root;
    private final CriteriaBuilder criteriaBuilder;

    public JpaEvaluator(Root<?> root, CriteriaBuilder criteriaBuilder) {
        this.root = root;
        this.criteriaBuilder = criteriaBuilder;
    }

    @Override
    public Predicate evaluate(Filter filter) {
        if (filter == null || filter.getCriteria() == null) {
            return criteriaBuilder.conjunction();
        }
        return buildRecursive(filter.getCriteria(), root, criteriaBuilder);
    }

    private Predicate buildRecursive(Criteria criteria, Root<?> root, CriteriaBuilder criteriaBuilder) {
        if (criteria.getOperator() != null) {
            List<Predicate> childrenPredicates = criteria.getCriteria().stream()
                    .map(childCriteria -> buildRecursive(childCriteria, root, criteriaBuilder))
                    .toList();

            return switch (criteria.getOperator()) {
                case AND -> criteriaBuilder.and(childrenPredicates.toArray(new Predicate[0]));
                case OR -> criteriaBuilder.or(childrenPredicates.toArray(new Predicate[0]));
                case NOT -> criteriaBuilder.not(childrenPredicates.get(0));
            };
        } else if (criteria.getExpression() != null) {
            return buildComparison(criteria.getExpression(), root, criteriaBuilder);
        }
        return criteriaBuilder.conjunction();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildComparison(ComparisonExpression c, Root<?> root, CriteriaBuilder criteriaBuilder) {
        final Path path = root.get(c.getField());
        final Object value = c.getValue();
        return switch (c.getOperator()) {
            case EQ -> criteriaBuilder.equal(path, value);
            case NEQ -> criteriaBuilder.notEqual(path, value);
            case GT -> criteriaBuilder.greaterThan(path, (Comparable) value);
            case GTE -> criteriaBuilder.greaterThanOrEqualTo(path, (Comparable) value);
            case LT -> criteriaBuilder.lessThan(path, (Comparable) value);
            case LTE -> criteriaBuilder.lessThanOrEqualTo(path, (Comparable) value);
            case CONTAINS -> criteriaBuilder.like(path, "%" + value + "%");
            case STARTS -> criteriaBuilder.like(path, value + "%");
            case ENDS -> criteriaBuilder.like(path, "%" + value);
            case IN -> {
                if (value instanceof List<?> listValue) {
                    criteriaBuilder.in(path).value(listValue);
                }
                throw new FilterException("IN operator requires a list value");
            }
            case IS_NULL -> criteriaBuilder.isNull(path);
            case IS_NOT_NULL -> criteriaBuilder.isNotNull(path);
            case BETWEEN -> {
                if (value instanceof List<?> list && list.size() == 2) {
                    Object lower = list.get(0);
                    Object upper = list.get(1);
                    criteriaBuilder.between(path, (Comparable) lower, (Comparable) upper);
                }
                throw new FilterException("BETWEEN requires a list with exactly two values");
            }
        };
    }
}
