package ci.pigier.controllers.ui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import ci.pigier.controllers.BaseController;
import ci.pigier.model.Note;
import ci.pigier.ui.FXMLPage;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ListNotesUIController extends BaseController implements Initializable {

    @FXML
    private TableColumn<?, ?> descriptionTc;

    @FXML
    private Label notesCount;

    @FXML
    private TableView<Note> notesListTable;

    @FXML
    private TextField searchNotes;

    @FXML
    private TableColumn<?, ?> titleTc;

    @FXML
    void doDelete(ActionEvent event) {
    	Note selectedNote = notesListTable.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de Suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette note ?");
            alert.setContentText(selectedNote.getTitle());
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
            	deleteNoteFromDatabase(selectedNote);
            	loadNotesFromDatabase(); // Mise à jour de la TableView
                updateNotesCount(); // Mise à jour du nombre de notes affichées
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aucune Sélection");
            alert.setHeaderText("Aucune note sélectionnée");
            alert.setContentText("Veuillez sélectionner une note à supprimer.");
            alert.showAndWait();
        }
    }

    @FXML
    void doEdit(ActionEvent event) throws IOException {
    	Note selectedNote = notesListTable.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
        	Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de Suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette note ?");
            alert.setContentText(selectedNote.getTitle());

            editNote = selectedNote;
            navigate(event, FXMLPage.ADD.getPage());
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aucune Sélection");
            alert.setHeaderText("Aucune note sélectionnée");
            alert.setContentText("Veuillez sélectionner une note à modifier.");
            alert.showAndWait();
        }
    }

    @FXML
    void newNote(ActionEvent event) throws IOException {
    	editNote = null;
    	navigate(event, FXMLPage.ADD.getPage());
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadNotesFromDatabase();
		updateNotesCount();

		FilteredList<Note> filteredData = new FilteredList<>(data, n -> true);
		notesListTable.setItems(filteredData);
		titleTc.setCellValueFactory(new PropertyValueFactory<>("title"));
		descriptionTc.setCellValueFactory(new
		PropertyValueFactory<>("description"));
		searchNotes.setOnKeyReleased(e -> {
			filteredData.setPredicate(n -> {
				if (searchNotes.getText() == null || searchNotes.getText().isEmpty())
					return true;
				return n.getTitle().contains(searchNotes.getText())
						|| n.getDescription().contains(searchNotes.getText());
			});
		});
	}

	private void updateNotesCount() {
        int length = data.size();
        
        if (length > 1)
        	notesCount.setText(data.size() + " Notes");
        else
        	notesCount.setText(data.size() + " Note");
    }
}




