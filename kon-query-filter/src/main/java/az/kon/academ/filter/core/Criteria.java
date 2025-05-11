package az.kon.academ.filter.core;

import java.util.List;
import java.util.stream.Collectors;

public class Criteria {
    private LogicalOperator operator;
    private List<Criteria> criteria;
    private ComparisonExpression expression;

    public Criteria() {
    }

    public Criteria(LogicalOperator operator, List<Criteria> criteria) {
        this.operator = operator;
        this.criteria = criteria;
    }

    public Criteria(ComparisonExpression expression) {
        this.expression = expression;
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public List<Criteria> getCriteria() {
        return criteria;
    }

    public ComparisonExpression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        if (expression != null) {
            return expression.toString();
        } else {
            String criteriaString = this.getCriteria().stream()
                    .map(Criteria::toString)
                    .collect(Collectors.joining(" " + operator + " "));
            return "(" + criteriaString + ")";
        }
    }
}
