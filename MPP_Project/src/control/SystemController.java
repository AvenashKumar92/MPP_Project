package control;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.text.TabableView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.dataaccess.Auth;
import model.dataaccess.DataAccessFacade;
import model.dataaccess.TestData;
import model.domain.Address;
import model.domain.Author;
import model.domain.Book;
import model.domain.BookCopy;
import model.domain.CheckoutRecordEntry;
import model.domain.LibraryMember;
import model.domain.LoginException;
import model.domain.User;
import view.AddBookToLibrary;
import view.AddCopyBook;
import view.AddCopyBookBoth;
import view.AddMember;
import view.AddMemberBoth;
import view.Admin;
import view.Authors;
import view.Both;
import view.CheckoutTableView;
import view.Librarian;
import view.LibrarianBoth;
import view.LibrarianHomepage;
import view.Login;
import view.SearchLibraryMember;

public class SystemController {
	@FXML
	private Label ErrorLabel;

	@FXML
	private TextField loginText;

	@FXML
	private TextField loginPass;

	public void login(ActionEvent e) throws Exception, LoginException, ClassNotFoundException {
		String id = loginText.getText();
		String pass = loginPass.getText();

		DataAccessFacade data = new DataAccessFacade();
		HashMap<String, User> map = data.readUserMap();
		if (map.containsKey(id) && map.get(id).getPassword().equals(pass)) {
			String level = map.get(id).getAuthorization() + "";
			switch (level) {
			case "LIBRARIAN":
				Stage primaryStage1 = new Stage();
				((Node) e.getSource()).getScene().getWindow().hide();
				LibrarianHomepage libHome = new LibrarianHomepage();
				libHome.start(primaryStage1);
				break;

			case "ADMIN":
				Stage primaryStage2 = new Stage();
				((Node) e.getSource()).getScene().getWindow().hide();
				Admin ad = new Admin();
				ad.start(primaryStage2);
				break;

			case "BOTH":
				Stage primaryStage3 = new Stage();
				((Node) e.getSource()).getScene().getWindow().hide();
				Both both = new Both();
				both.start(primaryStage3);
				break;
			}
		} else {
			ErrorLabel.setText("User ID or password is incorrect");
		}
	}

	public void exitSystem(ActionEvent e) throws Exception, LoginException {

		((Node) e.getSource()).getScene().getWindow().hide();

	}

	@FXML
	private TextField memberID;
	@FXML
	private TextField ISBN;
	@FXML
	private Label bookLabel;

	@FXML
	private TableView<CheckoutTableView> tableView;
	@FXML
	private TableColumn<CheckoutRecordEntry, String> iSBNcol;
	@FXML
	private TableColumn<CheckoutRecordEntry, String> iDcol;
	@FXML
	private TableColumn<CheckoutRecordEntry, LocalDate> date1col;
	@FXML
	private TableColumn<CheckoutRecordEntry, LocalDate> date2col;

	CheckoutRecordEntry entry;
	static DataAccessFacade data = new DataAccessFacade();
	static HashMap<String, LibraryMember> mapLibMembers = data.readMemberMap();

	public void checkoutBook(ActionEvent e) throws LoginException, Exception {
		String mID = memberID.getText();
		String mISBN = ISBN.getText();

		HashMap<String, Book> map1 = data.readBooksMap();
		if (mapLibMembers.containsKey(mID) && map1.containsKey(mISBN)) {
			boolean available = false;
			BookCopy[] b1 = map1.get(mISBN).getCopies();

			for (BookCopy b : b1) {
				if (b.isAvailable()) {
					available = true;
					b.changeAvailability();
					entry = new CheckoutRecordEntry(mID, b, LocalDate.now(), LocalDate.now().plusDays(21));
					LibraryMember libMem = mapLibMembers.get(mID);
					libMem.addEntry(entry);
					mapLibMembers.put(mID, libMem);
					b.changeAvailability();
					CheckoutTableView checkbookView = new CheckoutTableView(entry);
					break;
				}

			}
			if (!available)
				bookLabel.setText("copy of book not available");
		} else {
			bookLabel.setText("Member ID or book ISBN is invalid");
		}

	}

