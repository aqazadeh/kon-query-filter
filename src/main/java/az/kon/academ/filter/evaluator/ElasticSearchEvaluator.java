package az.kon.academ.filter.evaluator;

import az.kon.academ.filter.core.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;

import java.util.List;

public class ElasticSearchEvaluator implements FilterEvaluator<Query> {
    @Override
    public Query evaluate(Filter filter) {
        return buildQuery(filter.getCriteria());
    }

    private Query buildQuery(Criteria criteria) {
        if (criteria.getComparison() != null) {
            return buildComparisonQuery(criteria.getComparison());
        } else if (criteria.getCriteria() != null) {
            return buildLogicalQuery(criteria);
        }
        return null;
    }

    private Query buildComparisonQuery(ComparisonExpression comparison) {
        String field = comparison.getField();
        String value = comparison.getValue();
        ComparisonOperator operator = comparison.getOperator();
        return switch (operator) {
            case EQ -> TermQuery.of(t -> t.field(field).value(value))._toQuery();
            case NEQ -> BoolQuery.of(b -> b
                    .mustNot(TermQuery.of(t -> t.field(field).value(value))._toQuery())
            )._toQuery();
//            case GT -> RangeQuery.of(r -> r.field(field).gt(JsonData.of(value)))._toQuery();
//            case GTE -> RangeQuery.of(r -> r.field(field).gte(JsonData.of(value)))._toQuery();
//            case LT -> RangeQuery.of(r -> r.field(field).lt(JsonData.of(value)))._toQuery();
//            case LTE -> RangeQuery.of(r -> r.field(field).lte(JsonData.of(value)))._toQuery();
            case CONTAINS -> WildcardQuery.of(w -> w.field(field).value("*" + value + "*"))._toQuery();
            case STARTS -> WildcardQuery.of(w -> w.field(field).value(value + "*"))._toQuery();
            default -> throw new UnsupportedOperationException("Operator not supported: " + operator);
        };
    }

    private Query buildLogicalQuery(Criteria criteria) {
        LogicalOperator op = criteria.getLogicalOperator();
        List<Criteria> subCriteria = criteria.getCriteria();

        List<Query> subQueries = subCriteria.stream()
                .map(this::buildQuery)
                .toList();

        return switch (op) {
            case AND -> BoolQuery.of(b -> b.must(subQueries))._toQuery();
            case OR -> BoolQuery.of(b -> b.should(subQueries).minimumShouldMatch("1"))._toQuery();
            default -> throw new UnsupportedOperationException("Logical operator not supported: " + op);
        };
    }
}
