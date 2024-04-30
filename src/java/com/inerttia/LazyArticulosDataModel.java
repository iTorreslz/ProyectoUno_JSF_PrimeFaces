package com.inerttia;

import es.inerttia.ittws.controllers.entities.custom.Articulo;
import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.context.FacesContext;
import org.apache.commons.collections4.ComparatorUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LocaleUtils;

/**
 *
 * @author Iván Torres
 */
public class LazyArticulosDataModel extends LazyDataModel<Articulo> {

    private List<Articulo> datasource;

    public LazyArticulosDataModel(List<Articulo> datasource) {
        this.datasource = datasource;
    }

    @Override
    public Articulo getRowData(String rowKey) {
        for (Articulo articulo : datasource) {
            if (articulo.getIdArticulo().equals(rowKey)) {
                return articulo;
            }
        }

        return null;
    }

    @Override
    public String getRowKey(Articulo articulo) {
        return String.valueOf(articulo.getIdArticulo());
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) datasource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();
    }

    @Override
    public List<Articulo> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        // apply offset & filters
        List<Articulo> articulos = datasource.stream()
                .skip(offset)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<Articulo>> comparators = sortBy.values().stream()
                    .map(o -> new LazySorterArticulos(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<Articulo> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            articulos.sort(cp);
        }

        return articulos;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();

            try {
                Object columnValue = String.valueOf(ShowcaseUtil.getPropertyValueViaReflection(o, filter.getField()));
                matching = constraint.isMatching(context, columnValue, filterValue, LocaleUtils.getCurrentLocale());
            } catch (ReflectiveOperationException | IntrospectionException e) {
                matching = false;
            }

            if (!matching) {
                break;
            }
        }

        return matching;
    }
}
