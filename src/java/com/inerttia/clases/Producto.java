package com.inerttia.clases;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Iván Torres
 */
public class Producto {

    // Variables
    private int codigo;
    private String nombre;
    private String descripcion;
    private Categoria categoria;
    private double precio;
    private Date fechaLanz;
    private boolean stock;
    private List<Lugar> lugares;
    private int posicion;

    // Constructores
    public Producto() {
    }

    public Producto(int codigo, String nombre, String descripcion, Categoria categoria, double precio, Date fechaLanz, boolean stock, List<Lugar> lugares,
            int posicion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.fechaLanz = fechaLanz;
        this.stock = stock;
        this.lugares = lugares;
        this.posicion = posicion;
    }

    // Getters y Setters
    /**
     * @return the codigo
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * @return the precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Date getFechaLanz() {
        return fechaLanz;
    }

    public void setFechaLanz(Date fechaLanz) {
        this.fechaLanz = fechaLanz;
    }

    public boolean isStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(boolean stock) {
        this.stock = stock;
    }

    public List<Lugar> getLugares() {
        return lugares;
    }

    public void setLugares(List<Lugar> lugares) {
        this.lugares = lugares;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    // HashCode y Equals
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.codigo;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Producto other = (Producto) obj;
        return this.codigo == other.codigo;
    }

    // ToString
    @Override
    public String toString() {
        return "Producto{" + "codigo=" + codigo + ", nombre=" + nombre + '}';
    }
}
