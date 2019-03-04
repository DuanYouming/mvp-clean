package com.foxconn.bandon.recipe.model;


import java.util.List;

public class RecipeCategory {

    private String msg;
    private String retCode;
    private Result result;

    public String getMsg() {
        return msg;
    }

    public Result getResult() {
        return result;
    }

    public String getRetCode() {
        return retCode;
    }

    public static class Result {

        private CategoryInfo categoryInfo;
        private List<CategoryItem> childs;

        public CategoryInfo getCategoryInfo() {
            return categoryInfo;
        }

        public List<CategoryItem> getItems() {
            return childs;
        }
    }
    public static class CategoryItem {

        private CategoryInfo categoryInfo;
        private List<Category> childs;

        public CategoryInfo getCategoryInfo() {
            return categoryInfo;
        }

        public List<Category> getCategories() {
            return childs;
        }

    }

    public static class Category {

        private CategoryInfo categoryInfo;

        public CategoryInfo getCategoryInfo() {
            return categoryInfo;
        }

    }
}
