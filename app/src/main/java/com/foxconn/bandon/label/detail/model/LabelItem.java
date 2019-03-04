package com.foxconn.bandon.label.detail.model;

public class LabelItem {

    private Data data;

    public LabelItem() {

    }

    public LabelItem(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private String foodTagName;
        private String foodTagBigPicture;
        private String foodNutrition;

        public Data() {
        }

        public Data(String foodTagName, String foodTagBigPicture, String foodNutrition) {
            this.foodTagName = foodTagName;
            this.foodTagBigPicture = foodTagBigPicture;
            this.foodNutrition = foodNutrition;
        }

        public String getFoodTagName() {
            return foodTagName;
        }

        public String getFoodTagBigPicture() {
            return foodTagBigPicture;
        }

        public String getFoodNutrition() {
            return foodNutrition;
        }
    }
}
