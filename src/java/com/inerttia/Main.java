package com.inerttia;

import com.inerttia.clases.Categoria;
import com.inerttia.clases.Lugar;
import com.inerttia.clases.Producto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Iván Torres
 */
@ManagedBean(name = "Main")
@ViewScoped
public class Main {

    // VARIABLES
    //
    private List<Producto> productos;

    private List<Categoria> categorias;

    Random random = new Random();

    private List<Lugar> lugares;

    private List<Lugar> lugaresSelected;

    private List<Lugar> lugaresSelected2;

    private List<Producto> productosData;

    private List<Producto> chartProductosData;

    private List<Producto> filtroProductos;

    private List<Lugar> filtroLugares;

    private List<Lugar> filtroLugares2;

    private Producto selectedProducto;

    private Producto selectedLazyProducto;

    private List<Producto> selectedProductos;

    private Date rangoInicio;

    private Date rangoFin;

    private int idSelectedCategoria;

    private boolean nuevo = false;

    private boolean show = false;

    private LineChartModel lineModel;

    private DonutChartModel donutModel;

    private BarChartModel barModel;

    private Date minDate;

    private Date minSearchDate;

    private Date today;

    private int selected;

    private boolean filtrado = false;

    private LazyProductosDataModel lazyModel;

    private int activeIndex;

    private UploadedFile archivoSubido;
    private int anteriorCategoria;
    private Date rangoInicioAnterior;
    private Date rangoFinAnterior;

