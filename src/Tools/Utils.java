package Tools;

import java.util.HashMap;

import Tools.Types.*;

public class Utils {
    public static HashMap<VideoCategory, Integer> getCategoryId(){
        HashMap<VideoCategory, Integer> videoCategories = new HashMap<>();
        videoCategories.put(VideoCategory.GENERAL, 2);
        videoCategories.put(VideoCategory.COMEDY, 1);
        videoCategories.put(VideoCategory.HORROR, 3);
        videoCategories.put(VideoCategory.CARTOON, 5);
        videoCategories.put(VideoCategory.THRILLER, 4);
        return videoCategories;
    }

    public static HashMap<Integer, VideoCategory> getCategoryName(){
        HashMap<Integer, VideoCategory> videoCategories = new HashMap<>();
        videoCategories.put(2, VideoCategory.GENERAL);
        videoCategories.put(1, VideoCategory.COMEDY);
        videoCategories.put(3, VideoCategory.HORROR);
        videoCategories.put(5, VideoCategory.CARTOON);
        videoCategories.put(4, VideoCategory.THRILLER);
        return videoCategories;
    }

    public static HashMap<Integer, VideoStatus> getStatusInfo(){
        HashMap<Integer, VideoStatus> videoStatus = new HashMap<>();
        videoStatus.put(1, VideoStatus.AVAILABLE);
        videoStatus.put(2, VideoStatus.WITHDRAWN);
        videoStatus.put(3, VideoStatus.LOST);
        videoStatus.put(4, VideoStatus.BORROWED);
        return videoStatus;
    }

    public static HashMap<VideoStatus, Integer> getStatusNumber() {
        HashMap<VideoStatus, Integer> videoStatus = new HashMap<>();
        videoStatus.put(VideoStatus.AVAILABLE, 1);
        videoStatus.put(VideoStatus.WITHDRAWN, 2);
        videoStatus.put(VideoStatus.LOST, 3);
        videoStatus.put(VideoStatus.BORROWED, 4);
        return videoStatus;
    }
    public static HashMap<VideoCategory, Integer> getBorrowingRate(){
        HashMap<VideoCategory, Integer> borrowingRates = new HashMap<>();
        borrowingRates.put(VideoCategory.GENERAL, 70);
        borrowingRates.put(VideoCategory.COMEDY, 50);
        borrowingRates.put(VideoCategory.HORROR, 60);
        borrowingRates.put(VideoCategory.CARTOON, 60);
        borrowingRates.put(VideoCategory.THRILLER, 80);
        return borrowingRates;
    }

    public static HashMap<VideoCategory, Integer> getFineRate(){
        HashMap<VideoCategory, Integer> fineRates = new HashMap<>();
        fineRates.put(VideoCategory.GENERAL, 42);
        fineRates.put(VideoCategory.COMEDY, 30);
        fineRates.put(VideoCategory.HORROR, 24);
        fineRates.put(VideoCategory.CARTOON, 36);
        fineRates.put(VideoCategory.THRILLER, 48);
        return fineRates;
    }

    public static HashMap<VideoStatus, String> getVideoStatusString() {
        HashMap<VideoStatus, String> videoStatusStrings = new HashMap<>();
        videoStatusStrings.put(VideoStatus.AVAILABLE, "Available");
        videoStatusStrings.put(VideoStatus.LOST, "Lost");
        videoStatusStrings.put(VideoStatus.BORROWED, "Borrowed");
        videoStatusStrings.put(VideoStatus.WITHDRAWN, "Withdrawn");
        return videoStatusStrings;
    }

    public static HashMap<VideoCategory, String> getVideoCategoryStrings() {
        HashMap<VideoCategory, String> videoCategoryStrings = new HashMap<>();
        videoCategoryStrings.put(VideoCategory.CARTOON, "Cartoon");
        videoCategoryStrings.put(VideoCategory.HORROR, "Horror");
        videoCategoryStrings.put(VideoCategory.GENERAL, "General");
        videoCategoryStrings.put(VideoCategory.THRILLER, "Thriller");
        return videoCategoryStrings;
    }

    public static HashMap<String, VideoCategory> getVideoCategoryType() {
        HashMap<String, VideoCategory> videoCategoryStrings = new HashMap<>();
        videoCategoryStrings.put("Cartoon", VideoCategory.CARTOON);
        videoCategoryStrings.put("Horror", VideoCategory.HORROR);
        videoCategoryStrings.put("General", VideoCategory.GENERAL);
        videoCategoryStrings.put("Thriller", VideoCategory.THRILLER);
        return videoCategoryStrings;
    }

    public static HashMap<TransactionTypes, Integer> getTransactionTypes() {
        HashMap<TransactionTypes, Integer> transactionTypes = new HashMap<>();
        transactionTypes.put(TransactionTypes.MEMBER_REGISTRATION, 1);
        transactionTypes.put(TransactionTypes.BORROWING_FEE, 2);
        transactionTypes.put(TransactionTypes.FINE_FEE, 3);
        return transactionTypes;
    }
}
