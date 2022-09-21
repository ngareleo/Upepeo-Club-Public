import javax.swing.*;
import java.util.HashMap;

public class VideoRegister {
    private final Database db;
    public JPanel VideoRegister;
    private JTextField videoName;
    private JButton submitButton;
    private JComboBox videoCategory;
    private JLabel videoNameError;
    private JLabel submitError;
    public JButton backButton;

    VideoRegister(Database database){
        this.db = database;
        setAllInvisible();
        check_credentials();
    }

    private void check_credentials(){
        submitButton.addActionListener( e ->{
            setAllInvisible();
            if(videoName.getText().trim().equals("")){
                videoNameError.setText("Video name entry is mandatory");
                videoNameError.setVisible(true);
                return;
            }

            String videoNameValue = videoName.getText().trim(),
                    videoCategoryValue = (String) videoCategory.getSelectedItem();
            HashMap<String, Integer> map = Include.getCategoryId();
            int video_category = map.get(videoCategoryValue);
            int res = db.addVideo(videoNameValue, video_category);
            System.out.printf("Result  : %d\n", res);
            if( res == 1 ){
                clearAll();
                JOptionPane.showMessageDialog(VideoRegister,"Video added successfully");
            }else{
                submitError.setText("Error submitting data");
                submitError.setVisible(true);
            }
        });
    }

    private void clearAll(){
        videoName.setText("");
    }
    private void setAllInvisible(){
        videoNameError.setVisible(false);
        submitError.setVisible(false);
    }
}
