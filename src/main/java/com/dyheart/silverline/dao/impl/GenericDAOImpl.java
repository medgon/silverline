package com.dyheart.silverline.dao.impl;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dyheart.silverline.dao.GenericDAO;
import com.dyheart.silverline.dto.AggregateDTO;
import com.dyheart.silverline.dto.FilterDTO;
import com.dyheart.silverline.dto.GroupDTO;
import com.dyheart.silverline.dto.SortDTO;




@Repository
@Transactional
public class GenericDAOImpl<T> implements GenericDAO<T> {
    @PersistenceContext
    protected EntityManager em; // protected for package and subclasses

    private Class<T> type;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public GenericDAOImpl() {
        Type t = getClass().getGenericSuperclass();
        if (t.getClass().getName().equals("java.lang.Class")) {
            type = null;
        } else {
            ParameterizedType pt = (ParameterizedType) t;
            type = (Class) pt.getActualTypeArguments()[0];
        }
    }

    public T add(T t) {
        em.persist(t);
        return t;
    }

    public void clear() {
        em.clear();
    }

    public void delete(Object id) {
        em.remove(em.getReference(type, id));
    }

    public T find(Object id) {
        return em.find(type, id);
    }

    public void flush() {
        em.flush();
    }

    public T update(T t) {
        return em.merge(t);
    }

    /**
     * Simple method to get all objects of this type. Should only be used during early development.
     *
     * @return List<T> List of the results of the type of object we are querying for.
     *
     */
    public List<T> getAll() {
        String queryString = "SELECT o FROM " + type.getSimpleName() + " o";
        TypedQuery<T> query = em.createQuery(queryString, type);
        return query.getResultList();
    }

    /**
     * Adds the elements from groupList to the beginning of the groupList. Calls getAllWithCriteria without passing groupList.
     *
     * @param page
     *            Starting page of the result set. a.k.a. 'skip'
     * @param pageSize
     *            Size of each result set returned
     * @param sortList
     *            List of sort criteria to use during sorting of the results
     * @param filterList
     *            List of filters to used for filtering the results
     * @param groupList
     *            List of groups used for grouping the results. In this method we add this list to the begining of the sortList
     *
     * @return List<T> List of the results of the type of object we are querying for.
     *
     */
    public List<T> getAllWithCriteria(Integer page, Integer pageSize, List<SortDTO> sortList, List<FilterDTO> filterList, List<GroupDTO> groupList) {

        if(!groupList.isEmpty()){
            // Transform the groupList to sortList and add the elements from the group list in the beginning
            List<SortDTO> groupAndSortList = new ArrayList<SortDTO>();
            for(GroupDTO groupDTO: groupList){
                SortDTO sortDTOfromGroupDTO = new SortDTO(groupDTO.getField(), groupDTO.getDir());
                groupAndSortList.add(sortDTOfromGroupDTO);
            }
            if(!sortList.isEmpty()){
                groupAndSortList.addAll(sortList);
            }
            sortList = groupAndSortList;
        }

        return  getAllWithCriteria(page, pageSize, sortList, filterList);

    }

    /**
     * Performs a criteria query on the given object type using the sort/filter lists passed in
     *
     * @param page
     *            Starting page of the result set. a.k.a. 'skip'
     * @param pageSize
     *            Size of each result set returned
     * @param sortList
     *            List of sort criteria to use during soring of the results
     * @param filterList
     *            List of filters to apply to the query
     *
     * @return List<T> List of the results of the type of object we are querying for.
     *
     */
    public List<T> getAllWithCriteria(Integer page, Integer pageSize, List<SortDTO> sortList, List<FilterDTO> filterList) {

        Date d1 = new Date();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);

        // Define the root entity we'll query from.
        Root<T> o = cq.from(type);
        cq.select(o);

        // Add sorts
        List<Order> orderList = this.getOrderByCriteria(cb, o, sortList);
        if (!orderList.isEmpty()) {
            cq.orderBy(orderList);
        }


