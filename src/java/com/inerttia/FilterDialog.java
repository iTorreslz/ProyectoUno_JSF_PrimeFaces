package com.inerttia;

import es.inerttia.ittws.controllers.FamiliaController;
import es.inerttia.ittws.controllers.MarcaController;
import es.inerttia.ittws.controllers.NivelClasificacionController;
import es.inerttia.ittws.controllers.TerceroController;
import es.inerttia.ittws.controllers.entities.Familia;
import es.inerttia.ittws.controllers.entities.Marca;
import es.inerttia.ittws.controllers.entities.NivelClasificacion;
import es.inerttia.ittws.controllers.entities.Tercero;
import es.inerttia.ittwscomun.Configuracion;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
    private List<Tercero> conTerceroDep;
    private List<Familia> familias;
    private List<Marca> marcas;
    private List<NivelClasificacion> nivelesClasificacion;

    private List<Tercero> tercerosSelected;
    private List<Familia> familiasSelected;
    private List<Marca> marcasSelected;
    private List<NivelClasificacion> nivelesClasifSelected;

    String ventana;

    //------------------------------------------------------------------------//
    //                                  INIT                                  //
    //------------------------------------------------------------------------//
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> param = context.getExternalContext().getRequestParameterMap();
        String params = param.get("params");
        if (params != null && !params.isEmpty()) {
            List<String> data = Arrays.asList(params.split(","));
            String type = data.get(0);
            Configuracion conf = new Configuracion();

            switch (type) {
                case "tercero_dep":
                    TerceroController ctlTercero = new TerceroController(conf);
                    conTerceroDep = ctlTercero.getTercerosDeposito();
                    ventana = "tercero_dep";
                    break;
                case "familia":
                    FamiliaController ctlFamilia = new FamiliaController(conf);
                    familias = ctlFamilia.getFamiliasSeleccion();
                    ventana = "familia";
                    break;
                case "marca":
                    MarcaController ctlMarca = new MarcaController(conf);
                    marcas = ctlMarca.getMarcasSeleccion();
                    ventana = "marca";
                    break;
                case "niv_clasif":
                    NivelClasificacionController ctlNivelClasif = new NivelClasificacionController(conf);
                    nivelesClasificacion = ctlNivelClasif.getNivelesClasificacion();
                    ventana = "niv_clasif";
                    break;
            }
            conf.cerrar();
        }
    }

    //
    //------------------------------------------------------------------------//
    //                           MÉTODOS DEL BEAN                             //
    //------------------------------------------------------------------------//
    //
    // CIERRA DIALOG
    public String setTitle() {
        switch (ventana) {
            case "tercero_dep":
                return "Terceros Depósito";
            case "familia":
                return "Familias";
            case "marca":
                return "Marcas";
            case "niv_clasif":
                return "Niveles de Clasificación";
            default:
                return "";
        }
    }
    
    public void closeDialog() {
        switch (ventana) {
            case "tercero_dep":
                PrimeFaces.current().dialog().closeDynamic(tercerosSelected);
                break;
            case "familia":
                PrimeFaces.current().dialog().closeDynamic(familiasSelected);
                break;
            case "marca":
                PrimeFaces.current().dialog().closeDynamic(marcasSelected);
                break;
            case "niv_clasif":
                PrimeFaces.current().dialog().closeDynamic(nivelesClasifSelected);
                break;
        }
    }

    //------------------------------------------------------------------------//
    //                           GETTERS Y SETTERS                            //
    //------------------------------------------------------------------------//
    //
    public List<Tercero> getConTerceroDep() {
//        if (items.getConTerceroDep() != null) {
//            conTerceroDep = items.getConTerceroDep();
//        }
        return conTerceroDep;
    }

    public void setConTerceroDep(List<Tercero> conTerceroDep) {
        this.conTerceroDep = conTerceroDep;
    }

    public List<Familia> getFamilias() {
        return familias;
    }

    public void setFamilias(List<Familia> familias) {
        this.familias = familias;
    }

    public List<Marca> getMarcas() {
        return marcas;
    }

    public void setMarcas(List<Marca> marcas) {
        this.marcas = marcas;
    }

    public List<NivelClasificacion> getNivelesClasificacion() {
        return nivelesClasificacion;
    }

    public void setNivelesClasificacion(List<NivelClasificacion> nivelesClasificacion) {
        this.nivelesClasificacion = nivelesClasificacion;
    }

    public List<Tercero> getTercerosSelected() {
        return tercerosSelected;
    }

    public void setTercerosSelected(List<Tercero> tercerosSelected) {
        this.tercerosSelected = tercerosSelected;
    }

    public List<Familia> getFamiliasSelected() {
        return familiasSelected;
    }

    public void setFamiliasSelected(List<Familia> familiasSelected) {
        this.familiasSelected = familiasSelected;
    }

    public List<Marca> getMarcasSelected() {
        return marcasSelected;
    }

    public void setMarcasSelected(List<Marca> marcasSelected) {
        this.marcasSelected = marcasSelected;
    }

    public List<NivelClasificacion> getNivelesClasifSelected() {
        return nivelesClasifSelected;
    }

    public void setNivelesClasifSelected(List<NivelClasificacion> nivelesClasifSelected) {
        this.nivelesClasifSelected = nivelesClasifSelected;
    }

    public String getVentana() {
        return ventana;
    }

    public void setVentana(String ventana) {
        this.ventana = ventana;
    }
}
