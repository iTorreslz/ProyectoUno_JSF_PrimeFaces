package com.inerttia;

import es.inerttia.ittws.controllers.ArticuloClasificacionController;
import es.inerttia.ittws.controllers.ClasificacionController;
import es.inerttia.ittws.controllers.FamiliaController;
import es.inerttia.ittws.controllers.MarcaController;
import es.inerttia.ittws.controllers.NivelClasificacionController;
import es.inerttia.ittws.controllers.PaisController;
import es.inerttia.ittws.controllers.TerceroController;
import es.inerttia.ittws.controllers.TipoArticuloController;
import es.inerttia.ittws.controllers.TipoPickingController;
import es.inerttia.ittws.controllers.entities.ArticuloClasificacion;
import es.inerttia.ittws.controllers.entities.Clasificacion;
import es.inerttia.ittws.controllers.entities.Familia;
import es.inerttia.ittws.controllers.entities.Marca;
import es.inerttia.ittws.controllers.entities.NivelClasificacion;
import es.inerttia.ittws.controllers.entities.Pais;
import es.inerttia.ittws.controllers.entities.Tercero;
import es.inerttia.ittws.controllers.entities.TipoArticulo;
import es.inerttia.ittws.controllers.entities.TipoPicking;
import es.inerttia.ittws.controllers.entities.custom.Articulo;
import es.inerttia.ittwscomun.Configuracion;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Iván Torres
 */
@ManagedBean(name = "Items")
@ViewScoped
public class Items {

    //------------------------------------------------------------------------//
    //                               VARIABLES                                //
    //------------------------------------------------------------------------//
    // LAZY TABLE
    private LazyProductosDataModel lazyItems;

    // ARTICULOS
    private List<Articulo> articulos;

    private List<Tercero> conTerceroDep;
    private List<Tercero> terceros;
    private int idArticulo;
    private String descripcion;
    private String referencia;
    private int codBarras;
    private String clasificacion;
    private List<Clasificacion> clasificaciones;
    private int estado;

    private int familia;
    private List<Familia> familias;
    private int marca;
    private List<Marca> marcas;
    private int nivelesClasificacion;
    private List<NivelClasificacion> niveles;
    private int bloqueo;
    private int grupoArticulos;
    private int etiquetaDe;
    private List<Pais> paises;
    private int etiquetado;
    private int controlLote;
    private int controlCaducidad;
    private int pendienteVerificacion;
    private int fechaUltModificacion;
    private int pesoBruto;
    private int volumenBruto;
    private int controlSeries;
    private int tipoArticulo;
    private List<TipoArticulo> tiposArticulo;
    private int stock;
    private int preventa;
    private int tipoPicking;
    private List<TipoPicking> tiposPicking;
    private int artClasificacion;
    private List<ArticuloClasificacion> articulosClasif;
    private double maxPesoBruto;
    private double minPesoBruto;

    //------------------------------------------------------------------------//
    //                                  INIT                                  //
    //------------------------------------------------------------------------//
    //
    @PostConstruct
    public void init() {

        clasificacion = "-1";

    }

    //------------------------------------------------------------------------//
    //                           MÉTODOS DEL BEAN                             //
    //------------------------------------------------------------------------//
    //
    // ABRE FILTER DIALOGS
    public void findDialogs() {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("resizable", false);
        options.put("draggable", false);
        PrimeFaces.current().dialog().openDynamic("filterTables", options, null);
    }
    
    public void ver(){
        System.out.println(this.conTerceroDep.size());
    }

    //------------------------------------------------------------------------//
    //                           GETTERS Y SETTERS                            //
    //------------------------------------------------------------------------//
    //
    public LazyProductosDataModel getLazyItems() {
        return lazyItems;
    }

    public void setLazyItems(LazyProductosDataModel lazyItems) {
        this.lazyItems = lazyItems;
    }