    // INIT
    //
    @PostConstruct
    public void init() {

        // Variables del INIT
        selectedProductos = new ArrayList<>();
        productos = new ArrayList<>();
        filtroProductos = new ArrayList<>();
        categorias = new ArrayList<>();
        lugares = new ArrayList<>();
        lugaresSelected = new ArrayList<>();
        productosData = new ArrayList<>();
        chartProductosData = new ArrayList<>();
        minDate = new Date();
        today = new Date();
        selected = -1;

        // Conjunto de fechas  para usarlas en los objetos de prueba
        String fechaString1 = "12/01/2024", fechaString2 = "20/02/2024", fechaString3 = "25/03/2023", fechaString4 = "01/04/2024",
                fechaString5 = "05/06/2014", fechaString6 = "18/10/2023";

        // Transformación de String a Date, con SimpleDateFormat, de las 6 fechas
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha1 = null;
        try {
            fecha1 = sdf.parse(fechaString1);
        } catch (ParseException ex) {
            System.out.println("Error");
        }
        Date fecha2 = null;
        try {
            fecha2 = sdf.parse(fechaString2);
        } catch (ParseException ex) {
            System.out.println("Error");
        }
        Date fecha3 = null;
        try {
            fecha3 = sdf.parse(fechaString3);
        } catch (ParseException ex) {
            System.out.println("Error");
        }
        Date fecha4 = null;
        try {
            fecha4 = sdf.parse(fechaString4);
        } catch (ParseException ex) {
            System.out.println("Error");
        }
        Date fecha5 = null;
        try {
            fecha5 = sdf.parse(fechaString5);
        } catch (ParseException ex) {
            System.out.println("Error");
        }
        Date fecha6 = null;
        try {
            fecha6 = sdf.parse(fechaString6);
        } catch (ParseException ex) {
            System.out.println("Error");
        }

        // Creación de las categorías para los productos
        categorias.add(new Categoria(1, "Gama alta"));
        categorias.add(new Categoria(2, "Gama media"));
        categorias.add(new Categoria(3, "Gama baja"));

        // Creación de lugares para los productos
        lugares.add(new Lugar(1, "Málaga", "España"));
        lugares.add(new Lugar(2, "Madrid", "España"));
        lugares.add(new Lugar(3, "Donostia", "España"));
        lugares.add(new Lugar(4, "Berlín", "Alemania"));
        lugares.add(new Lugar(5, "Dortmund", "Alemania"));
        lugares.add(new Lugar(6, "Bucarest", "Rumanía"));
        lugares.add(new Lugar(7, "Oradea", "Rumanía"));
        lugares.add(new Lugar(8, "Kyoto", "Japón"));
        lugares.add(new Lugar(9, "Tokyo", "Japón"));
        lugares.add(new Lugar(10, "Osaka", "Japón"));

        // Creación de nuevos productos y adición a la Lista de Productos
        productos.add(new Producto(1, "Televisor Inerttia", "Descripción amplia y muy detallada sobre el Televisor Inerttia lorem ipsum lorem ipsum lorem ipsum",
                categorias.get(1), 1249.99, fecha1, false, new ArrayList<>(), 0));
        productos.add(new Producto(2, "Smartphone Inerttia", "Descripción amplia y muy detallada sobre el Smartphone Inerttia lorem ipsum lorem ipsum lorem ipsum",
                categorias.get(0), 799.99, fecha2, true, new ArrayList<>(), 1));
        productos.add(new Producto(3, "Monitor Inerttia", "Descripción amplia y muy detallada sobre el Monitor Inerttia lorem ipsum lorem ipsum lorem ipsum",
                categorias.get(2), 99.99, fecha3, true, new ArrayList<>(), 2));
        productos.add(new Producto(4, "Tablet Inerttia", "Descripción amplia y muy detallada sobre el Tablet Inerttia lorem ipsum lorem ipsum lorem ipsum",
                categorias.get(0), 599.99, fecha4, false, new ArrayList<>(), 3));
        productos.add(new Producto(5, "Auriculares Inerttia", "Descripción amplia y muy detallada sobre el Auriculares Inerttia lorem ipsum lorem ipsum lorem ipsum",
                categorias.get(1), 89.99, fecha5, false, new ArrayList<>(), 4));
        productos.add(new Producto(6, "Smartwatch Inerttia", "Descripción amplia y muy detallada sobre el Smartwatch Inerttia lorem ipsum lorem ipsum lorem ipsum",
                categorias.get(1), 169.99, fecha6, true, new ArrayList<>(), 5));

        // Añadimos los lugares de forma aleatoria, ya que estamos probando
        for (Producto producto : productos) {

            if (producto.isStock()) {
                int num1 = random.nextInt(10) + 1;

                int[] numeros = new int[num1];
                int count = 0;

                while (count < num1) {
                    // Generación de un número aleatorio
                    int num2 = random.nextInt(10);

                    // Boolean si el número generado ya está en el array
                    boolean repetido = false;
                    for (int i = 0; i < count; i++) {
                        if (numeros[i] == num2) {
                            repetido = true;
                            break;
                        }
                    }

                    // Si no está repetido...
                    if (!repetido) {
                        numeros[count] = num2;
                        count++;
                    }
                }

                for (int i = 0; i < num1; i++) {
                    producto.getLugares().add(lugares.get(numeros[i]));
                }
            }
        }

        lazyModel = new LazyProductosDataModel(productos);

        // Creación de los Chart vacíos, sin campos en la tabla
        createLineModel();
        createDonutModel();
        createBarModel();
    }

    // GETTERS Y SETTERS
    //
    public List<Producto> getProductosData() {
        return productosData;
    }

    public void setProductosData(List<Producto> productosData) {
        this.productosData = productosData;
    }

    public List<Producto> getChartProductosData() {
        return chartProductosData;
    }

    public void setChartProductosData(List<Producto> chartProductosData) {
        this.chartProductosData = chartProductosData;
    }

    public List<Producto> getFiltroProductos() {
        return filtroProductos;
    }

    public void setFiltroProductos(List<Producto> filtroProductos) {
        this.filtroProductos = filtroProductos;
    }

    public List<Lugar> getFiltroLugares() {
        return filtroLugares;
    }

    public void setFiltroLugares(List<Lugar> filtroLugares) {
        this.filtroLugares = filtroLugares;
    }

    public List<Lugar> getFiltroLugares2() {
        return filtroLugares2;
    }

