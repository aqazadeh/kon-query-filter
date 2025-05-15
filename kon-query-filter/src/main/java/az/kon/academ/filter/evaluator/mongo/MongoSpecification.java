package az.kon.academ.filter.evaluator.mongo;


import az.kon.academ.filter.core.Filter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoSpecification {

    private final Filter filter;

    public MongoSpecification(Filter filter) {
        this.filter = filter;
    }

    public Query toQuery() {
        Criteria criteria = new MongoEvaluator().evaluate(filter);
        return new Query(criteria);
    }

    public static MongoSpecification byFilter(Filter filter) {
        return new MongoSpecification(filter);
    }
}

