/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.support;

import java.util.List;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.Projectable;
import com.mysema.query.QueryModifiers;
import com.mysema.query.SearchResults;
import com.mysema.query.SimpleProjectable;
import com.mysema.query.SimpleQuery;
import com.mysema.query.types.Expression;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.Predicate;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * SimpleQueryAdapter is an apdater implementation for the SimpleQuery and SimpleProjectabl interfaces
 *
 * @author tiwe
 *
 * @param <T> type of entity
 */
public class SimpleProjectableAdapter<T> implements SimpleQuery<SimpleProjectableAdapter<T>>, SimpleProjectable<T>{

    private final Projectable projectable;

    private final Expression<T> projection;

    private final SimpleQuery<?> query;

    @SuppressWarnings("BC_UNCONFIRMED_CAST")
    public <Q extends SimpleQuery<?> & Projectable> SimpleProjectableAdapter(Q query, Expression<T> projection){
        // NOTE : this is a correct cast which is not handled properly by FindBugs
        this(query, query, projection);
    }

    public SimpleProjectableAdapter(SimpleQuery<?> query, Projectable projectable, Expression<T> projection){
        this.query = query;
        this.projectable = projectable;
        this.projection = projection;
    }

    @Override
    public boolean exists(){
        return projectable.exists();
    }

    @Override
    public boolean notExists(){
        return projectable.notExists();
    }

    @Override
    public long count() {
        return projectable.count();
    }

    @Override
    public long countDistinct() {
        return projectable.countDistinct();
    }

    @Override
    public SimpleProjectableAdapter<T> distinct(){
        query.distinct();
        return this;
    }

    @Override
    public SimpleProjectableAdapter<T> limit(long limit) {
        query.limit(limit);
        return this;
    }

    @Override
    public CloseableIterator<T> iterate(){
        return projectable.iterate(projection);
    }

    @Override
    public CloseableIterator<T> iterateDistinct(){
        return projectable.iterateDistinct(projection);
    }

    @Override
    public List<T> list() {
        return projectable.list(projection);
    }

    @Override
    public List<T> listDistinct() {
        return projectable.listDistinct(projection);
    }

    @Override
    public SearchResults<T> listDistinctResults() {
        return projectable.listDistinctResults(projection);
    }

    @Override
    public SearchResults<T> listResults() {
        return projectable.listResults(projection);
    }

    @Override
    public SimpleProjectableAdapter<T> offset(long offset) {
        query.offset(offset);
        return this;
    }

    @Override
    public SimpleProjectableAdapter<T> orderBy(OrderSpecifier<?>... o) {
        query.orderBy(o);
        return this;
    }

    @Override
    public SimpleProjectableAdapter<T> restrict(QueryModifiers modifiers) {
        query.restrict(modifiers);
        return this;
    }

    @Override
    public <P> SimpleProjectableAdapter<T> set(ParamExpression<P> param, P value) {
        query.set(param, value);
        return this;
    }

    @Override
    public String toString(){
        return query.toString();
    }

    @Override
    public T singleResult() {
        return projectable.singleResult(projection);
    }

    @Override
    public T uniqueResult() {
        return projectable.uniqueResult(projection);
    }

    @Override
    public SimpleProjectableAdapter<T> where(Predicate... e) {
        query.where(e);
        return this;
    }

}