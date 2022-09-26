package Entities;

import Errors.ReturningUnBorrowedVideoException;
import Tools.Types.*;
import Tools.Utils;

import java.time.LocalDate;
import java.time.Period;

public class Video {
    private final String videoID;
    private final String videoName;
    private final VideoCategory videoCategory;
    private VideoStatus videoStatus;
    private Member borrower = null;
    private final String dateLastBorrowed;
    private String latestVideoReport;
    private int fineIncurred = 0;

    public Video(String videoID, String videoName, VideoCategory videoCategory) {
        this.videoID = videoID;
        this.videoName = videoName;
        this.videoCategory = videoCategory;
        this.videoStatus = VideoStatus.AVAILABLE;
        this.dateLastBorrowed = null;
    }

    public Video(String videoID, String videoName, VideoCategory videoCategory, VideoStatus videoStatus) {
        this.videoID = videoID;
        this.videoName = videoName;
        this.videoCategory = videoCategory;
        this.videoStatus = videoStatus;
        this.dateLastBorrowed = null;
    }

    public Video(String videoID, String videoName, VideoCategory videoCategory, VideoStatus videoStatus, Member borrower, String dateLastBorrowed) {
        this.videoID = videoID;
        this.videoName = videoName;
        this.videoCategory = videoCategory;
        this.videoStatus = videoStatus;
        this.dateLastBorrowed = dateLastBorrowed;
        this.borrower = borrower;
    }

    public String getVideoName() {
        return videoName;
    }
    public String getVideoID() {
        return videoID;
    }
    public void setLatestVideoReport(String latestVideoReport) {this.latestVideoReport = latestVideoReport; }
    public String getLatestVideoReport() {return latestVideoReport; }
    public VideoCategory getVideoCategory() {
        return videoCategory;
    }
    public VideoStatus getVideoStatus() {
        return videoStatus;
    }
    public boolean isInvalid() {return this.videoName == null && this.videoID == null; }
    public boolean isAvailable(){
        return this.borrower != null;
    }

    public int getBorrowingRate() {return Utils.getBorrowingRate().get(this.videoCategory); }
    public int checkBookFine() throws Exception {
        if (!this.isAvailable()) throw new ReturningUnBorrowedVideoException();
        if (this.dateLastBorrowed == null) return 0;
        int numberOfDueDays = getDueDays();
        if (numberOfDueDays > 14) {
            this.videoStatus = VideoStatus.LOST;
            this.fineIncurred += 700;
        }
        else {
            numberOfDueDays = 0;
        }
        if (numberOfDueDays > 3) {
            numberOfDueDays -= 3;
        }
        this.fineIncurred += this.getFineRate() * numberOfDueDays;
        return this.fineIncurred;
    }
    public void chargeBookForLossOrDamage() {
        this.fineIncurred += 700;
    }
    public int getDueDays(){
        assert this.dateLastBorrowed != null;
        int dueDays = 0;
        String[] dateMonthYearValues = this.dateLastBorrowed.split("-");
        LocalDate localDate = LocalDate.of(Integer.parseInt(dateMonthYearValues[0]),
                Integer.parseInt(dateMonthYearValues[1]),
                Integer.parseInt(dateMonthYearValues[2]));
        Period period = Period.between(localDate, LocalDate.now());
        dueDays += (period.getDays() + (period.getMonths() * 28) + (period.getYears() * 12 * 28));
        return dueDays;
    }
    public void setVideoStatus(VideoStatus videoStatus) {
        this.videoStatus = videoStatus;
    }
    private int getFineRate() {return Utils.getFineRate().get(this.videoCategory); }
    public String getVideoStatusString() {return Utils.getVideoStatusString().get(this.videoStatus); }
    public String getVideoCategoryString() {return Utils.getVideoCategoryStrings().get(this.videoCategory); }
    public boolean isLost() {return this.videoStatus == VideoStatus.LOST; }
}
