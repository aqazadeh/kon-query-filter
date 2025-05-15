package az.kon.academ.filter.core;

public class ComparisonExpression {
    private ComparisonOperator operator;
    private String field;
    private Object value;

    public ComparisonExpression() {
    }

    public ComparisonExpression(String field, ComparisonOperator operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return switch (operator) {
            case EQ -> field + " = '" + value + "'";
            case NEQ -> field + " != '" + value + "'";
            case GT -> field + " > '" + value + "'";
            case GTE -> field + " >= '" + value + "'";
            case LT -> field + " < '" + value + "'";
            case LTE -> field + " <= '" + value + "'";
            case CONTAINS -> field + " LIKE '%" + value + "%'";
            case STARTS -> field + " LIKE '" + value + "%'";
            default -> field + " " + operator + " '" + value + "'";
        };
    }
}
