package com.foxconn.bandon.food.model;

import java.util.List;

public class ColdRoomFood {
    public Content data;

    public class Content {
        public List<Label> list;
    }

    public class Label {
        public int id;
        public String deviceId;
        public String foodTagRename;
        public int numberSurplus;
        public String dueDate;
        public String foodTagBigPicture;
        public String foodTagSmallPicture;
        public String foodNutrition;
        public String iconColor;
        public String iconColorDetail;
        public String refrigeratingCompartmentPicture;
        public String xCoordinate;
        public String yCoordinate;
    }
}
