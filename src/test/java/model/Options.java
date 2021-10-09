package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Options {

    private List<Where> wheres;
    private List<String> field;
    private List<String> order;
    private List<String> group;
    private List<String> table;
    private Map attrs;
    private Limit limit;
    private String alias;
    private String having;
    private String lock;
    private String distinct;

    public Options()
    {
        wheres = new ArrayList();
        field = new ArrayList();
        order = new ArrayList();
        group = new ArrayList();
        attrs = new HashMap();
        table = new ArrayList();
    }

    public void addTable(String table)
    {
        this.table.add(table);
    }
    public void addWhere(Where where)
    {
        wheres.add(where);
    }
    public void addField(String field)
    {
        this.field.add(field);
    }
    public void addOrder(String order)
    {
        this.order.add(order);
    }
    public void addGroup(String group)
    {
        this.group.add(group);
    }

    public void setLimit(Integer size)
    {
        limit = new Limit(size);
    }
    public void setLimit(Integer offset , Integer size)
    {
        limit = new Limit(offset , size);
    }

    public List<Where> getWheres() {
        return wheres;
    }

    public List<String> getField() {
        return field;
    }

    public List<String> getOrder() {
        return order;
    }

    public List<String> getGroup() {
        return group;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setHaving(String having) {
        this.having = having;
    }

    public String getAlias() {
        return alias;
    }

    public String getHaving() {
        return having;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public String getDistinct() {
        return distinct;
    }

    public void setDistinct(String distinct) {
        this.distinct = distinct;
    }

    public List<String> getTable() {
        return table;
    }


    public void setWheres(List<Where> wheres) {
        this.wheres = wheres;
    }

    public void setField(List<String> field) {
        this.field = field;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

    public void setGroup(List<String> group) {
        this.group = group;
    }

    public void setTable(List<String> table) {
        this.table = table;
    }

    public void setAttrs(Map attrs) {
        this.attrs = attrs;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    /**
     * 设置属性
     * @param name
     * @param value
     * @return
     *
     */
    public void setAttribute(String name , Object value)
    {
        attrs.put(name , value);
    }

    /**
     * 获取属性
     * @param name
     * @return
     */
    public Object getAttribute(String name)
    {
        return attrs.get(name);
    }

    public Map getAttributes() {
        return attrs;
    }
}
