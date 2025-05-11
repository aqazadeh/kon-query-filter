package az.kon.academ.filter.core;

public class Filter {
    private Criteria criteria;
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
        return criteria;
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

    @Override
    public String toString() {
        return String.format("Filter [criteria=%s, page=%d, size=%d]",
                criteria, page, size);
    }
}