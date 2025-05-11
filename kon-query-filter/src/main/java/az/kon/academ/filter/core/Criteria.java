package az.kon.academ.filter.core;

import java.util.List;
import java.util.stream.Collectors;

public class Criteria {
    private LogicalOperator logicalOperator;
    private List<Criteria> criteria;
    private ComparisonExpression comparison;

    public Criteria() {}

    public Criteria(LogicalOperator logicalOperator, List<Criteria> criteria) {
        this.logicalOperator = logicalOperator;
        this.criteria = criteria;
    }

    public Criteria(ComparisonExpression comparison) {
        this.comparison = comparison;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public List<Criteria> getCriteria() {
        return criteria;
    }

    public ComparisonExpression getComparison() {
        return comparison;
    }

    @Override
    public String toString() {
        if (comparison != null) {
            return comparison.toString();
        } else {
            String criteriaString = criteria.stream()
                    .map(Criteria::toString)
                    .collect(Collectors.joining(" " + logicalOperator + " "));
            return "(" + criteriaString + ")";
        }
    }
}
