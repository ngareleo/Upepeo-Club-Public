package VideoRegistrationPanel;

import Connections.Database;
import Tools.Utils;

import javax.swing.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class VideoRegister {
    private final Database database;
    public JPanel VideoRegister;
    private JTextField videoName;
    private JButton submitButton;
    private JComboBox videoCategory;
    private JLabel videoNameError;
    private JLabel submitError;
    public JButton backButton;

    public VideoRegister(Database database, Logger logger){
        this.database = database;
        setAllInvisible();
        check_credentials();
    }

    private void check_credentials(){
        submitButton.addActionListener( e ->{
            setAllInvisible();
            if(videoName.getText().trim().equals(VideoRegistrationStrings.NO_TEXT)){
                videoNameError.setText(VideoRegistrationStrings.videoNameRequiredText);
                videoNameError.setVisible(true);
                return;
            }

            String videoNameValue = videoName.getText().trim(),
                    videoCategoryValue = (String) videoCategory.getSelectedItem();
            HashMap<String, Integer> map = Utils.getCategoryId();
            int video_category = map.get(videoCategoryValue);
            int res = database.addVideo(videoNameValue, video_category);
            if( res == 1 ){
                clearAll();
                JOptionPane.showMessageDialog(VideoRegister,VideoRegistrationStrings.videoRegistrationSuccessText);
            }else{
                submitError.setText(VideoRegistrationStrings.internalError);
                submitError.setVisible(true);
            }
        });
    }

    private void clearAll(){
        videoName.setText(VideoRegistrationStrings.NO_TEXT);
    }
    private void setAllInvisible(){
        videoNameError.setVisible(false);
        submitError.setVisible(false);
    }
}