        //check for searchIndicator  //this is temporary hacky way to handle basic disjunctions...  This Generic DAO should learn to recursively handle and/or stuff!
        boolean search = false;
        if(filterList != null && filterList.size() > 0) {
            if(filterList.get(0).getField().equals("searchIndicator") && filterList.get(0).getValue().equals("true")){
                search = true;
                filterList.remove(0);
            }
        }

        // Add filters
        List<Predicate> predicates = this.getFilterByCriteria(cb, o, filterList);
        if(search){  //since we're searching, we'll take the predicates and create a disjunction...  this OR this OR this OR this.
            Predicate orPredicate = cb.disjunction();
            for(Predicate predicate : predicates ) {
                orPredicate.getExpressions().add(predicate);
            }

            //used temporarily to only get employees from person table during search;
            Predicate employeeIdNotNullPredicate = cb.isNotNull(resolveFilterColumn(o, "employeeId"));
            Predicate andPredicate = cb.conjunction();
            andPredicate.getExpressions().add(employeeIdNotNullPredicate);
            andPredicate.getExpressions().add(orPredicate);
            cq.where(andPredicate);


//          cq.where(orPredicate);


        }
        else {  //since we're not searching, we'll add all the predicates which defaults to conjunctin...  this AND this AND this AND this.
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
            }
        }

        TypedQuery<T> tq = em.createQuery(cq);

        if (page != -1 && pageSize != -1) {
            tq.setFirstResult((page - 1) * pageSize); // skip
            tq.setMaxResults(pageSize); // take
        }

        List<T>  result = tq.getResultList();

        return result;
    }


    /**
     * Creates a list of Orders to be used for the criteria query
     *
     * @param cb
     *            CriteriaBuilder we are working with
     * @param o
     *            Root object we are querying from
     * @param sortList
     *            List of sort criteria from which to create the List<Order>
     *
     * @return List<Order> List of Orders we will apply to the Criteria Query
     *
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Order> getOrderByCriteria(CriteriaBuilder cb, Root<T> o, List<SortDTO> sortList) {
        List<Order> orderList = new ArrayList<Order>();
        if (sortList == null) {
            return orderList;
        }
        for (SortDTO sort : sortList) {
            try {
                Order order = null;

                Path path = this.resolveSortColumn(o, sort.getField());

                if (sort.getDir().equalsIgnoreCase("asc")) {
                    if(String.class.isAssignableFrom(path.getJavaType()))
                        order = cb.asc(cb.lower(path));
                    else
                        order = cb.asc(path);

                } else {
                    if(String.class.isAssignableFrom(path.getJavaType()))
                        order = cb.desc(cb.lower(path));
                    else
                        order = cb.desc(path);

                }

                orderList.add(order);
            } catch (IllegalArgumentException e) {
                // couldn't find field passed in.
                e.printStackTrace();
            }
        }

        return orderList;
    }

    /**
     * Creates a list of Predicate to be used for the criteria query
     *
     * @param cb
     *            CriteriaBuilder we are working with
     * @param o
     *            Root object we are querying from
     * @param filterList
     *            List of filter criteria from which to create the List<Predicate>
     *
     * @return List<Predicate> List of Predicates we will apply to the Criteria Query
     *
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Predicate> getFilterByCriteria(CriteriaBuilder cb, Root<T> o, List<FilterDTO> filterList) {
        List<Predicate> predicates = new ArrayList<Predicate>();


        if(filterList != null) {

            Predicate predicate;

            for (FilterDTO filter : filterList) {
                boolean dateField = false; // flag used to decide which value to put as the parameter.
                String operator = filter.getOperator();
                String field = filter.getField();
                String value = filter.getValue();

                Date valueDate = new Date(); // this is used if we are filtering on dates or timestamps. we need a date value in the predicate.

                try {

                    Path path = resolveFilterColumn(o, field);

                    // check the field type retrieved from the Path.
                    if (path.getJavaType().getSimpleName().equals("Date")) {
                        dateField = true;
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            valueDate = format.parse(value);
                        } catch (ParseException e) {
                            System.err.println("Could Not parse Date: " + value);
                        }
                    } else if (path.getJavaType().getSimpleName().equals("Timestamp")) {
                        dateField = true;
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            valueDate = format.parse(value);
                        } catch (ParseException e) {
                            System.err.println("Could Not parse Date: " + value);
                        }
                    }
                    else if (path.getJavaType().getSimpleName().equals("LocalDate")) {
                        dateField = true;
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            valueDate = format.parse(value);
                        } catch (ParseException e) {
                            System.err.println("Could Not parse Date: " + value);
                        }
                    }

                    if (operator.equals("in")) {
                        List<String> valueList = new ArrayList<String>(java.util.Arrays.asList(value.split("\\s*,\\s*")));
                        predicate = cb.isTrue(path.in(valueList));
                    } else if (operator.equals("contains")) {
                        predicate = cb.like(path, "%" + value + "%");
                    } else if (operator.equals("startswith")) {
                        predicate = cb.like(path, value + "%");
                    } else if (operator.equals("endswith")) {
                        predicate = cb.like(path, "%" + value);
                    } else if (operator.equals("eq")) {

                        // check if we're filtering on booleans. if so, the predicate is different.
                        if (path.getJavaType().getSimpleName().equals("boolean") || path.getJavaType().getSimpleName().equals("Boolean")) { // added "Boolean" for cases where the
                            // Boolean wrapper class is used.
                            if (value.equals("0") || value.equals("false")) {
                                predicate = cb.isFalse(path);
                            } else {
                                predicate = cb.isTrue(path);
                            }
                        } else if (dateField) {
                            predicate = cb.equal(path, valueDate);
                        }
                        else {
                            predicate = cb.equal(path, value);
                        }
                    }
                    else if (operator.equals("neq")) {
                        if (dateField) {
                            predicate = cb.notEqual(path, valueDate);
                        } else {
                            predicate = cb.notEqual(path, value);
                        }
                    } else if (operator.equals("like")) {
                        predicate = cb.like(path, value + "%");
                    } else if (operator.equals("lt")) {
                        if (dateField) {
                            predicate = cb.lessThan(path, valueDate);
                        } else {
                            predicate = cb.lessThan(path, value);
                        }
                    } else if (operator.equals("lte")) {
                        if (dateField) {
                            predicate = cb.lessThanOrEqualTo(path, valueDate);
                        } else {
                            predicate = cb.lessThanOrEqualTo(path, value);
                        }
                    } else if (operator.equals("gt")) {
                        if (dateField) {
                            predicate = cb.greaterThan(path, valueDate);
                        } else {
                            predicate = cb.greaterThan(path, value);
                        }
                    } else if (operator.equals("gte")) {
                        if (dateField) {
                            predicate = cb.greaterThanOrEqualTo(path, valueDate);
                        } else {
                            predicate = cb.greaterThanOrEqualTo(path, value);
                        }
                    } else if (operator.equals("isnull")) {
                        predicate = cb.isNull(path);
                    } else if (operator.equals("isnotnull")) {
                        predicate = cb.isNotNull(path);
                    } else {
                        continue; // if we didn't match the operator, don't do anything else with this one.
                    }

                    predicates.add(predicate);
                } catch (IllegalArgumentException e) {
                    // couldn't find field passed in.
                    e.printStackTrace();
                }
            }
        }
        return predicates;
    }

    /**
     * Determines the object Path using the sort column of the root object we are with.
     *
     * @param o
     *            Root object we are querying from
     * @param sortColumn
     *            column from which to determine the path
     *
     * @return Path Path on the object to field we are sorting on
     *
     */
    @SuppressWarnings({ "rawtypes" })
    public Path resolveSortColumn(Root<T> o, String sortColumn) {
        Path path = null;

        if (!sortColumn.contains(".")) {
            path = o.get(sortColumn);
        } else {
            // split up sortColumn on the dots.
            String[] sortArray = sortColumn.split("\\."); // i.e. someField.name or someField.someOtherField.description

            try {
                path = o.get(sortArray[0]); // 0 is the main class we're working with

                // loop through sortArray and add the rest of the path;
                for (int i = 1; i < sortArray.length; i++) {
                    path = path.get(sortArray[i]);
                }
            } catch (IllegalArgumentException e) {
                // problems b/c of subclass (generally)
                e.printStackTrace();
            }

            // if downcasting didn't work, just set to parent so it doesn't crash. although sorting wouldn't work right in this case.
            if (path == null) {
                path = o.get(sortArray[0]);
            }
        }

        return path;
    }

    /**
     * Determines the object Path using the filter column of the root object we are with.
     *
     * @param o
     *            Root object we are querying from
     * @param filterColumn
     *            column from which to determine the path
     *
     * @return Path Path on the object to field we are filtering on
     *
     */
    @SuppressWarnings({ "rawtypes" })
    public Path resolveFilterColumn(Root<T> o, String filterColumn) {
        Path path = null;

        if (!filterColumn.contains(".")) {
            if (filterColumn.toLowerCase().contains("date")) {
                path = o.<Date> get(filterColumn);
            } else {
                path = o.<String> get(filterColumn);
            }
        } else {
            String[] filterArray = filterColumn.split("\\."); // i.e. permissionGroup.name
            path = o.<String> get(filterArray[0]);
            Join join = null;
            for (int i = 1; i < filterArray.length; i++) {
                // this block checks to see if we're dealing with a Collection object (List, Set, etc..) then we make sure to Join that class so that we can filter on attributes of
                // the List elements.
                if (Collection.class.isAssignableFrom(path.getJavaType())) { // look at previous revision of this method to see another approach, but less generic;
                    // add join to the root if this is the first time in.;
                    if (join == null) {
                        join = o.join(filterArray[i - 1]); // join to the root
                    } else {
                        join = join.join(filterArray[i - 1]); // join to the previous join;
                    }
                    // now add the attribute that we're on to the path.
                    path = join.get(filterArray[i]);
                } else {
                    path = path.get(filterArray[i]);
                }
            }
        }

        return path;
    }

    /**
     * returns the total count of the resulting criteria query using filterList
     *
     * @param filterList
     *            List of filters to apply to the query
     *
     * @return Long total number of records in the resulting query
     *
     */
    public Long countAll(List<FilterDTO> filterList) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        // Define the root entity we'll query from.
        Root<T> o = cq.from(type);

        cq.select(cb.count(o));

        // Add filters
        List<Predicate> predicates = this.getFilterByCriteria(cb, o, filterList);

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[predicates.size()]));
        }

        return em.createQuery(cq).getSingleResult();

    }

    /**
     * Returns aggregate data for tables
     *
     * Note: The order of the aggregateDTOs will be the same as the order of returned data... this is for kendo.
     *
     * @param aggregateList
     *            list of aggregateDTOs containing aggregate info. field should be actual java path
     * @param filterList
     *            list of filters to use in query
     * @return List<Object[]> of values for each field passed in. returns List of minimum length 1. for kendo...
     *
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Object[]> getAggregateData(List<AggregateDTO> aggregateList, List<FilterDTO> filterList) {

        ArrayList<Object[]> returnData = new ArrayList<Object[]>();
        Object[] results = null;

        if (aggregateList.size() == 0) {
            results = new Object[1];
        } else {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<?> cq = cb.createQuery(Object[].class);

            // Define the root entity we'll query from.
            Root<T> o = cq.from(type);

            List<Selection<?>> selectionList = new ArrayList<Selection<?>>();
            // add the columns we are selecting the sum for. select sum(someField.amount) as someField_amount
            for (AggregateDTO aggregate : aggregateList) {
                Path path = resolveFilterColumn(o, aggregate.getField());
                // we're assuming 'sum' aggregates for now. if we add other aggregates, we should alter this..
                if (aggregate.getAggregate().equals("sum")) {
                    selectionList.add(cb.sum(path)); // .alias(columnName.replace(".", "_")));
                }
            }

            cq.multiselect(selectionList);

            // Add filters
            List<Predicate> predicates = this.getFilterByCriteria(cb, o, filterList);

            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
            }

            if (selectionList.size() == 0) {
                results = new Object[1];
            } else if (selectionList.size() > 1) {
                results = (Object[]) em.createQuery(cq).getSingleResult();
            } else {
                results = new Object[1];
                results[0] = em.createQuery(cq).getSingleResult();
            }

        }

        returnData.add(results);

        return returnData;
    }
}