    public List<Articulo> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<Articulo> articulos) {
        this.articulos = articulos;
    }

    public List<Tercero> getConTerceroDep() {
        return conTerceroDep;
    }

    public void setConTerceroDep(List<Tercero> conTerceroDep) {
        this.conTerceroDep = conTerceroDep;
    }

    public List<Tercero> getTerceros() {
        if (terceros == null) {
            Configuracion conf = new Configuracion();
            TerceroController ctl = new TerceroController(conf);
            terceros = ctl.getTercerosDeposito();
            conf.cerrar();
        }
        return terceros;
    }

    public void setTerceros(List<Tercero> terceros) {
        this.terceros = terceros;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public int getCodBarras() {
        return codBarras;
    }

    public void setCodBarras(int codBarras) {
        this.codBarras = codBarras;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public List<Clasificacion> getClasificaciones() {
        if (clasificaciones == null) {
            Configuracion conf = new Configuracion();
            ClasificacionController ctl = new ClasificacionController(conf);
            clasificaciones = ctl.getClasificaciones();
            conf.cerrar();
        }
        return clasificaciones;
    }

    public void setClasificaciones(List<Clasificacion> clasificaciones) {
        this.clasificaciones = clasificaciones;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getFamilia() {
        return familia;
    }

    public void setFamilia(int familia) {
        this.familia = familia;
    }

    public List<Familia> getFamilias() {
        if (familias == null) {
            Configuracion conf = new Configuracion();
            FamiliaController ctl = new FamiliaController(conf);
            familias = ctl.getFamiliasSeleccion();
            conf.cerrar();
        }
        return familias;
    }

    public void setFamilias(List<Familia> familias) {
        this.familias = familias;
    }

    public int getMarca() {
        return marca;
    }

    public void setMarca(int marca) {
        this.marca = marca;
    }

    public List<Marca> getMarcas() {
        if (marcas == null) {
            Configuracion conf = new Configuracion();
            MarcaController ctl = new MarcaController(conf);
            marcas = ctl.getMarcasSeleccion();
            conf.cerrar();
        }
        return marcas;
    }

    public void setMarcas(List<Marca> marcas) {
        this.marcas = marcas;
    }

    public int getNivelesClasificacion() {
        return nivelesClasificacion;
    }

    public void setNivelesClasificacion(int nivelesClasificacion) {
        this.nivelesClasificacion = nivelesClasificacion;
    }

    public List<NivelClasificacion> getNiveles() {
        if (niveles == null) {
            Configuracion conf = new Configuracion();
            NivelClasificacionController ctl = new NivelClasificacionController(conf);
            niveles = ctl.getNivelesClasificacion();
            conf.cerrar();
        }
        return niveles;
    }

    public void setNiveles(List<NivelClasificacion> niveles) {
        this.niveles = niveles;
    }

    public int getBloqueo() {
        return bloqueo;
    }

    public void setBloqueo(int bloqueo) {
        this.bloqueo = bloqueo;
    }

    public int getGrupoArticulos() {
        return grupoArticulos;
    }

    public void setGrupoArticulos(int grupoArticulos) {
        this.grupoArticulos = grupoArticulos;
    }

    public int getEtiquetaDe() {
        return etiquetaDe;
    }

    public void setEtiquetaDe(int etiquetaDe) {
        this.etiquetaDe = etiquetaDe;
    }

    public List<Pais> getPaises() {
        if (paises == null) {
            Configuracion conf = new Configuracion();
            PaisController ctl = new PaisController(conf);
            paises = ctl.getPaises();
            conf.cerrar();
        }
        return paises;
    }

    public void setPaises(List<Pais> paises) {
        this.paises = paises;
    }

    public int getEtiquetado() {
        return etiquetado;
    }

    public void setEtiquetado(int etiquetado) {
        this.etiquetado = etiquetado;
    }

    public int getControlLote() {
        return controlLote;
    }

    public void setControlLote(int controlLote) {
        this.controlLote = controlLote;
    }

    public int getControlCaducidad() {
        return controlCaducidad;
    }

    public void setControlCaducidad(int controlCaducidad) {
        this.controlCaducidad = controlCaducidad;
    }

    public int getPendienteVerificacion() {
        return pendienteVerificacion;
    }

    public void setPendienteVerificacion(int pendienteVerificacion) {
        this.pendienteVerificacion = pendienteVerificacion;
    }

    public int getFechaUltModificacion() {
        return fechaUltModificacion;
    }

    public void setFechaUltModificacion(int fechaUltModificacion) {
        this.fechaUltModificacion = fechaUltModificacion;
    }

    public int getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(int pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public int getVolumenBruto() {
        return volumenBruto;
    }

    public void setVolumenBruto(int volumenBruto) {
        this.volumenBruto = volumenBruto;
    }

    public int getControlSeries() {
        return controlSeries;
    }

    public void setControlSeries(int controlSeries) {
        this.controlSeries = controlSeries;
    }

    public int getTipoArticulo() {
        return tipoArticulo;
    }

    public void setTipoArticulo(int tipoArticulo) {
        this.tipoArticulo = tipoArticulo;
    }

    public List<TipoArticulo> getTiposArticulo() {
        if (tiposArticulo == null) {
            Configuracion conf = new Configuracion();
            TipoArticuloController ctl = new TipoArticuloController(conf);
            tiposArticulo = ctl.getTiposArticulo();
            conf.cerrar();
        }
        return tiposArticulo;
    }

    public void setTiposArticulo(List<TipoArticulo> tiposArticulo) {
        this.tiposArticulo = tiposArticulo;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getPreventa() {
        return preventa;
    }

    public void setPreventa(int preventa) {
        this.preventa = preventa;
    }

    public int getTipoPicking() {
        return tipoPicking;
    }

    public void setTipoPicking(int tipoPicking) {
        this.tipoPicking = tipoPicking;
    }

    public List<TipoPicking> getTiposPicking() {
        if (tiposPicking == null) {
            Configuracion conf = new Configuracion();
            TipoPickingController ctl = new TipoPickingController(conf);
            tiposPicking = ctl.getTiposPicking();
            conf.cerrar();
        }
        return tiposPicking;
    }

    public void setTiposPicking(List<TipoPicking> tiposPicking) {
        this.tiposPicking = tiposPicking;
    }

    public int getArtClasificacion() {
        return artClasificacion;
    }

    public void setArtClasificacion(int artClasificacion) {
        this.artClasificacion = artClasificacion;
    }

    public List<ArticuloClasificacion> getArticulosClasif() {
        if (articulosClasif == null) {
            Configuracion conf = new Configuracion();
            ArticuloClasificacionController ctl = new ArticuloClasificacionController(conf);
            articulosClasif = ctl.getArticuloClasificacions();
            conf.cerrar();
        }
        return articulosClasif;
    }

    public void setArticulosClasif(List<ArticuloClasificacion> articulosClasif) {
        this.articulosClasif = articulosClasif;
    }

    public double getMaxPesoBruto() {
        return maxPesoBruto;
    }

    public void setMaxPesoBruto(double maxPesoBruto) {
        this.maxPesoBruto = maxPesoBruto;
    }

    public double getMinPesoBruto() {
        return minPesoBruto;
    }

    public void setMinPesoBruto(double minPesoBruto) {
        this.minPesoBruto = minPesoBruto;
    }
}
