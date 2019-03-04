package com.foxconn.bandon.recipe.model;

import java.util.List;

public class CategoryList {
    private String msg;
    private ResultBean result;
    private String retCode;
    private String  categoryName;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static class ResultBean {

        private int curPage;
        private int total;
        private List<ListBean> list;

        public int getCurPage() {
            return curPage;
        }

        public int getTotal() {
            return total;
        }

        public List<ListBean> getList() {
            return list;
        }

    }

    public static class ListBean {

        private String ctgTitles;
        private String menuId;
        private String name;
        private RecipeBean recipe;
        private String thumbnail;
        private List<String> ctgIds;

        public String getCtgTitles() {
            return ctgTitles;
        }

        public String getMenuId() {
            return menuId;
        }


        public String getName() {
            return name;
        }

        public RecipeBean getRecipe() {
            return recipe;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public List<String> getCtgIds() {
            return ctgIds;
        }

        public static class RecipeBean {

            private String ingredients;
            private String method;
            private String sumary;
            private String title;

            public String getIngredients() {
                return ingredients;
            }

            public String getMethod() {
                return method;
            }

            public String getSumary() {
                return sumary;
            }

            public String getTitle() {
                return title;
            }

        }
    }
}
