package az.kon.academ.filter.evaluator.elasticsearch;

import az.kon.academ.filter.core.Filter;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

public class ElasticsearchSpecification {
    private final Filter filter;

    public ElasticsearchSpecification(Filter filter) {
        this.filter = filter;
    }

    public Query toQuery() {
        ElasticsearchEvaluator evaluator = new ElasticsearchEvaluator();
        return evaluator.evaluate(filter);
    }

    public static ElasticsearchSpecification byFilter(Filter filter) {
        return new ElasticsearchSpecification(filter);
    }
}
