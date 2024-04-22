package com.inerttia;

import com.inerttia.clases.Categoria;
import com.inerttia.clases.Lugar;
import com.inerttia.clases.Producto;
import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import es.inerttia.ittwsEntidades.params.Metodos;
import es.inerttia.ittwsEntidades.rest.Peticion;
import es.inerttia.ittwsEntidades.wsAlmacen.PaletsRespuesta;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.component.export.PDFOptions;
import org.primefaces.component.export.PDFOrientationType;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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

    //------------------------------------------------------------------------//
    //                               VARIABLES                                //
    //------------------------------------------------------------------------//
    //
    // PRODUCTO
    private Producto selectedProducto;
    private Producto selectedLazyProducto;

    // CATEGORIA
    private int idSelectedCategoria; // ESTABLECIDO EN EL DIALOG DEL CRUD
    private int selected; // 
    private int anteriorCategoria; // eliminar

    // FECHAS
    private Date today;
    private Date rangoInicio;
    private Date rangoFin;
    private Date minDate;
    private Date rangoInicioAnterior;
    private Date rangoFinAnterior;

    // CHARTS
    private LineChartModel lineModel;
    private DonutChartModel donutModel;
    private BarChartModel barModel;

    // LISTAS PRODUCTOS
    private List<Producto> productos;
    private List<Producto> productosData;
    private List<Producto> filtroProductos;
    private List<Producto> chartProductosData;
    private List<Producto> selectedProductos;

    // LISTAS LUGARES
    private List<Lugar> lugares;
    private List<Lugar> lugaresSelected;
    private List<Lugar> lugaresSelected2;
    private List<Lugar> filtroLugares;
    private List<Lugar> filtroLugares2;

    // LISTAS CATEGORÍAS
    private List<Categoria> categorias;
    private List<Integer> categoriasSearch;

    // LAZY
    private LazyProductosDataModel lazyModel;

    // FILTROS
    private String filtroNombre;
    private String descripcion;
    private String fechaLanz;

    // BOOLEANOS DE COMPROBACIÓN
    private boolean nuevo = false;
    private boolean show = false;
    private boolean filtrado = false;

    // ÚTILES
    private PDFOptions pdfOpt;
    Random random = new Random();
    private UploadedFile archivoSubido;
    private int activeIndex;

    private PaletsRespuesta llamada1;

    //------------------------------------------------------------------------//
    //                                  INIT                                  //
    //------------------------------------------------------------------------//
    //
    @PostConstruct
    public void init() {

        // VARIABLES DEL INIT
        OkHttpClient datos = new OkHttpClient();
        String url = "http://172.26.100.112:8080/ittws3/webresources/post";

        String jsonBody = "{\n"
                + "    \"almacen\": null,\n"
                + "    \"centro\": null,\n"
                + "    \"empresa\": null,\n"
                + "    \"listaparametros\": [],\n"
                + "    \"metodo\": \"getPalet\",\n"
                + "    \"parametro1\": \"112233445566778899\",\n"
                + "    \"parametro2\": \"1\",\n"
                + "    \"parametro3\": \"1\",\n"
                + "    \"parametro4\": \"\",\n"
                + "    \"parametro5\": \"\",\n"
                + "    \"parametro6\": \"\",\n"
                + "    \"parametro7\": \"\",\n"
                + "    \"parametros\": \"\",\n"
                + "    \"password\": \"admin\",\n"
                + "    \"tracking\": \"\",\n"
                + "    \"usuario\": \"admin\",\n"
                + "    \"version\": \"1.1.2.37\"\n"
                + "}";

        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        
        try {
            // Ejecuta la solicitud POST
            Response response = datos.newCall(request).execute();

            // Obtiene la respuesta
            String responseData = response.body().string();

            // Haz algo con la respuesta
            System.out.println(responseData);

        } catch (IOException e) {
        }
        
        
        selected = -1;
        today = new Date();
        minDate = new Date();

        productos = new ArrayList<>();
        productosData = new ArrayList<>();
        filtroProductos = new ArrayList<>();
        chartProductosData = new ArrayList<>();
        selectedProductos = new ArrayList<>();

        lugares = new ArrayList<>();
        lugaresSelected = new ArrayList<>();

        categorias = new ArrayList<>();

        pdfOpt = new PDFOptions();
        pdfOpt.setOrientation(PDFOrientationType.LANDSCAPE);
        pdfOpt.setFontName("Arial Unicode MS");

        //------------------------------------------------------------------------//
        //                     CREACIÓN DE PRODUCTOS DE PRUEBA                    //
        //------------------------------------------------------------------------//
        //
        // FECHAS PARA PRODUCTOS DE PRUEBA
        String fechaString1 = "12/01/2024", fechaString2 = "20/02/2024", fechaString3 = "25/03/2023", fechaString4 = "01/04/2024",
                fechaString5 = "05/06/2014", fechaString6 = "18/10/2023";

        // TRANSFORMACIÓN DE STRING A DATE, USANDO SimpleDateFormat
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

        // CREACIÓN DE CATEGORÍAS
        categorias.add(new Categoria(1, "Gama alta"));
        categorias.add(new Categoria(2, "Gama media"));
        categorias.add(new Categoria(3, "Gama baja"));

        // CREACIÓN DE LUGARES
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

        // CREACIÓN DE PRODUCTOS
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

        // AÑADIMOS LUGARES DE FORMA ALEATORIA A LOS PRODUCTOS
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

        // INICIALIZACIÓN DE LAZYMODEL
        lazyModel = new LazyProductosDataModel(productos);

        // CREACIÓN DE CHARTS (VACÍOS)
        createLineModel();
        createDonutModel();
        createBarModel();
    }

    //------------------------------------------------------------------------//
    //                           GETTERS Y SETTERS                            //
    //------------------------------------------------------------------------//
    //
    public Producto getSelectedProducto() {
        return selectedProducto;
    }

    public void setSelectedProducto(Producto selectedProducto) {
        this.selectedProducto = selectedProducto;
    }

    public Producto getSelectedLazyProducto() {
        return selectedLazyProducto;
    }

    public void setSelectedLazyProducto(Producto selectedLazyProducto) {
        this.selectedLazyProducto = selectedLazyProducto;
    }

    public int getIdSelectedCategoria() {
        return idSelectedCategoria;
    }

    public void setIdSelectedCategoria(int idSelectedCategoria) {
        this.idSelectedCategoria = idSelectedCategoria;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getAnteriorCategoria() {
        return anteriorCategoria;
    }

    public void setAnteriorCategoria(int anteriorCategoria) {
        this.anteriorCategoria = anteriorCategoria;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
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

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
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

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductosData() {
        return productosData;
    }

    public void setProductosData(List<Producto> productosData) {
        this.productosData = productosData;
    }

    public List<Producto> getFiltroProductos() {
        return filtroProductos;
    }

    public void setFiltroProductos(List<Producto> filtroProductos) {
        this.filtroProductos = filtroProductos;
    }

    public List<Producto> getChartProductosData() {
        return chartProductosData;
    }

    public void setChartProductosData(List<Producto> chartProductosData) {
        this.chartProductosData = chartProductosData;
    }

    public List<Producto> getSelectedProductos() {
        return selectedProductos;
    }

    public void setSelectedProductos(List<Producto> selectedProductos) {
        this.selectedProductos = selectedProductos;
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

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public List<Integer> getCategoriasSearch() {
        return categoriasSearch;
    }

    public void setCategoriasSearch(List<Integer> categoriasSearch) {
        this.categoriasSearch = categoriasSearch;
    }

    public LazyProductosDataModel getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyProductosDataModel lazyModel) {
        this.lazyModel = lazyModel;
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripción) {
        this.descripcion = descripcion;
    }

    public String getFechaLanz() {
        return fechaLanz;
    }

    public void setFechaLanz(String fechaLanz) {
        this.fechaLanz = fechaLanz;
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

    public boolean isFiltrado() {
        return filtrado;
    }

    public void setFiltrado(boolean filtrado) {
        this.filtrado = filtrado;
    }

    public void setPdfOpt(PDFOptions pdfOpt) {
        this.pdfOpt = pdfOpt;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public UploadedFile getArchivoSubido() {
        return archivoSubido;
    }

    public void setArchivoSubido(UploadedFile archivoSubido) {
        this.archivoSubido = archivoSubido;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public PaletsRespuesta getLlamada1() {
        return llamada1;
    }

    public void setLlamada1(PaletsRespuesta llamada1) {
        this.llamada1 = llamada1;
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

    // MUESTRA UNA TABLA DE PRODUCTOS (CRUD)
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

        for (Producto prod : productos) {
            if (this.categoriasSearch.contains(prod.getCategoria().getId())) {
                if (this.activeIndex == 0) {
                    this.filtroProductos.add(prod);
                } else {
                    this.chartProductosData.add(prod);
                }
            } else if (this.categoriasSearch.isEmpty()) {
                if (this.activeIndex == 0) {
                    this.filtroProductos.add(prod);
                } else {
                    this.chartProductosData.add(prod);
                }
            }
        }

        if (this.rangoInicio != null && this.rangoFin != null) {
            if (this.activeIndex == 0) {
                this.filtroProductos = this.filtroProductos.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
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
                this.filtroProductos = this.filtroProductos.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
                        || (producto.getFechaLanz().after(this.rangoInicio)))
                        .collect(Collectors.toList());
            } else {
                this.chartProductosData = this.chartProductosData.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoInicio)
                        || (producto.getFechaLanz().after(this.rangoInicio)))
                        .collect(Collectors.toList());
            }

        } else if (this.rangoFin != null) {
            if (this.activeIndex == 0) {
                this.filtroProductos = this.filtroProductos.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoFin)
                        || (producto.getFechaLanz().before(this.rangoFin)))
                        .collect(Collectors.toList());
            } else {
                this.chartProductosData = this.chartProductosData.stream().filter(producto -> producto.getFechaLanz().equals(this.rangoFin)
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
    }

    // COMPRUEBA SI HAY PRODUCTOS SELECCIONADOS CON CHECKBOX
    public boolean hasSelectedProductos() {
        return this.selectedProductos != null && !this.selectedProductos.isEmpty();
    }

    // SE INICIA CON LA CREACIÓN DE UN NUEVO PRODUCTO (CRUD)
    public void openNew() {
        this.idSelectedCategoria = 1;
        editProduct(new Producto(), false, true);
    }

    // SE INICIA CON LA EDICIÓN DE UN PRODUCTO EXISTENTE (CRUD)
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

    // VALIDA LOS CAMPOS EN LA CREACIÓN / EDICIÓN DE UN PRODUCTO
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

    // GUARDA UN PRODUCTO CREADO / EDITADO SI ESTÁ TODO CORRECTO
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

                            if (this.categoriasSearch.contains(selectedProducto.getCategoria().getId())) {
                                this.filtroProductos.add(this.selectedProducto);
                            } else if (this.categoriasSearch.isEmpty()) {
                                this.filtroProductos.add(this.selectedProducto);
                            }

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
            }
        } else {
            switch (validation(this.selectedProducto)) {
                case "correcto":
                    rellenarTabla();
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

    // COMPLETA UN MENSAJE POR PANTALLA
    public String getDeleteButtonMessage() {
        if (hasSelectedProductos()) {
            int size = this.selectedProductos.size();
            return size > 1 ? size + " products selected" : "1 product selected";
        }

        return null;
    }

    // ELIMINA UN PRODUCTO (CRUD)
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

    // ELIMINA MÚLTIPLES PRODUCTOS SELECCIONADOS CON CHECKBOX (CRUD)
    public void eliminarSelectedProductos() {
        this.productosData.removeAll(this.selectedProductos);
        this.selectedProductos = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Productos eliminados"));

        if (this.activeIndex == 1) {
            // Creación de nuevo de los Chart
            createLineModel();
            createDonutModel();
            createBarModel();
        }
    }

    // CREACIÓN DEL LINE CHART
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

    // CREACIÓN DEL DONUT CHART
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

    // CREACIÓN DEL BAR CHART
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

    // FORMATEA EL STOCK CAMBIANDO TRUE POR "SÍ" Y FALSE POR "NO"
    public String exportStock(boolean stock) {
        return stock ? "sí" : "no";
    }

    // EXPORTA LOS PRODUCTOS QUE HAY EN LA TABLA EN ESE MOMENTO
    public void exportarProductos() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Productos");

            String[] headers = {"Código", "Nombre", "Descripción", "Categoría", "Precio", "Fecha de Lanzamiento", "Stock", "Posición"};

            Font fontBlanco = workbook.createFont();
            fontBlanco.setColor(IndexedColors.WHITE.getIndex());

            XSSFCellStyle estiloCelda = workbook.createCellStyle();
            XSSFColor gris = new XSSFColor(new java.awt.Color(223, 223, 223));
            estiloCelda.setFillForegroundColor(gris);
            estiloCelda.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFCellStyle estiloFilaNoStock = workbook.createCellStyle();
            XSSFColor rojo = new XSSFColor(new java.awt.Color(255, 0, 56));
            estiloFilaNoStock.setFillForegroundColor(rojo);
            estiloFilaNoStock.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloFilaNoStock.setFont(fontBlanco);

            XSSFCellStyle estiloFilaPrecio = workbook.createCellStyle();
            XSSFColor verde = new XSSFColor(new java.awt.Color(17, 109, 7));
            estiloFilaPrecio.setFillForegroundColor(verde);
            estiloFilaPrecio.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloFilaPrecio.setFont(fontBlanco);

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');
            DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

            int rowNum = 1;
            int startingRow = 0;

            if (productosData.size() != filtroProductos.size()) {

                Row filtersRow = sheet.createRow(0);
                List<String> filters = new ArrayList<>();

                if (this.categoriasSearch.size() != this.categorias.size() && !this.categoriasSearch.isEmpty()) {

                    for (int cat : this.categoriasSearch) {
                        filters.add("Categoría: " + categorias.get(cat).getNombre());
                    }
                }
                if (rangoInicio != null && rangoFin != null) {
                    filters.add("Fecha inicio: " + formatDate(rangoInicio) + " Fecha fin: " + formatDate(rangoFin));
                } else if (rangoInicio != null) {
                    filters.add("Fecha inicio: " + formatDate(rangoInicio));
                } else if (rangoFin != null) {
                    filters.add("Fecha fin: " + formatDate(rangoFin));
                }

                for (int i = 0; i < filters.size(); i++) {
                    Cell cell = filtersRow.createCell(i);
                    cell.setCellValue(filters.get(i));
                }

                Row headerRow = sheet.createRow(2);

                rowNum = 3;
                startingRow = 2;

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(estiloCelda);
                }

            } else {
                Row headerRow = sheet.createRow(0);

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(estiloCelda);
                }
            }

            for (Producto producto : this.filtroProductos) {
                Row row = sheet.createRow(rowNum++);

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);

                    if (!producto.isStock()) {
                        cell.setCellStyle(estiloFilaNoStock);
                    } else if (producto.getPrecio() > 100) {
                        cell.setCellStyle(estiloFilaPrecio);
                    }

                    switch (i) {
                        case 0:
                            cell.setCellValue(producto.getCodigo());
                            break;
                        case 1:
                            cell.setCellValue(producto.getNombre());
                            break;
                        case 2:
                            cell.setCellValue(producto.getDescripcion());
                            break;
                        case 3:
                            cell.setCellValue(producto.getCategoria().getNombre());
                            break;
                        case 4:
                            cell.setCellValue(df.format(producto.getPrecio()) + "€");
                            break;
                        case 5:
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String fechaFormat = sdf.format(producto.getFechaLanz());
                            cell.setCellValue(fechaFormat);
                            break;
                        case 6:
                            cell.setCellValue(producto.isStock() ? "sí" : "no");
                            break;
                        case 7:
                            cell.setCellValue(producto.getPosicion() + 1);
                            break;
                    }
                }
            }

            XSSFCellStyle estiloCeldaDerecha = workbook.createCellStyle();
            estiloCeldaDerecha.setAlignment(HorizontalAlignment.RIGHT);

            String sumaFormateada = df.format(getSum());

            Row totalRow = sheet.createRow(rowNum++);
            Cell headerCell = totalRow.createCell(3);
            headerCell.setCellStyle(estiloCelda);
            headerCell.setCellValue("Total Precio: ");
            Cell totalCell = totalRow.createCell(4);
            totalCell.setCellStyle(estiloCeldaDerecha);
            totalCell.setCellValue(sumaFormateada + "€");

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            sheet.setAutoFilter(new CellRangeAddress(startingRow, rowNum, 0, headers.length - 1));

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

    // EJECUTA LAS OPCIONES DEL PDF
    public PDFOptions getPdfOpt() {
        return pdfOpt;
    }

    // DESCARGA EL ARCHIVO ZIP
    public StreamedContent getArchivoZip() {
        File archivoZip = generarArchivoZip();
        final InputStream inputStream;
        try {
            inputStream = new FileInputStream(archivoZip);
        } catch (FileNotFoundException e) {
            return null;
        }
        return DefaultStreamedContent.builder()
                .name("productos.zip")
                .contentType("application/zip")
                .stream(() -> inputStream)
                .build();
    }

    // CREA UN ARCHIVO ZIP CONTENEDOR DE DOS ARCHIVOS, XLSX Y PDF
    private File generarArchivoZip() {
        List<File> archivos = new ArrayList<>();
        archivos.add(generarArchivoXLSX());

        File archivoZip = new File("productos.zip");

        try (FileOutputStream fos = new FileOutputStream(archivoZip); ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (File archivo : archivos) {
                ZipEntry zipEntry = new ZipEntry(archivo.getName());
                zos.putNextEntry(zipEntry);

                byte[] bytes = Files.readAllBytes(archivo.toPath());
                zos.write(bytes, 0, bytes.length);
                zos.closeEntry();
            }
        } catch (IOException e) {
        }

        return archivoZip;
    }

    // GENERA EL ARCHIVO XLSX PARA LA COMPRESIÓN EN ZIP
    public File generarArchivoXLSX() {
        try {
            File archivoXLSX;
            try (
                    XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("Productos");

                String[] headers = {"Código", "Nombre", "Descripción", "Categoría", "Precio", "Fecha de Lanzamiento", "Stock", "Posición"};
                Font fontBlanco = workbook.createFont();
                fontBlanco.setColor(IndexedColors.WHITE.getIndex());

                XSSFCellStyle estiloCelda = workbook.createCellStyle();
                XSSFColor gris = new XSSFColor(new java.awt.Color(223, 223, 223));
                estiloCelda.setFillForegroundColor(gris);

                estiloCelda.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                XSSFCellStyle estiloFilaNoStock = workbook.createCellStyle();
                XSSFColor rojo = new XSSFColor(new java.awt.Color(255, 0, 56));
                estiloFilaNoStock.setFillForegroundColor(rojo);

                estiloFilaNoStock.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                estiloFilaNoStock.setFont(fontBlanco);

                XSSFCellStyle estiloFilaPrecio = workbook.createCellStyle();
                XSSFColor verde = new XSSFColor(new java.awt.Color(17, 109, 7));
                estiloFilaPrecio.setFillForegroundColor(verde);

                estiloFilaPrecio.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                estiloFilaPrecio.setFont(fontBlanco);

                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                symbols.setGroupingSeparator('.');
                DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

                int rowNum = 1;
                int startingRow = 0;
                if (productosData.size() != filtroProductos.size()) {

                    Row filtersRow = sheet.createRow(0);
                    List<String> filters = new ArrayList<>();

                    if (this.categoriasSearch.size() != this.categorias.size() && !this.categoriasSearch.isEmpty()) {

                        for (int cat : this.categoriasSearch) {
                            filters.add("Categoría: " + categorias.get(cat).getNombre());
                        }
                    }
                    if (rangoInicio != null && rangoFin != null) {
                        filters.add("Fecha inicio: " + formatDate(rangoInicio) + " Fecha fin: " + formatDate(rangoFin));
                    } else if (rangoInicio != null) {
                        filters.add("Fecha inicio: " + formatDate(rangoInicio));
                    } else if (rangoFin != null) {
                        filters.add("Fecha fin: " + formatDate(rangoFin));
                    }

                    for (int i = 0; i < filters.size(); i++) {
                        Cell cell = filtersRow.createCell(i);
                        cell.setCellValue(filters.get(i));
                    }

                    Row headerRow = sheet.createRow(2);

                    rowNum = 3;
                    startingRow = 2;

                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                        cell.setCellStyle(estiloCelda);
                    }

                } else {
                    Row headerRow = sheet.createRow(0);

                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                        cell.setCellStyle(estiloCelda);
                    }
                }
                for (Producto producto : this.filtroProductos) {
                    Row row = sheet.createRow(rowNum++);

                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = row.createCell(i);

                        if (!producto.isStock()) {
                            cell.setCellStyle(estiloFilaNoStock);
                        } else if (producto.getPrecio() > 100) {
                            cell.setCellStyle(estiloFilaPrecio);
                        }

                        switch (i) {
                            case 0:
                                cell.setCellValue(producto.getCodigo());
                                break;
                            case 1:
                                cell.setCellValue(producto.getNombre());
                                break;
                            case 2:
                                cell.setCellValue(producto.getDescripcion());
                                break;
                            case 3:
                                cell.setCellValue(producto.getCategoria().getNombre());
                                break;
                            case 4:
                                cell.setCellValue(df.format(producto.getPrecio()) + "€");
                                break;
                            case 5:
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String fechaFormat = sdf.format(producto.getFechaLanz());
                                cell.setCellValue(fechaFormat);
                                break;
                            case 6:
                                cell.setCellValue(producto.isStock() ? "sí" : "no");
                                break;
                            case 7:
                                cell.setCellValue(producto.getPosicion() + 1);
                                break;
                        }
                    }
                }

                XSSFCellStyle estiloCeldaDerecha = workbook.createCellStyle();
                estiloCeldaDerecha.setAlignment(HorizontalAlignment.RIGHT);

                String sumaFormateada = df.format(getSum());
                Row totalRow = sheet.createRow(rowNum++);

                Cell headerCell = totalRow.createCell(3);
                headerCell.setCellStyle(estiloCelda);
                headerCell.setCellValue("Total Precio: ");

                Cell totalCell = totalRow.createCell(4);
                totalCell.setCellStyle(estiloCeldaDerecha);
                totalCell.setCellValue(sumaFormateada + "€");

                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                sheet.setAutoFilter(new CellRangeAddress(startingRow, rowNum, 0, headers.length - 1));

                // Generar el archivo XLSX
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);
                byte[] content = out.toByteArray();

                archivoXLSX = File.createTempFile("productos", ".xlsx");

                try (FileOutputStream fos = new FileOutputStream(archivoXLSX)) {
                    fos.write(content);
                }
            }

            return archivoXLSX;

        } catch (IOException e) {
            return null;
        }
    }

    // GESTIONA LA CREACIÓN U OCULTACIÓN DE LA TABLA DE LUGARES DE LOS PRODUCTOS
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

    // GESTIONA EL EVENTO DE REORDENACIÓN DE FILAS
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
        }
    }

    // GESTIONA LA SUBIDA DE ARCHIVOS .XLSX O .XLS
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

    // DEVUELVE UNA CADENA EQUIVALENTE AL VALOR DE UNA CELDA, ÚTIL PARA LA LECTURA DE UN EXCEL
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

    // DEVUELVE LA SUMA DE PRECIOS DE LOS PRODUCTOS VISIBLES
    public double getSum() {
        if (!filtroProductos.isEmpty()) {
            return filtroProductos.stream().mapToDouble(Producto::getPrecio).sum();
        } else {
            return 0;
        }
    }

    public PaletsRespuesta llamada(String usuario, String password, String sscc, int buscarMuelle, int lineas) {
        PaletsRespuesta r = null;
        Peticion p = new Peticion();
        p.setUsuario(usuario);
        p.setPassword(password);
        p.setParametro1(sscc);
        p.setParametro2(String.valueOf(buscarMuelle));
        p.setParametro3(String.valueOf(lineas));
        p.setMetodo(Metodos.GET_PALET);
        return r;
    }
}
