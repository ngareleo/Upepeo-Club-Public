package Tools;

import java.util.HashMap;

public class Utils {
    public static HashMap<String, Integer> getCategoryId(){
        HashMap<String, Integer> videoCategories = new HashMap<>();
        videoCategories.put("General", 2);
        videoCategories.put("Comedy", 1);
        videoCategories.put("Horror", 3);
        videoCategories.put("Cartoon", 5);
        videoCategories.put("Thriller", 4);
        return videoCategories;
    }

    public static HashMap<Integer, String> getCategoryName(){
        HashMap<Integer, String> videoCategories = new HashMap<>();
        videoCategories.put(2, "General");
        videoCategories.put(1, "Comedy");
        videoCategories.put(3, "Horror");
        videoCategories.put(5, "Cartoon");
        videoCategories.put(4, "Thriller");
        return videoCategories;
    }

    public static HashMap<Integer, String> getStatusInfo(){
        HashMap<Integer, String> videoStatus = new HashMap<>();
        videoStatus.put(1, "Available");
        videoStatus.put(2, "Withdrawn");
        videoStatus.put(3, "Lost");
        videoStatus.put(4, "Borrowed");
        return videoStatus;
    }
    public static HashMap<Integer, Integer> getBorrowingRate(){
        HashMap<Integer, Integer> borrowingRates = new HashMap<>();
        borrowingRates.put(1, 70);
        borrowingRates.put(2, 50);
        borrowingRates.put(3, 60);
        borrowingRates.put(4, 60);
        borrowingRates.put(5, 80);
        return borrowingRates;
    }

    public static HashMap<Integer, Integer> getFineRate(){
        HashMap<Integer, Integer> fineRates = new HashMap<>();
        fineRates.put(1, 42);
        fineRates.put(2, 30);
        fineRates.put(3, 24);
        fineRates.put(4, 36);
        fineRates.put(5, 48);
        return fineRates;
    }

}
