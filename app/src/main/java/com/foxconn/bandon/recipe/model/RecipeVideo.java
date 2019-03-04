package com.foxconn.bandon.recipe.model;

import java.util.List;

public class RecipeVideo {

    private List<RecipesBean> recipes;

    public List<RecipesBean> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipesBean> recipes) {
        this.recipes = recipes;
    }

    public static class RecipesBean {
        /**
         * name : 东坡肉
         * path : Dongpo_Pork.mp4
         */

        private String name;
        private String path;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
