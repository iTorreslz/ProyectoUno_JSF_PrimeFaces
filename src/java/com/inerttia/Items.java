package com.inerttia;

import es.inerttia.ittws.controllers.ArticuloClasificacionController;
import es.inerttia.ittws.controllers.ArticuloController;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

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
    private LazyArticulosDataModel lazyItems;

    // ARTICULOS
    private List<Articulo> articulos;
    private List<Articulo> filteredArticulos;

    // SELECTED
    private String terceroStringSelected;
    private String familiaStringSelected;
    private String marcaStringSelected;
    private String nivelStringSelected;

    // VENTANA
    private String ultimaVentana;

    private List<Tercero> tercerosSelected;
    private List<Tercero> terceros;
    private String idArticulo;
    private String descripcion;
    private String referencia;
    private String codBarras;
    private String clasificacion;
    private List<Clasificacion> clasificaciones;
    private int estado;

    private List<Familia> familiasSelected;
    private List<Familia> familias;
    private List<Marca> marcasSelected;
    private List<Marca> marcas;
    private List<NivelClasificacion> nivelesSelected;
    private List<NivelClasificacion> niveles;
    private int bloqueo;
    private int grupoArticulos;
    private String etiquetaDe;
    private List<Pais> paises;
    private String etiquetado;
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

    // IDs STRING
    String idStringTerceros;
    String idStringFamilias;
    String idStringMarcas;
    String idStringNiveles;

    //------------------------------------------------------------------------//
    //                                  INIT                                  //
    //------------------------------------------------------------------------//
    //
    @PostConstruct
    public void init() {
        clasificacion = "-1";
        estado = -1;
        bloqueo = -1;
        grupoArticulos = -1;
        etiquetaDe = "-1";
        etiquetado = "-1";
        controlLote = -1;
        controlCaducidad = -1;
        pendienteVerificacion = -1;
        fechaUltModificacion = -1;
        pesoBruto = -1;
        volumenBruto = -1;
        controlSeries = -1;
        tipoArticulo = -1;
        stock = -1;
        preventa = -1;
        tipoPicking = -1;
        artClasificacion = -1;
        minPesoBruto = -1;
        maxPesoBruto = -1;
    }

    //------------------------------------------------------------------------//
    //                           MÉTODOS DEL BEAN                             //
    //------------------------------------------------------------------------//
    //
    // FORMATEA UNA FECHA A "dd/MM/yyyy"
    public String formatDate(Date fecha) {
        if (fecha != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = sdf.format(fecha);
            return fechaFormateada;
        }
        return null;
    }

    // ABRE FILTER DIALOGS
    public void findDialogs(String tipoVentana) {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("resizable", false);

        Map<String, List<String>> params = new HashMap<>();
        List<String> paramsVars = new ArrayList<>();
        ultimaVentana = tipoVentana;
        paramsVars.add(tipoVentana);
        params.put("params", paramsVars);

        PrimeFaces.current().dialog().openDynamic("filterTables", options, params);
    }

    public String getTercerosIDString() {
        if (tercerosSelected != null && !tercerosSelected.isEmpty()) {
            idStringTerceros = "(";
            for (int i = 0; i < tercerosSelected.size(); i++) {
                if (i != (tercerosSelected.size() - 1)) {
                    idStringTerceros += "'" + tercerosSelected.get(i).getIdTercero() + "',";
                } else {
                    idStringTerceros += "'" + tercerosSelected.get(i).getIdTercero() + "')";
                }
            }
            return idStringTerceros;
        } else {
            return null;
        }
    }

    public String getFamiliasIDString() {
        if (familiasSelected != null && !familiasSelected.isEmpty()) {
            idStringFamilias = "(";
            for (int i = 0; i < familiasSelected.size(); i++) {
                if (i != (familiasSelected.size() - 1)) {
                    idStringFamilias += "'" + familiasSelected.get(i).getIdFamilia() + "',";
                } else {
                    idStringFamilias += "'" + familiasSelected.get(i).getIdFamilia() + "')";
                }
            }
            return idStringFamilias;
        } else {
            return null;
        }
    }

    public String getMarcasIDString() {
        if (marcasSelected != null && !marcasSelected.isEmpty()) {
            idStringMarcas = "(";
            for (int i = 0; i < marcasSelected.size(); i++) {
                if (i != (marcasSelected.size() - 1)) {
                    idStringMarcas += "'" + marcasSelected.get(i).getIdMarca() + "',";
                } else {
                    idStringMarcas += "'" + marcasSelected.get(i).getIdMarca() + "')";
                }
            }
            return idStringMarcas;
        } else {
            return null;
        }
    }

    public String getNivelesIDString() {
        if (nivelesSelected != null && !nivelesSelected.isEmpty()) {
            idStringNiveles = "(";
            for (int i = 0; i < nivelesSelected.size(); i++) {
                if (i != (nivelesSelected.size() - 1)) {
                    idStringNiveles += "'" + nivelesSelected.get(i).getIdNivelClasificacion() + "',";
                } else {
                    idStringNiveles += "'" + nivelesSelected.get(i).getIdNivelClasificacion() + "')";
                }
            }
            return idStringNiveles;
        } else {
            return null;
        }
    }

    public void search() {
        Configuracion conf = new Configuracion();
        ArticuloController ctl = new ArticuloController(conf);
        articulos = ctl.getArticulosConsultaGeneral(1, 1, etiquetado, idArticulo, descripcion, referencia, codBarras, clasificacion, estado, bloqueo, grupoArticulos,
                getTercerosIDString(), getFamiliasIDString(), getMarcasIDString(), getNivelesIDString(), controlLote, controlSeries,
                tipoArticulo, controlCaducidad, pendienteVerificacion, fechaUltModificacion, pesoBruto, volumenBruto, -1, stock, preventa, 0,
                etiquetaDe, tipoPicking, artClasificacion, minPesoBruto, maxPesoBruto);

        lazyItems = new LazyArticulosDataModel(articulos);

        conf.cerrar();
    }

    public void clearFilter(String tipoFiltro) {
        switch (tipoFiltro) {
            case "tercero_dep":
                this.terceroStringSelected = "";
                this.tercerosSelected = null;
                break;
            case "familia":
                this.familiaStringSelected = "";
                this.familiasSelected = null;
                break;
            case "marca":
                this.marcaStringSelected = "";
                this.marcasSelected = null;
                break;
            case "niv_clasif":
                this.nivelStringSelected = "";
                this.nivelesSelected = null;
                break;
        }
    }

    public void afterCloseDialog(SelectEvent event) {
        switch (ultimaVentana) {
            case "tercero_dep":
                terceroStringSelected = "";
                if ((List<Tercero>) event.getObject() != null && !((List<Tercero>) event.getObject()).isEmpty()) {
                    tercerosSelected = (List<Tercero>) event.getObject();
                    if (tercerosSelected.size() > 1) {
                        terceroStringSelected = "VARIOS";
                    } else {
                        terceroStringSelected = tercerosSelected.get(0).getNombreComercial();
                    }
                }
                break;
            case "familia":
                familiaStringSelected = "";
                if ((List<Familia>) event.getObject() != null && !((List<Familia>) event.getObject()).isEmpty()) {
                    familiasSelected = (List<Familia>) event.getObject();
                    if (familiasSelected.size() > 1) {
                        familiaStringSelected = "VARIOS";
                    } else {
                        familiaStringSelected = familiasSelected.get(0).getNombre();
                    }
                }
                break;
            case "marca":
                marcaStringSelected = "";
                if ((List<Marca>) event.getObject() != null && !((List<Marca>) event.getObject()).isEmpty()) {
                    marcasSelected = (List<Marca>) event.getObject();
                    if (marcasSelected.size() > 1) {
                        marcaStringSelected = "VARIOS";
                    } else {
                        marcaStringSelected = marcasSelected.get(0).getNombre();
                    }
                }
                break;
            case "niv_clasif":
                nivelStringSelected = "";
                if ((List<NivelClasificacion>) event.getObject() != null && !((List<NivelClasificacion>) event.getObject()).isEmpty()) {
                    nivelesSelected = (List<NivelClasificacion>) event.getObject();
                    if (nivelesSelected.size() > 1) {
                        nivelStringSelected = "VARIOS";
                    } else {
                        nivelStringSelected = nivelesSelected.get(0).getDescripcion();
                    }
                }
                break;
        }
    }

    // EXPORTA LOS PRODUCTOS QUE HAY EN LA TABLA EN ESE MOMENTO
    public void exportarProductos() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Artículos");

            String[] headers = {"Artículo", "Referencia", "Descripción", "Código de barras", "Id Artículo Depósito", "Fecha de Alta", "Marca", "Familia",
                "Fecha Ult. Modif.", "Lotes", "Caduc.", "Control Seriado", "Estado", "B.V.", "Usuario Último Bloqueo Ventas", "Fecha Último Bloqueo Ventas",
                "B.C.", "Grupo Art.", "Tipo Picking", "Art. Clasificación", "Clasificación", "Con Etiquetas", "Etiq. Portuguesa", "Etiquetado Port.", "Etiq. Italiana",
                "Etiquetado Ita.", "Etiq. Europea", "Propiedad de", "Pndte. Verificar", "Fecha Verificación", "Altura (cm)", "Ancho (cm)", "Largo (cm)", "Peso Neto (gr)",
                "Volumen Neto (cm3)", "Peso Bruto (gr)", "Volumen Bruto (cm3)", "Preventa"};

            XSSFCellStyle estiloCelda = workbook.createCellStyle();
            XSSFColor gris = new XSSFColor(new java.awt.Color(223, 223, 223));
            estiloCelda.setFillForegroundColor(gris);
            estiloCelda.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 1;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(estiloCelda);
            }

            for (Articulo articulo : articulos) {
                Row row = sheet.createRow(rowNum++);

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);

                    switch (i) {
                        case 0:
                            cell.setCellValue(articulo.getIdArticulo());
                            break;
                        case 1:
                            cell.setCellValue(articulo.getReferencia());
                            break;
                        case 2:
                            cell.setCellValue(articulo.getDescripcion());
                            break;
                        case 3:
                            cell.setCellValue(articulo.getCodigoBarras());
                            break;
                        case 4:
                            cell.setCellValue(articulo.getIdArticuloDeposito());
                            break;
                        case 5:;
                            cell.setCellValue(formatDate(articulo.getFechaAlta()));
                            break;
                        case 6:
                            cell.setCellValue(articulo.getIdMarca().getNombre());
                            break;
                        case 7:
                            cell.setCellValue(articulo.getIdFamilia().getNombre());
                            break;
                        case 8:
                            cell.setCellValue(formatDate(articulo.getFechaUltModif()));
                            break;
                        case 9:
                            cell.setCellValue(articulo.isControlLotes() ? "sí" : "no");
                            break;
                        case 10:
                            cell.setCellValue(articulo.isControlCaducidad() ? "sí" : "no");
                            break;
                        case 11:
                            cell.setCellValue(articulo.isControlSeries() ? "sí" : "no");
                            break;
                        case 12:
                            cell.setCellValue(articulo.getEstado());
                            break;
                        case 13:
                            cell.setCellValue(articulo.isBloqueoVentas() ? "sí" : "no");
                            break;
                        case 14:
                            cell.setCellValue(articulo.getUsuarioUltBloqueoVentas().getUsername());
                            break;
                        case 15:
                            cell.setCellValue(formatDate(articulo.getFechaUltBloqueoVentas()));
                            break;
                        case 16:
                            cell.setCellValue(articulo.isBloqueoCompras() ? "sí" : "no");
                            break;
                        case 17:
                            cell.setCellValue(articulo.getArticuloGrupo().getEtiquetaGrupo().getDescripcion());
                            break;
                        case 18:
                            cell.setCellValue(articulo.getIdTipoPicking());
                            break;
                        case 19:
                            cell.setCellValue(articulo.getIdArticuloClasificacion().getDescripcion());
                            break;
                        case 20:
                            cell.setCellValue(articulo.getIdClasificacion());
                            break;
                        case 21:
                            cell.setCellValue(articulo.isEtiqueta() ? "sí" : "no");
                            break;
                        case 22:
                            cell.setCellValue(articulo.isEtiquetaProtuguesa() ? "sí" : "no");
                            break;
                        case 23:
                            cell.setCellValue(articulo.isEtiquetadoPortugues() ? "sí" : "no");
                            break;
                        case 24:
                            cell.setCellValue(articulo.isEtiquetaItaliana() ? "sí" : "no");
                            break;
                        case 25:
                            cell.setCellValue(articulo.isEtiquetadoItaliano() ? "sí" : "no");
                            break;
                        case 26:
                            cell.setCellValue(articulo.isEtiquetaEuropea() ? "sí" : "no");
                            break;
                        case 27:
                            cell.setCellValue(articulo.getIdUsuarioCreacion());
                            break;
                        case 28:
                            cell.setCellValue(articulo.isPendienteVerificar() ? "sí" : "no");
                            break;
                        case 29:
                            cell.setCellValue(articulo.getFechaUltimaVerificacion() != null ? "sí" : "no");
                            break;
                        case 30:
                            cell.setCellValue(articulo.getAlto().doubleValue());
                            break;
                        case 31:
                            cell.setCellValue(articulo.getAncho().doubleValue());
                            break;
                        case 32:
                            cell.setCellValue(articulo.getLargo().doubleValue());
                            break;
                        case 33:
                            cell.setCellValue(articulo.getPesoNeto().doubleValue());
                            break;
                        case 34:
                            cell.setCellValue(articulo.getVolumenNeto().doubleValue());
                            break;
                        case 35:
                            cell.setCellValue(articulo.getPesoBruto().doubleValue());
                            break;
                        case 36:
                            cell.setCellValue(articulo.getVolumen().doubleValue());
                            break;
                        case 37:
                            cell.setCellValue(articulo.isPreventa() ? "sí" : "no");
                            break;
                    }
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            byte[] content = out.toByteArray();

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=productos.xlsx");
            response.setContentLength(content.length);

            response.getOutputStream().write(content);

            FacesContext.getCurrentInstance().responseComplete();

        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    //------------------------------------------------------------------------//
    //                           GETTERS Y SETTERS                            //
    //------------------------------------------------------------------------//
    //
    public LazyArticulosDataModel getLazyItems() {
        return lazyItems;
    }

    public void setLazyItems(LazyArticulosDataModel lazyItems) {
        this.lazyItems = lazyItems;
    }

    public List<Articulo> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<Articulo> articulos) {
        this.articulos = articulos;
    }

    public List<Articulo> getFilteredArticulos() {
        return filteredArticulos;
    }

    public void setFilteredArticulos(List<Articulo> filteredArticulos) {
        this.filteredArticulos = filteredArticulos;
    }

    public String getTerceroStringSelected() {
        return terceroStringSelected;
    }

    public void setTerceroStringSelected(String terceroStringSelected) {
        this.terceroStringSelected = terceroStringSelected;
    }

    public String getFamiliaStringSelected() {
        return familiaStringSelected;
    }

    public void setFamiliaStringSelected(String familiaStringSelected) {
        this.familiaStringSelected = familiaStringSelected;
    }

    public String getMarcaStringSelected() {
        return marcaStringSelected;
    }

    public void setMarcaStringSelected(String marcaStringSelected) {
        this.marcaStringSelected = marcaStringSelected;
    }

    public String getNivelStringSelected() {
        return nivelStringSelected;
    }

    public void setNivelStringSelected(String nivelStringSelected) {
        this.nivelStringSelected = nivelStringSelected;
    }

    public String getUltimaVentana() {
        return ultimaVentana;
    }

    public void setUltimaVentana(String ultimaVentana) {
        this.ultimaVentana = ultimaVentana;
    }

    public List<Tercero> getTercerosSelected() {
        return tercerosSelected;
    }

    public void setTercerosSelected(List<Tercero> tercerosSelected) {
        this.tercerosSelected = tercerosSelected;
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

    public String getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(String idArticulo) {
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

    public String getCodBarras() {
        return codBarras;
    }

    public void setCodBarras(String codBarras) {
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

    public List<Familia> getFamiliasSelected() {
        return familiasSelected;
    }

    public void setFamiliasSelected(List<Familia> familiasSelected) {
        this.familiasSelected = familiasSelected;
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

    public List<Marca> getMarcasSelected() {
        return marcasSelected;
    }

    public void setMarcasSelected(List<Marca> marcasSelected) {
        this.marcasSelected = marcasSelected;
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

    public List<NivelClasificacion> getNivelesSelected() {
        return nivelesSelected;
    }

    public void setNivelesSelected(List<NivelClasificacion> nivelesSelected) {
        this.nivelesSelected = nivelesSelected;
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

    public String getEtiquetaDe() {
        return etiquetaDe;
    }

    public void setEtiquetaDe(String etiquetaDe) {
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

    public String getEtiquetado() {
        return etiquetado;
    }

    public void setEtiquetado(String etiquetado) {
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

    public String getIdStringTerceros() {
        return idStringTerceros;
    }

    public void setIdStringTerceros(String idStringTerceros) {
        this.idStringTerceros = idStringTerceros;
    }

    public String getIdStringFamilias() {
        return idStringFamilias;
    }

    public void setIdStringFamilias(String idStringFamilias) {
        this.idStringFamilias = idStringFamilias;
    }

    public String getIdStringMarcas() {
        return idStringMarcas;
    }

    public void setIdStringMarcas(String idStringMarcas) {
        this.idStringMarcas = idStringMarcas;
    }

    public String getIdStringNiveles() {
        return idStringNiveles;
    }

    public void setIdStringNiveles(String idStringNiveles) {
        this.idStringNiveles = idStringNiveles;
    }
}
