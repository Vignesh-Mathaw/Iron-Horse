package com.spiderindia.ironhorse.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Product_Retrofit implements Serializable {

    private String id, name, slug, subcategory_id, image,  description, status, date_added, category_id, indicator,
            brandname, rating, rating_count, row_order, brand_id, price,stock_check;
    private ArrayList<varient> priceVariations;
    private ArrayList<String> other_images;

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getRow_order() {
        return row_order;
    }

    public void setRow_order(String row_order) {
        this.row_order = row_order;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock_check() {
        return stock_check;
    }

    public void setStock_check(String stock_check) {
        this.stock_check = stock_check;
    }

    public void setPriceVariations(ArrayList<varient> priceVariations) {
        this.priceVariations = priceVariations;
    }



    public String getBrandname() {
        return brandname;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating_count() {
        return rating_count;
    }

    public void setRating_count(String rating_count) {
        this.rating_count = rating_count;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public double globalStock;

    public Product_Retrofit() {
    }

    public Product_Retrofit(String brandname, String id, String name, String slug, String subcategory_id,
                            String image, ArrayList<String> other_images, String description, String status, String date_added,
                            String category_id, ArrayList<varient> priceVariations, String indicator, String rating, String rating_count) {
        this.brandname = brandname;
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.subcategory_id = subcategory_id;
        this.image = image;
        this.other_images = other_images;
        this.description = description;
        this.status = status;
        this.date_added = date_added;
        this.priceVariations = priceVariations;
        this.category_id = category_id;
        this.indicator = indicator;
        this.rating = rating;
        this.rating_count = rating_count;
    }

    public String getIndicator() {
        return indicator;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOther_images(ArrayList<String>  other_images) {
        this.other_images = other_images;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<String>  getOther_images() {
        return other_images;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDate_added() {
        return date_added;
    }

    public double getGlobalStock() {
        return globalStock;
    }

    public void setGlobalStock(double globalStock) {
        this.globalStock = globalStock;
    }

    public ArrayList<varient> getPriceVariations() {
        return priceVariations;
    }


}
