package gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import businessLogic.LoginController;

public class InterfaceLoginManager {
	@FXML
	private TextField txtUsername;
	@FXML
	private TextField txtPassword;

	public static String hashString(String input) {
		try {
			// Crea un'istanza di MessageDigest per l'algoritmo SHA-256
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// Calcola l'hash della stringa in input
			byte[] hashBytes = digest.digest(input.getBytes());

			// Converti l'hash in formato esadecimale
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0'); // Aggiungi uno zero iniziale se necessario
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Hash algoritm not found", e);
		}
	}

	@FXML
	public void login() {
		String username = txtUsername.getText();
		String password = txtPassword.getText();

		LoginController loginController = new LoginController();

		if (loginController.login(username, password)) {
			try {
				// Carica la nuova scena
				Parent loader = FXMLLoader.load(getClass().getResource("../resources/InterfacciaTeacher.fxml"));
				Scene secondaryScene = new Scene(loader);
				Stage newWindow = new Stage();
				newWindow.setTitle("Teacher...");
				newWindow.setScene(secondaryScene);

				// Ottieni lo stage corrente e chiudilo
				Stage currentStage = (Stage) txtUsername.getScene().getWindow();
				currentStage.close();
				// Mostra la nuova finestra
				newWindow.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}