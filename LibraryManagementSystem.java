package Computer_Programming_Lab_Project;

import java.util.Date;
import java.util.Scanner;

// book class
class Book {

    private String title;
    private String author;
    private int availableCopies;
    private int totalCopies;

    public Book(String title, String author, int totalCopies) {
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    // getting private things to other classes
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void addCopies(int copies) {
        this.totalCopies += copies;
        this.availableCopies += copies;
    }

    public String toString() {
        return "'" + title + "'" + " by " + author + " - available: " + availableCopies + " - total amount: " + totalCopies;
    }
}

// user class
class User {

    private String name;
    private String email;
    private Book[] borrowedBooks;
    private int borrowedCount;
    private static final int max_borrowed_book_amount = 3;

    public User(String name, String email) {
        
        this.name = name;
        this.email = email;
        this.borrowedBooks = new Book[max_borrowed_book_amount];
        this.borrowedCount = 0;
    }

    public String getEmail() {
        return email;
    }

    // we will call this method in the library class
    public boolean borrowBook(Book book) {
        
        if (borrowedCount < max_borrowed_book_amount) {
            borrowedBooks[borrowedCount++] = book;
            return true;
        }
        return false;
    }

    public boolean returnBook(Book book) {
        
        for (int i = 0; i < borrowedCount; i++) {
            
            if (borrowedBooks[i].equals(book)) {
                borrowedBooks[i] = borrowedBooks[--borrowedCount];
                borrowedBooks[borrowedCount] = null;
                return true;
            }
        }
        return false;
    }

    public Book[] getBorrowedBooks() {
        return borrowedBooks;
    }

    public String toString() {
        return name + " (" + email + ")";
    }
}

// transaction class
class Transaction {

    private User user;
    private Book book;
    private String transactionType;
    private Date transactionDate;

    public Transaction(User user, Book book, String transactionType) {
        this.user = user;
        this.book = book;
        this.transactionType = transactionType;
        this.transactionDate = new Date();
    }

    public String toString() {
        return transactionDate.toString() + " - " + transactionType + ": " + book.toString() + " by " + user.toString();
    }
}

// library class
class Library {

    private static final int max_shelf = 4;
    private Book[][] shelves;
    private User[] users;
    private Transaction[] transactions;
    private int userCount;
    private int transactionCount;
    private int shelfCount;

    public Library() {
        shelves = new Book[15][max_shelf];
        users = new User[150];
        transactions = new Transaction[1001];
        userCount = 0;
        transactionCount = 0;
        shelfCount = 0;
    }

    // expanding shelves when it is full
    private void addingNewShelves() {
        Book[][] newShelves = new Book[shelves.length + 1][max_shelf];
        for (int i = 0; i < shelves.length; i++) {
            for (int j = 0; j < shelves[i].length; j++) {
                newShelves[i][j] = shelves[i][j];
            }
        }
        shelves = newShelves;
    }

    // adding new book
    public boolean addBook(String title, String author, int totalCopies) {
        for (int i = 0; i < shelves.length; i++) {
            for (int j = 0; j < shelves[i].length; j++) {
                if (shelves[i][j] != null && shelves[i][j].getTitle().equals(title) && shelves[i][j].getAuthor().equals(author)) {
                    shelves[i][j].addCopies(totalCopies);
                    return true;
                }
            }
        }

        for (int i = 0; i < shelfCount; i++) {
            for (int j = 0; j < max_shelf; j++) {
                if (shelves[i][j] == null) {
                    shelves[i][j] = new Book(title, author, totalCopies);
                    return true;
                }
            }
        }

        if (shelfCount >= shelves.length) {
            addingNewShelves();
        }

        if (shelfCount < shelves.length) {
            shelves[shelfCount][0] = new Book(title, author, totalCopies);
            shelfCount++;
            return true;
        }

        return false;
    }

