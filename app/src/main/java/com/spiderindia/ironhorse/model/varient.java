package com.spiderindia.ironhorse.model;


public class varient {

    private String id;
    private String productId;
    private String type;
    private String measurement;
    private String measurementUnitId;
    private String price;
    private String discountedPrice;
    private String serveFor;
    private String stock;
    private String stockUnitId;
    private String measurementUnitName;
    private String stockUnitName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getMeasurementUnitId() {
        return measurementUnitId;
    }

    public void setMeasurementUnitId(String measurementUnitId) {
        this.measurementUnitId = measurementUnitId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getServeFor() {
        return serveFor;
    }

    public void setServeFor(String serveFor) {
        this.serveFor = serveFor;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getStockUnitId() {
        return stockUnitId;
    }

    public void setStockUnitId(String stockUnitId) {
        this.stockUnitId = stockUnitId;
    }

    public String getMeasurementUnitName() {
        return measurementUnitName;
    }

    public void setMeasurementUnitName(String measurementUnitName) {
        this.measurementUnitName = measurementUnitName;
    }

    public varient(String id, String productId, String type, String measurement, String measurementUnitId, String price, String discountedPrice, String serveFor, String stock, String stockUnitId, String measurementUnitName, String stockUnitName) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.measurement = measurement;
        this.measurementUnitId = measurementUnitId;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.serveFor = serveFor;
        this.stock = stock;
        this.stockUnitId = stockUnitId;
        this.measurementUnitName = measurementUnitName;
        this.stockUnitName = stockUnitName;
    }

    public String getStockUnitName() {
        return stockUnitName;
    }

    public void setStockUnitName(String stockUnitName) {
        this.stockUnitName = stockUnitName;
    }

}