	public void addMemberEntry(ActionEvent e) throws LoginException, Exception {

		Stage memberStage = new Stage();
		((Node) e.getSource()).getScene().getWindow().hide();
		AddMember addMember = new AddMember();
		addMember.start(memberStage);
	}

	public void addCopyOfBookEntry(ActionEvent e) throws LoginException, Exception {
		Stage bookStage = new Stage();
		((Node) e.getSource()).getScene().getWindow().hide();
		AddCopyBook addBook = new AddCopyBook();
		addBook.start(bookStage);
	}

	@FXML
	private TextField firstName;
	@FXML
	private TextField lastName;
	@FXML
	private TextField phoneNumber;
	@FXML
	private TextField street;
	@FXML
	private TextField city;
	@FXML
	private TextField state;
	@FXML
	private TextField zip;
	@FXML
	private TextField libmemberId;

	public void addLibraryMember(ActionEvent e) throws LoginException, Exception {

		String fName = firstName.getText();
		String lName = lastName.getText();
		String tel = phoneNumber.getText();
		String str = street.getText();
		String ct = city.getText();
		String st = state.getText();
		String zp = zip.getText();
		String memId = libmemberId.getText();
		Address add = new Address(str, ct, st, zp);
		LibraryMember libMember = new LibraryMember(memId, fName, lName, tel, add);

		DataAccessFacade data = new DataAccessFacade();
		data.saveNewMember(libMember);
		libmemberId.setText("");
		firstName.setText("");
		lastName.setText("");
		phoneNumber.setText("");
		street.setText("");
		city.setText("");
		state.setText("");
		zip.setText("");
		JOptionPane.showMessageDialog(null, "A new library member added");

	}

	@FXML
	private TextField isbnId;

	@FXML
	private Label searchResult;
	Book book;

	public void searchBook(ActionEvent e) throws LoginException, Exception {
		String isbn = isbnId.getText();
		DataAccessFacade data = new DataAccessFacade();
		HashMap<String, Book> bookMap = data.readBooksMap();
		if (bookMap.get(isbn) != null) {
			book = bookMap.get(isbn);
			searchResult.setText(isbn);
		} else {
			searchResult.setText("invalid ISBN number");
		}

	}

	public void addCopyOfBook(ActionEvent e) throws LoginException, Exception {

		book.addCopy();
		JOptionPane.showMessageDialog(null, "A copy of book is added");
	}

	public void addLibraryMemBoth(ActionEvent e) throws LoginException, Exception {

		Stage bothStage = new Stage();
		((Node) e.getSource()).getScene().getWindow().hide();
		AddMemberBoth addBothMem = new AddMemberBoth();
		addBothMem.start(bothStage);
	}

	public void addCopyBookBoth(ActionEvent e) throws LoginException, Exception {

		Stage bookCopyStage = new Stage();
		((Node) e.getSource()).getScene().getWindow().hide();
		AddCopyBookBoth addCopyBookboth = new AddCopyBookBoth();
		addCopyBookboth.start(bookCopyStage);
	}

	public void signOut(ActionEvent e) throws LoginException, Exception {

		((Node) e.getSource()).getScene().getWindow().hide();
		Login login = new Login();
		Stage primaryStage = new Stage();
		login.start(primaryStage);
	}

	public void backtoHompage(ActionEvent e) throws LoginException, Exception {

		((Node) e.getSource()).getScene().getWindow().hide();
		Admin admin = new Admin();
		Stage primaryStage = new Stage();
		admin.start(primaryStage);
	}

	public void checkOutBookBoth(ActionEvent e) throws LoginException, Exception {

		Stage librarianStage = new Stage();
		((Node) e.getSource()).getScene().getWindow().hide();
		LibrarianBoth addmember = new LibrarianBoth();
		addmember.start(librarianStage);
	}

	public void backhomepageBoth(ActionEvent e) throws LoginException, Exception {
		((Node) e.getSource()).getScene().getWindow().hide();
		Both both = new Both();
		Stage primaryStage = new Stage();
		both.start(primaryStage);
	}

	public void addNewBook(ActionEvent e) throws LoginException, Exception {
		((Node) e.getSource()).getScene().getWindow().hide();
		AddBookToLibrary addBook = new AddBookToLibrary();
		Stage primaryStage = new Stage();
		addBook.start(primaryStage);
	}

