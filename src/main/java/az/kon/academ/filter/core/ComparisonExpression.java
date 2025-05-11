package az.kon.academ.filter.core;

public class ComparisonExpression {
    private final String field;
    private final ComparisonOperator operator;
    private final String value;

    public ComparisonExpression(String field, ComparisonOperator operator, String value) {
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

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (operator) {
            case EQ:
                return field + " = '" + value + "'";
            case NEQ:
                return field + " != '" + value + "'";
            case GT:
                return field + " > '" + value + "'";
            case GTE:
                return field + " >= '" + value + "'";
            case LT:
                return field + " < '" + value + "'";
            case LTE:
                return field + " <= '" + value + "'";
            case CONTAINS:
                return field + " LIKE '%" + value + "%'";
            case STARTS:
                return field + " LIKE '" + value + "%'";
            default:
                return field + " " + operator + " '" + value + "'";
        }
    }
}
