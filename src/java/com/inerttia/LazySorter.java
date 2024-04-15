package com.inerttia;

import com.inerttia.clases.Producto;
import java.util.Comparator;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Iván Torres
 */
public class LazySorter implements Comparator<Producto> {

    private String sortField;
    private SortOrder sortOrder;

    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(Producto producto1, Producto producto2) {
        try {
            Object value1 = ShowcaseUtil.getPropertyValueViaReflection(producto1, sortField);
            Object value2 = ShowcaseUtil.getPropertyValueViaReflection(producto2, sortField);

            int value = ((Comparable) value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
