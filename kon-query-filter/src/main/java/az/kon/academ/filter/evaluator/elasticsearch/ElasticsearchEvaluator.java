package az.kon.academ.filter.evaluator.elasticsearch;

import az.kon.academ.filter.core.ComparisonExpression;
import az.kon.academ.filter.core.Criteria;
import az.kon.academ.filter.core.Filter;
import az.kon.academ.filter.evaluator.FilterEvaluator;
import az.kon.academ.filter.exception.FilterException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

import java.util.List;
import java.util.stream.Collectors;

public class ElasticsearchEvaluator implements FilterEvaluator<Query> {

    @Override
    public Query evaluate(Filter filter) {
        if (filter == null || filter.getCriteria() == null) {
            return QueryBuilders.matchAll().build()._toQuery();
        }
        return buildRecursive(filter.getCriteria());
    }

    private Query buildRecursive(Criteria criteria) {
        if (criteria.getOperator() != null) {
            List<Query> childrenQueries = criteria.getCriteria().stream()
                    .map(this::buildRecursive)
                    .collect(Collectors.toList());

            BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

            switch (criteria.getOperator()) {
                case AND:
                    boolQueryBuilder.must(childrenQueries);
                    break;
                case OR:
                    boolQueryBuilder.should(childrenQueries);
                    boolQueryBuilder.minimumShouldMatch("1");
                    break;
                case NOT:
                    if (childrenQueries.size() != 1) {
                        throw new FilterException("NOT operator requires exactly one child criteria");
                    }
                    boolQueryBuilder.mustNot(childrenQueries.get(0));
                    break;
                default:
                    throw new FilterException("Unknown logical operator: " + criteria.getOperator());
            }
            return boolQueryBuilder.build()._toQuery();

        } else if (criteria.getExpression() != null) {
            return buildComparison(criteria.getExpression());
        }
        return QueryBuilders.matchAll().build()._toQuery();
    }

    private Query buildComparison(ComparisonExpression c) {
        final String field = c.getField();
        final Object value = c.getValue();

        return switch (c.getOperator()) {
            case EQ -> QueryBuilders.term(t -> t.field(field).value(value.toString())).term()._toQuery();
            case NEQ -> QueryBuilders.bool()
                    .mustNot(QueryBuilders.term(t -> t.field(field).value(value.toString())).term()._toQuery())
                    .build()._toQuery();
            case GT -> {
                if (value instanceof Number) {
                    yield QueryBuilders.range(r -> r.number(n -> n.field(field).gt((Double) value))).range()._toQuery();
                } else if (value instanceof String) {
                    yield QueryBuilders.range(r -> r.term(t -> t.field(field).gt(value.toString()))).range()._toQuery();
                }
                throw new FilterException("GT operator requires a Number or String value");
            }
            case GTE -> {
                if (value instanceof Number) {
                    yield QueryBuilders.range(r -> r.number(n -> n.field(field).gte((Double) value))).range()._toQuery();
                } else if (value instanceof String) {
                    yield QueryBuilders.range(r -> r.term(t -> t.field(field).gte(value.toString()))).range()._toQuery();
                }
                throw new FilterException("GTE operator requires a Number or String value");
            }
            case LT -> {
                if (value instanceof Number) {
                    yield QueryBuilders.range(r -> r.number(n -> n.field(field).lt((Double) value))).range()._toQuery();
                } else if (value instanceof String) {
                    yield QueryBuilders.range(r -> r.term(t -> t.field(field).lt(value.toString()))).range()._toQuery();
                }
                throw new FilterException("LT operator requires a Number or String value");
            }
            case LTE -> {
                if (value instanceof Number) {
                    yield QueryBuilders.range(r -> r.number(n -> n.field(field).lte((Double) value))).range()._toQuery();
                } else if (value instanceof String) {
                    yield QueryBuilders.range(r -> r.term(t -> t.field(field).lte(value.toString()))).range()._toQuery();
                }
                throw new FilterException("LTE operator requires a Number or String value");
            }
            case CONTAINS -> {
                if (!(value instanceof String)) {
                    throw new FilterException("CONTAINS operator requires a String value");
                }
                yield QueryBuilders.wildcard(w -> w.field(field).value("*" + value + "*")).wildcard()._toQuery();
            }
            case STARTS -> {
                if (!(value instanceof String)) {
                    throw new FilterException("STARTS operator requires a String value");
                }
                yield QueryBuilders.wildcard(w -> w.field(field).value(value + "*")).wildcard()._toQuery();
            }
            case ENDS -> {
                if (!(value instanceof String)) {
                    throw new FilterException("ENDS operator requires a String value");
                }
                yield QueryBuilders.wildcard(w -> w.field(field).value("*" + value)).wildcard()._toQuery();
            }
            case IN -> {
                if (value instanceof List<?> listValue && !listValue.isEmpty()) {

                    yield QueryBuilders.terms(t -> t.field(field).terms(ts -> ts.value(listValue.stream().map(FieldValue::of).collect(Collectors.toList())))).term()._toQuery();
                }
                throw new FilterException("IN operator requires a non-empty list value");
            }
            case IS_NULL -> QueryBuilders.bool()
                    .mustNot(QueryBuilders.exists().field(field).build()._toQuery())
                    .build()._toQuery();
            case IS_NOT_NULL -> QueryBuilders.exists().field(field).build()._toQuery();
            case BETWEEN -> {
                if (value instanceof List<?> list && list.size() == 2) {
                    Object lower = list.get(0);
                    Object upper = list.get(1);

                    if (lower instanceof Number && upper instanceof Number) {
                        yield QueryBuilders.range(r -> r.number(n -> n.field(field)
                                .gte((Double) lower)
                                .lte((Double) upper)
                        )).range()._toQuery();
                    } else if (lower instanceof String && upper instanceof String) {
                        yield QueryBuilders.range(r -> r.term(t -> t.field(field)
                                .gte(lower.toString())
                                .lte(upper.toString())
                        )).range()._toQuery();
                    } else {
                        throw new FilterException("BETWEEN operator requires a list of two Number or two String values");
                    }
                }
                throw new FilterException("BETWEEN requires a list with exactly two values");
            }
        };
    }
}
