package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.domain.CheckoutRecordEntry;
import model.domain.LoginException;

@SuppressWarnings("unused")
public class CheckoutTableView extends Stage {

	String isbn = "";
	String checkoutDate = "";
	String dueDate = "";
	//Stage primaryStage;

	private CheckoutRecordEntry entry;

	public CheckoutTableView(CheckoutRecordEntry entry) {
		this.entry = entry;
		Stage primaryStage = new Stage();
		primaryStage.setTitle("Summary of Checkoutbook");


		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setVgap(10);
		grid.setHgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Label isbnLabel = new Label("ISBN");
		Label checoutLabel = new Label("Checout date");
		Label dueLabel = new Label("Due date");

		HBox colName = new HBox(70);
		colName.setAlignment(Pos.CENTER);
		colName.getChildren().addAll(isbnLabel, checoutLabel, dueLabel);

		Label isbnLabelValue = new Label();
		Label checkoutLabelValue = new Label();
		Label dueLabelValue = new Label();
		HBox valueBox = new HBox(70);
		valueBox.setAlignment(Pos.CENTER);

		isbn = entry.getBookCopy().getBook().getIsbn();
		checkoutDate = entry.getChechoutDate() + "";
		dueDate = entry.getDueDate() + "";
		isbnLabelValue.setText(isbn);
		checkoutLabelValue.setText(checkoutDate);
		dueLabelValue.setText(dueDate);
		valueBox.getChildren().addAll(isbnLabelValue, checkoutLabelValue, dueLabelValue);

		Button btnReturn = new Button("OK");
		btnReturn.setPrefWidth(60);
		HBox nboxReturn = new HBox(20);
		nboxReturn.setAlignment(Pos.BOTTOM_CENTER);
		nboxReturn.setPrefHeight(20);
		nboxReturn.setPrefWidth(20);
		nboxReturn.getChildren().add(btnReturn);

		VBox vboxalldata = new VBox(20);
		vboxalldata.setAlignment(Pos.CENTER);
		vboxalldata.getChildren().addAll(colName, valueBox, nboxReturn);
		grid.add(vboxalldata, 0, 0);

		btnReturn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				((Node)e.getSource()).getScene().getWindow().hide();
			}
		});

		primaryStage.setScene(new Scene(grid, 500, 200));
		primaryStage.show();

	}

}