    public void setFiltroLugares2(List<Lugar> filtroLugares2) {
        this.filtroLugares2 = filtroLugares2;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Producto getSelectedProducto() {
        return selectedProducto;
    }

    public void setSelectedProducto(Producto selectedProducto) {
        this.selectedProducto = selectedProducto;
    }

    public List<Producto> getSelectedProductos() {
        return selectedProductos;
    }

    public void setSelectedProductos(List<Producto> selectedProductos) {
        this.selectedProductos = selectedProductos;
    }

    public Date getRangoInicio() {
        return rangoInicio;
    }

    public void setRangoInicio(Date rangoInicio) {
        this.rangoInicio = rangoInicio;
    }

    public Date getRangoFin() {
        return rangoFin;
    }

    public void setRangoFin(Date rangoFin) {
        this.rangoFin = rangoFin;
    }

    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public int getIdSelectedCategoria() {
        return idSelectedCategoria;
    }

    public void setIdSelectedCategoria(int idSelectedCategoria) {
        this.idSelectedCategoria = idSelectedCategoria;
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public DonutChartModel getDonutModel() {
        return donutModel;
    }

    public void setDonutModel(DonutChartModel donutModel) {
        this.donutModel = donutModel;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public List<Lugar> getLugares() {
        return lugares;
    }

    public void setLugares(List<Lugar> lugares) {
        this.lugares = lugares;
    }

    public List<Lugar> getLugaresSelected() {
        return lugaresSelected;
    }

    public void setLugaresSelected(List<Lugar> lugaresSelected) {
        this.lugaresSelected = lugaresSelected;
    }

    public List<Lugar> getLugaresSelected2() {
        return lugaresSelected2;
    }

    public void setLugaresSelected2(List<Lugar> lugaresSelected2) {
        this.lugaresSelected2 = lugaresSelected2;
    }

    public Date getMinSearchDate() {
        return minSearchDate;
    }

    public void setMinSearchDate(Date minSearchDate) {
        this.minSearchDate = minSearchDate;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public boolean isFiltrado() {
        return filtrado;
    }

    public void setFiltrado(boolean filtrado) {
        this.filtrado = filtrado;
    }

    public LazyProductosDataModel getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyProductosDataModel lazyModel) {
        this.lazyModel = lazyModel;
    }

    public Producto getSelectedLazyProducto() {
        return selectedLazyProducto;
    }

    public void setSelectedLazyProducto(Producto selectedLazyProducto) {
        this.selectedLazyProducto = selectedLazyProducto;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public UploadedFile getArchivoSubido() {
        return archivoSubido;
    }

    public void setArchivoSubido(UploadedFile archivoSubido) {
        this.archivoSubido = archivoSubido;
    }

    public int getAnteriorCategoria() {
        return anteriorCategoria;
    }

    public void setAnteriorCategoria(int anteriorCategoria) {
        this.anteriorCategoria = anteriorCategoria;
    }

    public Date getRangoInicioAnterior() {
        return rangoInicioAnterior;
    }

    public void setRangoInicioAnterior(Date rangoInicioAnterior) {
        this.rangoInicioAnterior = rangoInicioAnterior;
    }

    public Date getRangoFinAnterior() {
        return rangoFinAnterior;
    }

    public void setRangoFinAnterior(Date rangoFinAnterior) {
        this.rangoFinAnterior = rangoFinAnterior;
    }

    // ===============================================================================
    // MÉTODOS DEL BEAN
    //
    public String formatDate(Date fecha) {
        if (fecha != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = sdf.format(fecha);
            return fechaFormateada;
        }
        return null;
    }

    public void rellenarTabla() {

        if (this.activeIndex == 0) {
            PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
            PrimeFaces.current().executeScript("PF('dtLugares').clearFilters()");
        }

        this.productosData = new ArrayList<>();
        this.filtroProductos = new ArrayList<>();
        this.chartProductosData = new ArrayList<>();

        for (Producto prod : productos) {
            this.productosData.add(prod);
        }

        if (this.idSelectedCategoria != -1) {
            for (Producto prod : productos) {
                if (prod.getCategoria().getId() == this.idSelectedCategoria) {
                    if (this.activeIndex == 0) {
                        this.filtroProductos.add(prod);
                    } else {
                        this.chartProductosData.add(prod);
                    }
                }
            }
        } else {
            for (Producto prod : productos) {
                if (this.activeIndex == 0) {
                    this.filtroProductos.add(prod);
                } else {
                    this.chartProductosData.add(prod);
                }
            }
        }

        if (this.rangoInicio != null && this.rangoFin != null) {
            if (this.activeIndex == 0) {
                this.productosData = this.productosData.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
                        || producto.getFechaLanz().equals(this.rangoFin)
                        || (producto.getFechaLanz().after(this.rangoInicio) && producto.getFechaLanz().before(this.rangoFin)))
                        .collect(Collectors.toList());
            } else {
                this.chartProductosData = this.chartProductosData.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
                        || producto.getFechaLanz().equals(this.rangoFin)
                        || (producto.getFechaLanz().after(this.rangoInicio) && producto.getFechaLanz().before(this.rangoFin)))
                        .collect(Collectors.toList());
            }

        } else if (this.rangoInicio != null) {
            if (this.activeIndex == 0) {
                this.productosData = this.productosData.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
                        || (producto.getFechaLanz().after(this.rangoInicio)))
                        .collect(Collectors.toList());
            } else {
                this.chartProductosData = this.chartProductosData.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
                        || (producto.getFechaLanz().after(this.rangoInicio)))
                        .collect(Collectors.toList());
            }

        } else if (this.rangoFin != null) {
            if (this.activeIndex == 0) {
                this.productosData = this.productosData.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
                        || (producto.getFechaLanz().before(this.rangoFin)))
                        .collect(Collectors.toList());
            } else {
                this.chartProductosData = this.chartProductosData.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
                        || (producto.getFechaLanz().before(this.rangoFin)))
                        .collect(Collectors.toList());
            }
        }

        if (this.activeIndex == 0) {
            productosData.sort(Comparator.comparing(Producto::getPosicion));

            lazyModel = new LazyProductosDataModel(productosData);
            lazyModel.setRowCount(productosData.size());
        } else {
            // Creación de los Chart
            createLineModel();
            createDonutModel();
            createBarModel();
        }

        this.rangoInicioAnterior = this.rangoInicio;
        this.rangoFinAnterior = this.rangoFin;
        this.anteriorCategoria = this.idSelectedCategoria;
    }

    public boolean hasSelectedProductos() {
        return this.selectedProductos != null && !this.selectedProductos.isEmpty();
    }

    public void openNew() {
        this.idSelectedCategoria = 1;
        editProduct(new Producto(), false, true);
    }

    public void editProduct(Producto selectedProducto, boolean justShow, boolean isNew) {
        this.selectedProducto = selectedProducto;

        if (isNew == false) {
            this.idSelectedCategoria = selectedProducto.getCategoria().getId();
        }
        nuevo = isNew;
        show = justShow;

        if (this.activeIndex == 1) {
            // Creación de nuevo de los Chart
            createLineModel();
            createDonutModel();
            createBarModel();
        }
    }

    public String validation(Producto producto) {
        if (producto.getNombre().isEmpty() || producto.getDescripcion().length() < 10) {
            if (producto.getNombre().isEmpty() && producto.getDescripcion().length() < 10) {
                return "ambos";
            } else if (producto.getDescripcion().length() >= 10) {
                return "nombre";
            } else {
                return "descripcion";
            }
        } else {
            return "correcto";
        }
    }

    public void saveProduct() {

        switch (this.idSelectedCategoria) {
            case 1:
                this.selectedProducto.setCategoria(this.categorias.get(0));
                break;
            case 2:
                this.selectedProducto.setCategoria(this.categorias.get(1));
                break;
            case 3:
                this.selectedProducto.setCategoria(this.categorias.get(2));
                break;
        }

        if (nuevo) {
            boolean exists = false;
            for (Producto productoBase : productos) {
                if (productoBase.getCodigo() == selectedProducto.getCodigo()) {
                    exists = true;
                }
            }
            if (!productosData.isEmpty()) {
                for (Producto productoData : productosData) {
                    if (productoData.getCodigo() == selectedProducto.getCodigo()) {
                        exists = true;
                    }
                }
            }

            if (!exists) {
                switch (validation(this.selectedProducto)) {
                    case "correcto":
                        this.productos.add(this.selectedProducto);
                        this.productosData.add(this.selectedProducto);
                        if (this.activeIndex == 0) {
                            this.filtroProductos.add(this.selectedProducto);
                        } else if (this.activeIndex == 1) {
                            this.chartProductosData.add(this.selectedProducto);
                        }
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Añadido Correctamente", ""));
                        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
                        break;
                    case "ambos":
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre no debe estar vacío", ""));
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La descripción debe tener más de 10 caracteres", ""));
                        break;
                    case "nombre":
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre no debe estar vacío", ""));
                        break;
                    default:
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La descripción debe tener más de 10 caracteres", ""));
                        break;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El ID seleccionado ya existe", ""));
                switch (validation(this.selectedProducto)) {
                    case "ambos":
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre no debe estar vacío", ""));
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La descripción debe tener más de 10 caracteres", ""));
                        break;
                    case "nombre":
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre no debe estar vacío", ""));
                        break;
                    case "descripcion":
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La descripción debe tener más de 10 caracteres", ""));
                        break;
                }
                exists = false;
            }
        } else {
            switch (validation(this.selectedProducto)) {
                case "correcto":
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Actualizado", ""));
                    PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
                    break;
                case "ambos":
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre no debe estar vacío", ""));
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La descripción debe tener más de 10 caracteres", ""));
                    break;
                case "nombre":
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre no debe estar vacío", ""));
                    break;
                case "descripcion":
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La descripción debe tener más de 10 caracteres", ""));
                    break;
            }
        }

        if (this.activeIndex == 1) {
            // Creación de nuevo del Chart
            createLineModel();
            createDonutModel();
            createBarModel();
        }
    }

    public String getDeleteButtonMessage() {
        if (hasSelectedProductos()) {
            int size = this.selectedProductos.size();
            return size > 1 ? size + " products selected" : "1 product selected";
        }

        return null;
    }

    public void eliminarProducto(Producto p) {
        this.filtroProductos.remove(p);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Producto Eliminado", ""));

        if (this.activeIndex == 1) {
            // Creación de nuevo del Chart
            createLineModel();
            createDonutModel();
            createBarModel();
        }
    }

    public void eliminarSelectedProductos() {
        this.productosData.removeAll(this.selectedProductos);
        this.selectedProductos = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Productos eliminados"));

        if (this.activeIndex == 1) {
            // Creación de nuevo del Chart
            createLineModel();
            createDonutModel();
            createBarModel();
        }
    }

    public void createLineModel() {

        lineModel = new LineChartModel();
        ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> precios = new ArrayList<>();

        if (chartProductosData != null && !chartProductosData.isEmpty()) {

            for (Producto producto : chartProductosData) {
                precios.add(producto.getPrecio());
            }

        }

        dataSet.setData(precios);
        dataSet.setFill(false);
        dataSet.setLabel("Chart de Precios (productos)");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setTension(0.1);
        data.addChartDataSet(dataSet);

        List<String> labels = new ArrayList<>();

        if (chartProductosData != null && !chartProductosData.isEmpty()) {

            for (Producto producto : chartProductosData) {
                labels.add(producto.getNombre());
            }

        }

        data.setLabels(labels);

        //Opciones
        LineChartOptions opciones = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Precios");
        opciones.setTitle(title);

        lineModel.setOptions(opciones);
        lineModel.setData(data);

    }

    public void createDonutModel() {
        donutModel = new DonutChartModel();
        ChartData data = new ChartData();

        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> precios = new ArrayList<>();

        if (chartProductosData != null && !chartProductosData.isEmpty()) {

            for (Producto producto : chartProductosData) {
                precios.add(producto.getPrecio());
            }

        }
        dataSet.setData(precios);

        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        bgColors.add("rgb(255, 205, 86)");
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<>();

        if (chartProductosData != null && !chartProductosData.isEmpty()) {

            for (Producto producto : chartProductosData) {
                labels.add(producto.getNombre());
            }

        }
        data.setLabels(labels);

        donutModel.setData(data);
    }

    public void createBarModel() {
        barModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Precios");

        List<Number> precios = new ArrayList<>();

        if (chartProductosData != null && !chartProductosData.isEmpty()) {

            for (Producto producto : chartProductosData) {
                precios.add(producto.getPrecio());
            }

        }
        barDataSet.setData(precios);

        List<String> bgColor = new ArrayList<>();
        bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");
        barDataSet.setBackgroundColor(bgColor);

        List<String> borderColor = new ArrayList<>();
        borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();

        if (chartProductosData != null && !chartProductosData.isEmpty()) {

            for (Producto producto : chartProductosData) {
                labels.add(producto.getNombre());
            }

        }
        data.setLabels(labels);
        barModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        linearAxes.setBeginAtZero(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("italic");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        barModel.setOptions(options);
    }

    public String exportStock(boolean stock) {
        return stock ? "sí" : "no";
    }

    public void exportarProductos() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Productos");
            Row headerRow = sheet.createRow(0);

            String[] headers = {"Código", "Nombre", "Descripción", "Categoría", "Precio", "Fecha de Lanzamiento", "Stock"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Producto producto : this.productosData) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(producto.getCodigo());
                row.createCell(1).setCellValue(producto.getNombre());
                row.createCell(2).setCellValue(producto.getDescripcion());
                row.createCell(3).setCellValue(producto.getCategoria().getNombre());
                row.createCell(4).setCellValue(producto.getPrecio());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fechaFormat = sdf.format(producto.getFechaLanz());
                row.createCell(5).setCellValue(fechaFormat);

                String stock = (producto.isStock() ? "sí" : "no");
                row.createCell(6).setCellValue(stock);
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

    public void gestionTablaLugares() {

        if (!selectedProducto.isStock()) {
            this.lugaresSelected = new ArrayList<>();
            this.selected = -1;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay stock", ""));
        } else {
            if (!lugaresSelected.isEmpty()) {
                this.lugaresSelected = new ArrayList<>();
                if (this.selected == this.selectedProducto.getCodigo()) {
                    this.selected = -1;
                    this.selectedProducto = null;
                    PrimeFaces.current().executeScript("PF('dtLugares').clearFilters();");
                } else {
                    this.lugaresSelected.addAll(selectedProducto.getLugares());
                    this.selected = this.selectedProducto.getCodigo();
                    PrimeFaces.current().executeScript("PF('dtLugares').clearFilters();");
                }

            } else {
                this.lugaresSelected = new ArrayList<>();
                this.lugaresSelected.addAll(selectedProducto.getLugares());
                this.selected = this.selectedProducto.getCodigo();
            }
        }
    }

    public Date ajustarMinDate(int tipo) {
        switch (tipo) {
            case 0:
                if (this.rangoFin != null) {
                    return this.rangoFin;
                } else {
                    return null;
                }
            case 1:
                if (this.rangoInicio != null) {
                    return this.rangoInicio;
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    public void onRowSelect(SelectEvent<Producto> event) {
        FacesMessage msg = new FacesMessage("Producto seleccionado", String.valueOf(event.getObject().getCodigo()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowReorder(ReorderEvent event) {

        boolean noReorder = false;

        if (filtroProductos.size() != productosData.size()) {
            filtroProductos.sort(Comparator.comparing(Producto::getPosicion));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No se pueden reordenar las filas cuando se está filtrando", ""));
            noReorder = true;
        }

        if (!noReorder) {
            for (Producto producto : productosData) {
                if (producto.getPosicion() == event.getFromIndex()) {
                    producto.setPosicion(event.getToIndex());
                } else {
                    if (event.getToIndex() < event.getFromIndex()) {
                        if (producto.getPosicion() >= event.getToIndex() && producto.getPosicion() < event.getFromIndex()) {
                            producto.setPosicion(producto.getPosicion() + 1);
                        }
                    } else {
                        if (producto.getPosicion() <= event.getToIndex() && producto.getPosicion() != 0
                                && producto.getPosicion() > event.getFromIndex()) {
                            producto.setPosicion(producto.getPosicion() - 1);
                        }
                    }
                }
            }

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Posición de " + productosData.get(event.getFromIndex()).getNombre().toUpperCase() + " cambiada.", "Desde: " + (event.getFromIndex() + 1) + ", Hasta:" + (event.getToIndex() + 1));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public void fileUpload(FileUploadEvent event) {
        this.archivoSubido = null;
        UploadedFile file = event.getFile();

        if (file != null && file.getFileName() != null) {
            try (InputStream inputStream = file.getInputStream()) {
                boolean repetido = false;
                boolean posicion = true;
                boolean stock = true;
                boolean noValido = false;

                Workbook workbook;
                if (file.getFileName().toLowerCase().endsWith(".xlsx")) {
                    workbook = new XSSFWorkbook(inputStream);
                } else if (file.getFileName().toLowerCase().endsWith(".xls")) {
                    workbook = new HSSFWorkbook(inputStream);
                } else {
                    throw new IllegalArgumentException("Tipo de archivo no soportado: " + file.getFileName());
                }

                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();
                List<Producto> nuevosProductos = new ArrayList<>();

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (row.getRowNum() == 0) {
                        continue;
                    }

                    for (int i = 0; i < 7; i++) {
                        if (row.getCell(i) == null || getStringValue(row.getCell(i)).equals("")) {
                            switch (i) {
                                case 0:
                                    posicion = false;
                                    break;
                                case 1:
                                    FacesMessage errorMessage2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El producto número " + (i + 1)
                                            + " de tu archivo Excel necesita de un Código identificador");
                                    FacesContext.getCurrentInstance().addMessage(null, errorMessage2);
                                    noValido = true;
                                    break;
                                case 2:
                                    FacesMessage errorMessage3 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El producto número " + (i + 1)
                                            + " de tu archivo Excel necesita de un Nombre");
                                    FacesContext.getCurrentInstance().addMessage(null, errorMessage3);
                                    noValido = true;
                                    break;
                                case 4:
                                    FacesMessage errorMessage4 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El producto número " + (i + 1)
                                            + " de tu archivo Excel necesita de una Categoría");
                                    FacesContext.getCurrentInstance().addMessage(null, errorMessage4);
                                    noValido = true;
                                    break;
                                case 5:
                                    FacesMessage errorMessage5 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El producto número " + (i + 1)
                                            + " de tu archivo Excel necesita de un Precio");
                                    FacesContext.getCurrentInstance().addMessage(null, errorMessage5);
                                    noValido = true;
                                    break;
                                case 7:
                                    stock = false;
                                    break;
                            }
                        }
                    }

                    if (!noValido) {

                        if (!chartProductosData.isEmpty()) {
                            for (Producto chartProd : chartProductosData) {
                                if (chartProd.getCodigo() == Integer.parseInt(getStringValue(row.getCell(1)).substring(0, getStringValue(row.getCell(1)).indexOf(".")))) {
                                    repetido = true;
                                    FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El producto " + chartProd.getNombre()
                                            + " de tu lista ya tiene el código número " + chartProd.getCodigo() + ". Coincide con el código del producto "
                                            + getStringValue(row.getCell(2)) + " del archivo Excel subido");
                                    FacesContext.getCurrentInstance().addMessage(null, errorMessage);
                                }
                            }
                        } else if (!filtroProductos.isEmpty()) {
                            for (Producto productoExistente : filtroProductos) {
                                if (productoExistente.getCodigo() == Integer.parseInt(getStringValue(row.getCell(1)).substring(0, getStringValue(row.getCell(1)).indexOf(".")))) {
                                    repetido = true;
                                    FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El producto " + productoExistente.getNombre()
                                            + " de tu lista ya tiene el código número " + productoExistente.getCodigo() + ". Coincide con el código del producto "
                                            + getStringValue(row.getCell(2)) + " del archivo Excel subido");
                                    FacesContext.getCurrentInstance().addMessage(null, errorMessage);
                                }
                            }
                        }
                    }

                    if (!repetido && !noValido) {
                        Producto producto = new Producto();

                        if (posicion == false) {
                            producto.setPosicion(productos.get(productos.size() - 1).getPosicion());
                        } else {
                            producto.setPosicion(Integer.parseInt(getStringValue(row.getCell(0)).substring(0, getStringValue(row.getCell(0)).indexOf("."))));
                        }

                        producto.setCodigo(Integer.parseInt(getStringValue(row.getCell(1)).substring(0, getStringValue(row.getCell(1)).indexOf("."))));
                        producto.setNombre(getStringValue(row.getCell(2)));
                        producto.setDescripcion(getStringValue(row.getCell(3)));

                        for (Categoria categoria : this.categorias) {
                            if (getStringValue(row.getCell(4)).equals(categoria.getNombre())) {
                                producto.setCategoria(categoria);
                                break;
                            }
                        }
                        producto.setPrecio(Double.parseDouble(getStringValue(row.getCell(5))));

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date fecha = sdf.parse(getStringValue(row.getCell(6)));
                            producto.setFechaLanz(fecha);
                        } catch (ParseException ex) {
                            System.out.println("Error");
                        }

                        if (stock == false) {
                            producto.setStock(false);
                        } else {
                            if (getStringValue(row.getCell(7)).equals("si")) {
                                producto.setStock(true);
                            } else {
                                producto.setStock(false);
                            }
                        }

                        producto.setLugares(new ArrayList<>());

                        nuevosProductos.add(producto);
                    }
                }

                if (!nuevosProductos.isEmpty()) {
                    List<String> prods = new ArrayList<>();
                    for (Producto nuevoProd : nuevosProductos) {
                        if (!this.filtroProductos.isEmpty() || !this.chartProductosData.isEmpty()) {
                            boolean enFecha = true;

                            if (this.rangoInicioAnterior != null && this.rangoFin == null) {
                                enFecha = nuevoProd.getFechaLanz().compareTo(this.rangoInicioAnterior) >= 0;
                            } else if (this.rangoInicioAnterior == null && this.rangoFinAnterior != null) {
                                enFecha = nuevoProd.getFechaLanz().compareTo(this.rangoFinAnterior) <= 0;
                            } else if (this.rangoInicioAnterior != null && this.rangoFinAnterior != null) {
                                enFecha = (nuevoProd.getFechaLanz().compareTo(this.rangoInicioAnterior) >= 0) && (nuevoProd.getFechaLanz().compareTo(this.rangoFinAnterior) <= 0);
                            }

                            if (this.anteriorCategoria == 1 && nuevoProd.getCategoria().getId() == 1 && enFecha) {
                                if (!this.filtroProductos.isEmpty()) {
                                    this.filtroProductos.add(nuevoProd);
                                }

                                if (!this.chartProductosData.isEmpty()) {
                                    this.chartProductosData.add(nuevoProd);
                                }
                            } else if (this.anteriorCategoria == 2 && nuevoProd.getCategoria().getId() == 2 && enFecha) {
                                if (!this.filtroProductos.isEmpty()) {
                                    this.filtroProductos.add(nuevoProd);
                                }

                                if (!this.chartProductosData.isEmpty()) {
                                    this.chartProductosData.add(nuevoProd);
                                }
                            } else if (this.anteriorCategoria == 3 && nuevoProd.getCategoria().getId() == 3 && enFecha) {
                                if (!this.filtroProductos.isEmpty()) {
                                    this.filtroProductos.add(nuevoProd);
                                }

                                if (!this.chartProductosData.isEmpty()) {
                                    this.chartProductosData.add(nuevoProd);
                                }
                            } else if (this.anteriorCategoria == -1 && enFecha) {
                                if (!this.filtroProductos.isEmpty()) {
                                    this.filtroProductos.add(nuevoProd);
                                }

                                if (!this.chartProductosData.isEmpty()) {
                                    this.chartProductosData.add(nuevoProd);
                                }
                            }
                        }

                        this.productosData.add(nuevoProd);
                        prods.add(nuevoProd.getNombre());
                    }

                    productosData.sort(Comparator.comparing(Producto::getPosicion));
                    filtroProductos.sort(Comparator.comparing(Producto::getPosicion));
                    if (!this.chartProductosData.isEmpty()) {
                        // Creación de nuevo de los Chart
                        chartProductosData.sort(Comparator.comparing(Producto::getPosicion));
                        createLineModel();
                        createDonutModel();
                        createBarModel();
                    }

                    FacesMessage msg = new FacesMessage("Hecho", "Productos añadidos: " + prods.toString());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }

            } catch (IOException | NumberFormatException e) {
                FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Hubo un error al procesar el archivo subido");
                FacesContext.getCurrentInstance().addMessage(null, errorMessage);
            } catch (IllegalArgumentException e) {
                FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, errorMessage);
            }
        }
        this.archivoSubido = null;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (null == cell.getCellType()) {
            return null;
        } else {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        return sdf.format(cell.getDateCellValue());
                    } else {
                        return String.valueOf(cell.getNumericCellValue());
                    }
                default:
                    return null;
            }
        }
    }
}
