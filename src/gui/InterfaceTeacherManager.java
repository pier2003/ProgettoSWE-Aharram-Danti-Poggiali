package gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InterfaceTeacherManager {
	@FXML
	private Button btnLesson;
	@FXML
	public void openLesson() {
		openWindow("../resources/LessonInterface.fxml", "Lesson");
	}

	@FXML
	public void openHomework() {
		openWindow("../resources/HomeworkInterface.fxml", "Homework");
	}
	
	@FXML
	public void openMeeting() {
		openWindow("../resources/MeetingInterface.fxml", "Meeting");
	}
	
	private void openWindow(String path, String nameWindow) {
		try {
			// Carica la nuova scena
			Parent loader = FXMLLoader.load(getClass().getResource(path));
			Scene secondaryScene = new Scene(loader);
			Stage newWindow = new Stage();
			newWindow.setTitle(nameWindow);
			newWindow.setScene(secondaryScene);
			newWindow.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}