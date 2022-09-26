package Entities;

import Connections.Database;
import Tools.Types;
import java.util.logging.Logger;

public class VideoHandler {
    private static Database database = null;
    private static Logger logger;

    public VideoHandler(Database database, Logger logger) {
        VideoHandler.database = database;
        VideoHandler.logger = logger;
    }

    public Video fetchVideo(String videoID){
        return VideoHandler.database.getVideoInformation(videoID);
    }

    public Member getMemberInformation(String membershipId){
        return VideoHandler.database.getMemberInformation(membershipId);
    }

    public boolean returnVideo(Video video) {
        return VideoHandler.database.returnVideo(video) == Types.QueryProgress.COMPLETE;
    }

    public boolean borrowVideo(Video video, Member member) {
        return database.borrowVideo(video.getVideoID(), member.getMembershipID()) == Types.QueryProgress.COMPLETE;
    }

    public boolean register(Video video) {
        return VideoHandler.database.addVideo(video) == Types.QueryProgress.COMPLETE;
    }
}
