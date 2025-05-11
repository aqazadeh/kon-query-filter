package az.kon.academ.filter;

import az.kon.academ.filter.core.*;
import az.kon.academ.filter.evaluator.ElasticSearchEvaluator;
import az.kon.academ.filter.evaluator.FilterEvaluator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Criteria criteria = new Criteria(LogicalOperator.OR, List.of(
//                new Criteria(LogicalOperator.AND, List.of(
//                        new Criteria(new ComparisonExpression("name", ComparisonOperator.EQ, "John")),
//                        new Criteria(new ComparisonExpression("age", ComparisonOperator.GT, "25"))
//                )),
                new Criteria(new ComparisonExpression("status", ComparisonOperator.STARTS, "ACTIVE"))
        ));

        Filter filter = new Filter(criteria, 1, 100);

        FilterEvaluator<Query> evaluator = new ElasticSearchEvaluator();
        var query = evaluator.evaluate(filter);
        System.out.println(filter);

        System.out.println(query);
    }
}

