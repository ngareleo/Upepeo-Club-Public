package Panels.VideoRegistrationPanel;

import Connections.Database;
import Entities.Video;
import Entities.VideoHandler;
import Tools.Types;
import Tools.Utils;
import javax.swing.*;
import java.util.logging.Logger;

public class VideoRegister {
    public JPanel VideoRegister;
    private JTextField videoName;
    private JButton submitButton;
    private JComboBox videoCategory;
    private JLabel videoNameError;
    private JLabel submitError;
    public JButton backButton;
    private final VideoHandler videoHandler;

    public VideoRegister(Database database, Logger logger){
        this.videoHandler = new VideoHandler(database, logger);
        setAllErrorFieldsInvisible();
        handleDataSubmission();
    }

    private void handleDataSubmission(){
        submitButton.addActionListener( e ->{
            setAllErrorFieldsInvisible();
            Video video = getVideoFormData();
            if(video == null) return;
            boolean videoRegistered = this.videoHandler.register(video);
            if(videoRegistered){
                clearAllFormFields();
                JOptionPane.showMessageDialog(VideoRegister,VideoRegistrationStrings.videoRegistrationSuccessText);
            }else{
                submitError.setText(VideoRegistrationStrings.internalError);
                submitError.setVisible(true);
            }
        });
    }

    private Video getVideoFormData() {
        if(videoName.getText().trim().equals(VideoRegistrationStrings.NO_TEXT)){
            videoNameError.setText(VideoRegistrationStrings.videoNameRequiredText);
            videoNameError.setVisible(true);
            return null;
        }
        String videoNameValue = videoName.getText().trim();
        String videoCategoryValue = (String) videoCategory.getSelectedItem();
        Types.VideoCategory videoCategoryType = Utils.getVideoCategoryType().get(videoCategoryValue);
        return new Video(null, videoNameValue, videoCategoryType);
    }

    private void clearAllFormFields(){
        videoName.setText(VideoRegistrationStrings.NO_TEXT);
    }
    private void setAllErrorFieldsInvisible(){
        videoNameError.setVisible(false);
        submitError.setVisible(false);
    }
}
