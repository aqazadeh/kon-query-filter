package az.kon.academ.filter.evaluator.mongo;

import az.kon.academ.filter.core.ComparisonExpression;
import az.kon.academ.filter.core.Filter;
import az.kon.academ.filter.evaluator.FilterEvaluator;
import az.kon.academ.filter.exception.FilterException;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class MongoEvaluator implements FilterEvaluator<Criteria> {

    @Override
    public Criteria evaluate(Filter filter) {
        if (filter == null || filter.getCriteria() == null) {
            return new Criteria();
        }
        return buildRecursive(filter.getCriteria());
    }

    private Criteria buildRecursive(az.kon.academ.filter.core.Criteria criteria) {
        if (criteria.getOperator() != null) {
            List<Criteria> children = criteria.getCriteria().stream()
                    .map(this::buildRecursive)
                    .toList();

            return switch (criteria.getOperator()) {
                case AND -> new Criteria().andOperator(children.toArray(new Criteria[0]));
                case OR -> new Criteria().orOperator(children.toArray(new Criteria[0]));
                case NOT -> {
                    if (children.size() != 1) {
                        throw new FilterException("NOT operator requires exactly one child criteria");
                    }
                    yield new Criteria().norOperator(children.get(0));
                }
            };
        } else if (criteria.getExpression() != null) {
            return buildComparison(criteria.getExpression());
        }
        return new Criteria(); // default true
    }

    private Criteria buildComparison(ComparisonExpression c) {
        String field = c.getField();
        Object value = c.getValue();

        return switch (c.getOperator()) {
            case EQ -> Criteria.where(field).is(value);
            case NEQ -> Criteria.where(field).ne(value);
            case GT -> Criteria.where(field).gt(value);
            case GTE -> Criteria.where(field).gte(value);
            case LT -> Criteria.where(field).lt(value);
            case LTE -> Criteria.where(field).lte(value);
            case CONTAINS -> Criteria.where(field).regex(".*" + value + ".*", "i");
            case STARTS -> Criteria.where(field).regex("^" + value, "i");
            case ENDS -> Criteria.where(field).regex(value + "$", "i");
            case IN -> {
                if (value instanceof List<?> listValue) {
                    yield Criteria.where(field).in(listValue);
                }
                throw new FilterException("IN operator requires a list value");
            }
            case BETWEEN -> {
                if (value instanceof List<?> list && list.size() == 2) {
                    Object lower = list.get(0);
                    Object upper = list.get(1);
                    yield Criteria.where(field).gte(lower).lte(upper);
                }
                throw new FilterException("BETWEEN operator requires exactly 2 values");
            }
            case IS_NULL -> Criteria.where(field).is(null);
            case IS_NOT_NULL -> Criteria.where(field).ne(null);
        };
    }
}
