package ci.pigier.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ci.pigier.model.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class BaseController {
	protected Alert alert;
	protected static Note editNote = null;

	String url = "jdbc:mysql://0.0.0.0:3306/appnotetaking_db";
	String user = "root";
	String password = "root";

	protected static ObservableList<Note> data = FXCollections.<Note>observableArrayList();

	protected void navigate(Event event, URL fxmlDocName) throws IOException {
		// Chargement du nouveau document FXML de l'interface utilisateur
		Parent pageParent = FXMLLoader.load(fxmlDocName);
		// Création d'une nouvelle scène
		Scene scene = new Scene(pageParent);
		// Obtention de la scène actuelle
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		// Masquage de l'ancienne scène (facultatif)
		appStage.hide(); // facultatif
		// Définition de la nouvelle scène pour la scène
		appStage.setScene(scene);
		// Affichage de la scène
		appStage.show();
	}

	public Connection getConnection() {
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connecté.");
		} catch (SQLException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		
		return conn;
	}
	
	public void loadNotesFromDatabase() {
	    String query = "SELECT * FROM notes";
	    data.clear(); // Effacer les anciennes notes

	    try (Connection conn = getConnection();
	         java.sql.Statement stmt = conn.createStatement();
	         java.sql.ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            Note note = new Note(
	                rs.getInt("id"),
	                rs.getString("title"),
	                rs.getString("description")
	            );
	            data.add(note);
	        }
	        System.out.println("Notes chargées avec succès.");

	    } catch (SQLException e) {
	        System.out.println("Erreur lors du chargement des notes: " + e.getMessage());
	    }
	}
	
	public void addNoteToDatabase(Note note) {
	    String query = "INSERT INTO notes (title, description) VALUES (?, ?)";

	    try (Connection conn = getConnection();
	    	java.sql.PreparedStatement pstmt = conn.prepareStatement(query))
	    {

	        pstmt.setString(1, note.getTitle());
	        pstmt.setString(2, note.getDescription());
	        pstmt.executeUpdate();
	        System.out.println("Note ajoutée avec succès.");

	    } catch (SQLException e) {
	        System.out.println("Erreur lors de l'ajout de la note: " + e.getMessage());
	    }
	}

	public void deleteNoteFromDatabase(Note note) {
	    String query = "DELETE FROM notes WHERE id = ?";

	    try (Connection conn = getConnection();
	         java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {

	        pstmt.setInt(1, note.getID());
	        pstmt.executeUpdate();
	        System.out.println("Note supprimée avec succès.");

	    } catch (SQLException e) {
	        System.out.println("Erreur lors de la suppression de la note: " + e.getMessage());
	    }
	}

	public void updateNoteInDatabase(Note note) {
	    String query = "UPDATE notes SET title = ?, description = ? WHERE id = ?";

	    try (Connection conn = getConnection();
	         java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {

	        pstmt.setString(1, note.getTitle());
	        pstmt.setString(2, note.getDescription());
	        pstmt.setInt(3, note.getID());
	        pstmt.executeUpdate();
	        System.out.println("Note mise à jour avec succès.");

	    } catch (SQLException e) {
	        System.out.println("Erreur lors de la mise à jour de la note: " + e.getMessage());
	    }
	}

}
