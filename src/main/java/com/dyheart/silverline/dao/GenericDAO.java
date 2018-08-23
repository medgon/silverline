package com.dyheart.silverline.dao;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.dyheart.silverline.dto.AggregateDTO;
import com.dyheart.silverline.dto.FilterDTO;
import com.dyheart.silverline.dto.GroupDTO;
import com.dyheart.silverline.dto.SortDTO;

public interface GenericDAO<T> {

    T add(T t);

    void clear();

    void delete(Object id);

    T find(Object id);

    void flush();

    List<T> getAll();

    T update(T t);

    Long countAll(List<FilterDTO> filterList);

    List<Object[]> getAggregateData(List<AggregateDTO> aggregateList, List<FilterDTO> filterList);

    List<T> getAllWithCriteria(Integer page, Integer pageSize, List<SortDTO> sortList, List<FilterDTO> filterList, List<GroupDTO> groupList);

    List<T> getAllWithCriteria(Integer page, Integer pageSize, List<SortDTO> sortList, List<FilterDTO> filterList);

    List<Predicate> getFilterByCriteria(CriteriaBuilder cb, Root<T> o, List<FilterDTO> filterList);

    List<Order> getOrderByCriteria(CriteriaBuilder cb, Root<T> o, List<SortDTO> sortList);

    @SuppressWarnings("rawtypes")
    Path resolveFilterColumn(Root<T> o, String filterColumn);

    @SuppressWarnings("rawtypes")
    Path resolveSortColumn(Root<T> o, String sortColumn);
}

