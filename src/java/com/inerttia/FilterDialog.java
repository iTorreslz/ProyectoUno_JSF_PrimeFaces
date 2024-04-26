package com.inerttia;

import es.inerttia.ittws.controllers.entities.Tercero;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Iván Torres
 */
@ManagedBean(name = "FilterDialog")
@ViewScoped
public class FilterDialog {

    //------------------------------------------------------------------------//
    //                               VARIABLES                                //
    //------------------------------------------------------------------------//
    @ManagedProperty(value = "#{Items}")
    private Items items;
    private List<Tercero> conTerceroDep;

    //------------------------------------------------------------------------//
    //                                  INIT                                  //
    //------------------------------------------------------------------------//
    @PostConstruct
    public void init() {

    }

    //
    //------------------------------------------------------------------------//
    //                           MÉTODOS DEL BEAN                             //
    //------------------------------------------------------------------------//
    //
    // CIERRA DIALOG
    public void closeDialog() {
        items.setConTerceroDep(this.conTerceroDep);
        PrimeFaces.current().dialog().closeDynamic("filterTables");
    }

    //------------------------------------------------------------------------//
    //                           GETTERS Y SETTERS                            //
    //------------------------------------------------------------------------//
    //
    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public List<Tercero> getConTerceroDep() {
        if (items.getConTerceroDep() != null) {
            conTerceroDep = items.getConTerceroDep();
        }
        return conTerceroDep;
    }

    public void setConTerceroDep(List<Tercero> conTerceroDep) {
        this.conTerceroDep = conTerceroDep;
    }
}