    // creating new user
    public boolean registerUser(String name, String email) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getEmail().equals(email)) {
                return false;
            }
        }
        users[userCount++] = new User(name, email);
        return true;
    }

    // borrowing a book
    public boolean borrowBook(String email, String title, String author) {
        User user = findUserByEmail(email);
        if (user == null) {
            return false;
        }

        Book book = findBook(title, author);
        if (book == null || book.getAvailableCopies() <= 0) {
            return false;
        }

        if (user.borrowBook(book)) {
            book.addCopies(-1); // decreasing 1 when book is borrowed
            transactions[transactionCount++] = new Transaction(user, book, "Borrow");
            return true;
        }

        return false;
    }

    // returning book if it is borrowed
    public boolean returnBook(String email, String title, String author) {
        User user = findUserByEmail(email);
        if (user == null) {
            return false;
        }

        Book book = findBook(title, author);
        if (book == null || !user.returnBook(book)) {
            return false;
        }

        book.addCopies(1); // increasimg the available copies by 1
        transactions[transactionCount++] = new Transaction(user, book, "Return");
        return true;
    }

    // displaying all books
    public void displayBooks() {
        for (int i = 0; i < shelves.length; i++) {
            for (int j = 0; j < shelves[i].length; j++) {
                if (shelves[i][j] != null) {
                    System.out.println(shelves[i][j].toString());
                }
            }
        }
    }

    // viewing all transactions
    public void viewTransactions() {
        for (int i = 0; i < transactionCount; i++) {
            System.out.println(transactions[i].toString());
        }
    }

    // finding user by email
    private User findUserByEmail(String email) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getEmail().equals(email)) {
                return users[i];
            }
        }
        return null;
    }

    // finding book by title and author
    private Book findBook(String title, String author) {
        for (int i = 0; i < shelves.length; i++) {
            for (int j = 0; j < shelves[i].length; j++) {
                if (shelves[i][j] != null && shelves[i][j].getTitle().equals(title) && shelves[i][j].getAuthor().equals(author)) {
                    return shelves[i][j];
                }
            }
        }
        return null;
    }
}

// main method
public class LibraryManagementSystem {

    public static void main(String[] args) {
        Library library = new Library();
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("Library Management System:");
            System.out.println("1. Add a New Book");
            System.out.println("2. Register a New User");
            System.out.println("3. Borrow a Book");
            System.out.println("4. Return a Book");
            System.out.println("5. Display all Books");
            System.out.println("6. View Transactions");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = input.nextInt();
            // going to new line
            input.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Book Title: ");
                    String title = input.nextLine();
                    System.out.print("Enter Author: ");
                    String author = input.nextLine();
                    System.out.print("Enter Total Copies: ");
                    int copies = input.nextInt();
                    input.nextLine();
                    if (library.addBook(title, author, copies)) {
                        System.out.println("Book added successfully.");
                    } else {
                        System.out.println("Failed to add book.");
                    }
                    break;
                case 2:
                    System.out.print("Enter User Name: ");
                    String name = input.nextLine();
                    System.out.print("Enter User Email: ");
                    String email = input.nextLine();
                    if (library.registerUser(name, email)) {
                        System.out.println("User registered.");
                    } else {
                        System.out.println("User already exists.");
                    }
                    break;
                case 3:
                    System.out.print("Enter User Email: ");
                    String borrowEmail = input.nextLine();
                    System.out.print("Enter Book Title: ");
                    String borrowTitle = input.nextLine();
                    System.out.print("Enter Author: ");
                    String borrowAuthor = input.nextLine();
                    if (library.borrowBook(borrowEmail, borrowTitle, borrowAuthor)) {
                        System.out.println("Book borrowed successfully.");
                    } else {
                        System.out.println("Failed to borrow book.");
                    }
                    break;
                case 4:
                    System.out.print("Enter User Email: ");
                    String returnEmail = input.nextLine();
                    System.out.print("Enter Book Title: ");
                    String returnTitle = input.nextLine();
                    System.out.print("Enter Author: ");
                    String returnAuthor = input.nextLine();
                    if (library.returnBook(returnEmail, returnTitle, returnAuthor)) {
                        System.out.println("Book returned successfully.");
                    } else {
                        System.out.println("Failed to return book.");
                    }
                    break;
                case 5:
                    System.out.println("Displaying books:");
                    library.displayBooks();
                    break;
                case 6:
                    System.out.println("Displaying transactions:");
                    library.viewTransactions();
                    break;
                case 7:
                    System.out.println("Program exited succesfully");
                    input.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
