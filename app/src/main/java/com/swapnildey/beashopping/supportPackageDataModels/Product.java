package com.swapnildey.beashopping.supportPackageDataModels;

public class Product {

    private String id;
    private String name;
    private String offer;
    private String imageUrl;

    public Product(String id, String name, String desc, String imageUrl) {
        this.id = id;
        this.name = name;
        this.offer = desc;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return offer;
    }

    public void setDesc(String desc) {
        this.offer = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
