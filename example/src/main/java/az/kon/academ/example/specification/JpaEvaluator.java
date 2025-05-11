package az.kon.academ.example.specification;


import az.kon.academ.filter.core.ComparisonExpression;
import az.kon.academ.filter.core.Criteria;
import az.kon.academ.filter.core.Filter;
import az.kon.academ.filter.evaluator.FilterEvaluator;
import jakarta.persistence.criteria.CriteriaBuilder;
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
                default ->
                        throw new IllegalArgumentException("Unsupported logical type: " + criteria.getOperator());
            };
        } else if (criteria.getExpression() != null) {
            return buildComparison(criteria.getExpression(), root, criteriaBuilder);
        }
        return criteriaBuilder.conjunction();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildComparison(ComparisonExpression c, Root<?> root, CriteriaBuilder criteriaBuilder) {
        return switch (c.getOperator()) {
            case EQ -> criteriaBuilder.equal(root.get(c.getField()), c.getValue());
            case NEQ -> criteriaBuilder.notEqual(root.get(c.getField()), c.getValue());
            case GT -> criteriaBuilder.greaterThan(root.get(c.getField()), (Comparable) c.getValue());
            case GTE -> criteriaBuilder.greaterThanOrEqualTo(root.get(c.getField()), (Comparable) c.getValue());
            case LT -> criteriaBuilder.lessThan(root.get(c.getField()), (Comparable) c.getValue());
            case LTE -> criteriaBuilder.lessThanOrEqualTo(root.get(c.getField()), (Comparable) c.getValue());
            case CONTAINS -> criteriaBuilder.like(root.get(c.getField()), "%" + c.getValue() + "%");
            case STARTS -> criteriaBuilder.like(root.get(c.getField()), c.getValue() + "%");
            default -> throw new IllegalArgumentException("Unsupported comparison type: " + c.getOperator());
        };
    }
}
