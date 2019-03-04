package com.foxconn.bandon.recipe.model;

public class CategoryInfo {

    private String ctgId;
    private String name;
    private String parentId;

    CategoryInfo(String ctgId, String name, String parentId) {
        this.ctgId = ctgId;
        this.name = name;
        this.parentId = parentId;
    }

    public String getCtgId() {
        return ctgId;
    }
    public String getName() {
        return name;
    }
    public String getParentId() {
        return parentId;
    }

}
