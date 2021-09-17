package com.jntoo.db.model;

import com.jntoo.db.has.*;
import com.jntoo.db.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Options {

    private List<WhereModel> where;
    private List<String> field;
    private List<String> order;
    private List<String> group;
    private List<String> table;
    private List<String> join;
    private List<HasQuery> hasQuery;
    private LimitModel limit;
    private String alias;
    private String having;
    private String lock;
    private boolean distinct;
    private String force;

    public Options()
    {
        where = new ArrayList();
        field = new ArrayList();
        order = new ArrayList();
        group = new ArrayList();
        table = new ArrayList();
        join = new ArrayList();

    }

    public boolean isTable()
    {
        return table.size() > 0;
    }

    public void addTable(String table)
    {
        this.table.add(table);
    }

    public boolean isHasQuery()
    {
        return hasQuery != null && hasQuery.size() > 0;
    }

    public void addHasQuery(HasQuery table)
    {
        if(!isHasQuery()){
            hasQuery = new ArrayList();
        }
        this.hasQuery.add(table);
    }

    public boolean isJoin()
    {
        return table.size() > 0;
    }

    public void addJoin(String table)
    {
        this.join.add(table);
    }

    public boolean isWhere()
    {
        return where.size() > 0;
    }

    public void addWhere(WhereModel where)
    {
        this.where.add(where);
    }

    public boolean isField()
    {
        return field.size() > 0;
    }

    public void addField(String field)
    {
        this.field.add(field);
    }

    public boolean isOrder()
    {
        return order.size() > 0;
    }

    public void addOrder(String order)
    {
        this.order.add(order);
    }

    public boolean isGroup()
    {
        return group.size() > 0;
    }

    public void addGroup(String group)
    {
        this.group.add(group);
    }

    public boolean isLimit()
    {
        return limit != null;
    }

    public void setLimit(Long size)
    {
        limit = new LimitModel(size);
    }
    public void setLimit(Long offset , Long size)
    {
        limit = new LimitModel(offset , size);
    }

    public List<WhereModel> getWhere() {
        return where;
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

    public LimitModel getLimit() {
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


    public List<String> getTable() {
        return table;
    }
    public void setWhere(List<WhereModel> where) {
        this.where = where;
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
    public void setLimit(LimitModel limit) {
        this.limit = limit;
    }

    public List<String> getJoin() {
        return join;
    }

    public void setJoin(List<String> join) {
        this.join = join;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }


    public List<HasQuery> getHasQuery() {
        return hasQuery;
    }

    public void setHasQuery(List<HasQuery> hasQuery) {
        this.hasQuery = hasQuery;
    }
}
