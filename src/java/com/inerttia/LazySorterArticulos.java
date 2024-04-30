package com.inerttia;

import es.inerttia.ittws.controllers.entities.custom.Articulo;
import java.util.Comparator;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Iván Torres
 */
public class LazySorterArticulos implements Comparator<Articulo> {

    private String sortField;
    private SortOrder sortOrder;

    public LazySorterArticulos(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(Articulo articulo1, Articulo articulo2) {
        try {
            Object value1 = ShowcaseUtil.getPropertyValueViaReflection(articulo1, sortField);
            Object value2 = ShowcaseUtil.getPropertyValueViaReflection(articulo2, sortField);

            int value = ((Comparable) value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
