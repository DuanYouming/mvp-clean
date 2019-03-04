package com.foxconn.bandon.recipe.model;

import com.foxconn.bandon.R;
import java.util.ArrayList;
import java.util.List;

public class CategoryFactory {
    private static final int INDEX_SHAPE = 0;
    private static final int INDEX_RECOMMEND = 1;
    private static final int INDEX_MEALS = 2;
    public static final int INDEX_MORE = 3;

    public static List<CategoryInfo> initCategories(int index) {
        List<CategoryInfo> categories = new ArrayList<>();
        switch (index) {
            case INDEX_SHAPE:
                categories = getShapeCategories();
                break;
            case INDEX_RECOMMEND:
                categories = getRecommendCategories();
                break;
            case INDEX_MEALS:
                categories = getMealsCategories();
                break;
            case INDEX_MORE:
                categories = getMore();
                break;
            default:
                break;
        }
        return categories;
    }

    public static List<BannerInfo> initBannerInfo(int index) {
        List<BannerInfo> info = new ArrayList<>();
        switch (index) {
            case INDEX_SHAPE:
                info = getShapeBannerInfo();
                break;
            case INDEX_RECOMMEND:
                info = getRecommendBannerInfo();
                break;
            case INDEX_MEALS:
                info = getMealsBannerInfo();
                break;
            default:
                break;
        }
        return info;
    }


    private static List<CategoryInfo> getShapeCategories() {
        List<CategoryInfo> categories = new ArrayList<>();
        categories.add(new CategoryInfo("0010001046", "西餐", "0010001004"));
        categories.add(new CategoryInfo("0010001042", "台湾美食", "0010001004"));
        categories.add(new CategoryInfo("0010001044", "日本料理", "0010001004"));
        categories.add(new CategoryInfo("0010001052", "素食", "0010001006"));
        return categories;
    }

    private static List<CategoryInfo> getRecommendCategories() {
        List<CategoryInfo> categories = new ArrayList<>();
        categories.add(new CategoryInfo("0010001036", "上海菜", "0010001006"));
        categories.add(new CategoryInfo("0010001059", "滋阴", "0010001006"));
        categories.add(new CategoryInfo("0010001054", "二人世界", "0010001005"));
        categories.add(new CategoryInfo("0010001008", "素菜", "0010001002"));
        return categories;
    }

    private static List<CategoryInfo> getMealsCategories() {
        List<CategoryInfo> categories = new ArrayList<>();
        categories.add(new CategoryInfo("0010001009", "早餐", "0010001002"));
        categories.add(new CategoryInfo("0010001016", "午餐", "0010001003"));
        categories.add(new CategoryInfo("0010001058", "晚餐", "0010001005"));
        return categories;
    }

    private static List<CategoryInfo> getMore() {
        List<CategoryInfo> categories = new ArrayList<>();
        categories.add(new CategoryInfo("0010001031", "川菜", "0010001004"));
        categories.add(new CategoryInfo("0010001032", "粤菜", "0010001004"));
        categories.add(new CategoryInfo("0010001035", "湘菜", "0010001004"));
        categories.add(new CategoryInfo("0010001063", "养生", "0010001006"));
        return categories;
    }


    private static List<BannerInfo> getShapeBannerInfo(){
        List<BannerInfo> info = new ArrayList<>();
        info.add(new BannerInfo(R.drawable.recipe_sharp_banner_1, "怎么自制橙香椰汁咖喱牛腩饭", "00100010110000033862"));
        info.add(new BannerInfo(R.drawable.recipe_sharp_banner_2, "养身菜谱，我就选莴笋牛腩!", "00100010200000040207"));
        info.add(new BannerInfo(R.drawable.recipe_sharp_banner_3, "爱吃蔬菜的习惯从婴儿时期开始----骨汤炖西兰花！", "00100010090000027519"));
        return info;
    }

    private static List<BannerInfo> getRecommendBannerInfo(){
        List<BannerInfo> info = new ArrayList<>();
        info.add(new BannerInfo(R.drawable.recipe_recommend_banner_1, "轻食主义！零负担料理分享", "00100010140000037023"));
        info.add(new BannerInfo(R.drawable.recipe_recommend_banner_2, "杯子蛋糕超简单！", "00100010140000036716"));
        info.add(new BannerInfo(R.drawable.recipe_recommend_banner_3, "剩菜这样做———鸡肉蘑菇炖菜！", "00100010200000040190"));
        return info;
    }

    private static List<BannerInfo> getMealsBannerInfo(){
        List<BannerInfo> info = new ArrayList<>();
        info.add(new BannerInfo(R.drawable.recipe_meal_banner_1, "惬意早餐，轻松享受慵懒周末！", "00100010280000040276"));
        info.add(new BannerInfo(R.drawable.recipe_meal_banner_2, "鱼肉煎饼，异国料理自己动手做！", "00100010140000039927"));
        info.add(new BannerInfo(R.drawable.recipe_meal_banner_3, "非传统口味的奶香玫瑰煎蛋饼", "00100010140000037576"));
        return info;
    }




}
