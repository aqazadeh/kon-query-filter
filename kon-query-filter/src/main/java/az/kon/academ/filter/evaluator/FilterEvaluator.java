package az.kon.academ.filter.evaluator;

import az.kon.academ.filter.core.Filter;

public interface FilterEvaluator<T> {
    T evaluate(Filter filter);
}