	public void addAuthor(ActionEvent e) throws LoginException, Exception {

		Authors addAuthor = new Authors();
		Stage primaryStage = new Stage();
		addAuthor.start(primaryStage);
	}

	@FXML
	private TextField a_firstName;
	@FXML
	private TextField a_lastName;
	@FXML
	private TextField a_street;
	@FXML
	private TextField a_zip;
	@FXML
	private TextField a_state;
	@FXML
	private TextField a_city;
	@FXML
	private TextField a_telNumber;
	@FXML
	private TextField a_credential;
	@FXML
	private TextField a_bio;
	@FXML
	private List<Author> authors = new ArrayList<>();

	public void addAuthorsToList(ActionEvent e) throws LoginException, Exception {

		String fName = a_firstName.getText();
		String lName = a_lastName.getText();
		String street = a_street.getText();
		String zip = a_zip.getText();
		String state = a_state.getText();
		String city = a_city.getText();
		String tel = a_telNumber.getText();
		String cre = a_credential.getText();
		String bio = a_bio.getText();

		Address address = new Address(street, city, state, zip);
		Author author = new Author(fName, lName, tel, address, bio);
		authors.add(author);

		((Node) e.getSource()).getScene().getWindow().hide();
	}

	@FXML
	private TextField addBook_isbn;
	@FXML
	private TextField addBook_title;
	@FXML
	private TextField AddBook_checkLength;
	@FXML
	private TextField addBook_numCopies;

	private List<Book> bookList = new ArrayList<>();

	public void addBookToLibrary(ActionEvent e) throws LoginException, Exception {

		String isbn = addBook_isbn.getText();
		String title = addBook_isbn.getText();
		String length = AddBook_checkLength.getText();
		int checkoutLength = Integer.parseInt(length);

		Book newBook = new Book(isbn, title, checkoutLength, authors);
		bookList.add(newBook);
		DataAccessFacade data = new DataAccessFacade();

		HashMap<String, Book> books = data.readBooksMap();
		Set<String> keys = books.keySet();
		for(String s : keys){
			bookList.add(books.put(s, books.get(s)));
		}

		data.loadBookMap(bookList);

		JOptionPane.showMessageDialog(null, "A book is added to the library collection");
		addBook_numCopies.setText("");
		addBook_isbn.setText("");
		addBook_title.setText("");
		AddBook_checkLength.setText("");
		bookList.clear();

	}

	public void librariancheckout(ActionEvent e) throws LoginException, Exception {
		((Node) e.getSource()).getScene().getWindow().hide();
		Librarian lib = new Librarian();
		Stage primaryStage = new Stage();
		lib.start(primaryStage);
	}

	public void backLibrarianHomepage(ActionEvent e) throws LoginException, Exception {
		((Node) e.getSource()).getScene().getWindow().hide();
		LibrarianHomepage libHomepage = new LibrarianHomepage();
		Stage primaryStage = new Stage();
		libHomepage.start(primaryStage);
	}

	public void searchLibMember(ActionEvent e) throws LoginException, Exception {

		((Node) e.getSource()).getScene().getWindow().hide();
		Stage searMember = new Stage();
		SearchLibraryMember searLibMember = new SearchLibraryMember();
		searLibMember.start(searMember);

	}

	@FXML
	private TextField isbnInput;
	@FXML
	private Label searResult;
	LibraryMember member;

	public void searchMember(ActionEvent e) throws LoginException, Exception {

		String isbnNum = isbnInput.getText();

		if (mapLibMembers.containsKey(isbnNum)) {
			member = mapLibMembers.get(isbnNum);
			searResult.setText("Member found, print the checkout record");

		} else
			searResult.setText("Member not found, try again");

	}

	public void printToConsole(ActionEvent e) throws LoginException, Exception {

		List<CheckoutRecordEntry> checkOutrecords = member.getEntries();
		System.out.println("Member Id\t  ISBN\t     Checkout date\t due date");
		for (CheckoutRecordEntry entry : checkOutrecords) {

			System.out.println(entry.getMemberId() + "\t\t" + entry.getBookCopy().getBook().getIsbn() + "\t"
					+ entry.getChechoutDate() + "\t" + entry.getDueDate());
		}

	}

}
