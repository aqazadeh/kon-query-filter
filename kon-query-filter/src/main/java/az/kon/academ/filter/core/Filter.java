package az.kon.academ.filter.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Filter {
    private Criteria criteria;

    @JsonIgnore
    private Criteria systemCriteria;

    private Integer page;
    private Integer size;

    public Filter() {
    }

    public Filter(Criteria criteria, Integer page, Integer size) {
        this.criteria = criteria;
        this.page = page;
        this.size = size;
    }

    public Criteria getCriteria() {
        if (criteria == null && systemCriteria == null) {
            return null;
        } else if (criteria == null) {
            return systemCriteria;
        } else if (systemCriteria == null) {
            return criteria;
        } else {
            return new Criteria(LogicalOperator.AND, List.of(criteria, systemCriteria));
        }
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Criteria getSystemCriteria() {
        return systemCriteria;
    }

    public void setSystemCriteria(Criteria systemCriteria) {
        this.systemCriteria = systemCriteria;
    }


    @Override
    public String toString() {
        return String.format("Filter [criteria=%s, page=%d, size=%d]",
                criteria, page, size);
    }
}