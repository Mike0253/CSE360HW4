package application;

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The DatabaseHelper class is responsible for managing the connection to the
 * database, performing operations such as user registration, login validation,
 * and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static String DB_URL = "jdbc:h2:./FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	private Connection connection = null;
	private Statement statement = null;
	// PreparedStatement pstmt

	public DatabaseHelper() {
		try {
			connectToDatabase();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public DatabaseHelper(String inputDataBase) {
		DB_URL = inputDataBase;
		try {
			connectToDatabase();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public int ResetHard(String URL) {
		if (URL.equals(DB_URL)) {
			System.out.println("WARNING: Attempting to reset the database using DROP ALL OBJECTS...");
			try {
				// Use H2's specific command for dropping everything
				statement.execute("DROP ALL OBJECTS");
				System.out.println("Database reset successfully. All objects dropped.");
				createTables();
			} catch (SQLException e) {
				System.err.println("Error during database reset: " + e.getMessage());
			}
			return 0;
		}
		return 1;

	}

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " + "password VARCHAR(255), " + "role INT," + "reputation INT,"
				+ "uuid VARCHAR(36) UNIQUE," + "trustedReviewers VARCHAR(2048))";
		statement.execute(userTable);

		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes (" + "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE)";
		statement.execute(invitationCodesTable);

		// question
		String questionsTable = "CREATE TABLE IF NOT EXISTS Questions (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "uuid VARCHAR(36) UNIQUE, " // UUID for the question
				+ "uuidUSER VARCHAR(36) , " + "name VARCHAR(255), " + "title VARCHAR(255), "
				+ "textBody VARCHAR(2048), " + "date DATE, " + "chosenAnswer VARCHAR(36)," + "UnderReview BOOLEAN)";
		statement.execute(questionsTable);

		// answer
		String answersTable = "CREATE TABLE IF NOT EXISTS Answers (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "uuid VARCHAR(36) UNIQUE, " + "question_uuid VARCHAR(36), " + "uuidUSER VARCHAR(36), "
				+ "name VARCHAR(255)," + "UnderReview BOOLEAN," + "textBody VARCHAR(2048), " + "date DATE, "
				+ "upvotedBy VARCHAR(2048) DEFAULT NULL, " + "downvotedBy VARCHAR(2048) DEFAULT NULL)"; // Allow NULL
																										// values
		statement.execute(answersTable);

		// privateMessage
		String privateMessage = "CREATE TABLE IF NOT EXISTS privateMessage (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "uuid VARCHAR(36) UNIQUE, " // UUID for message
				+ "fromUUID VARCHAR(36), " // UUID for from user
				+ "toUUID VARCHAR(36), " // UUID for to user
				+ "reviewUUID VARCHAR(36), " // UUID for to response
				+ "textBody VARCHAR(2048), " + "date DATE )"; // sent date
		statement.execute(privateMessage);
		
		String reviewTable = "CREATE TABLE IF NOT EXISTS Review (" +
			    "id VARCHAR(36) PRIMARY KEY, " +
			    "reviewerId VARCHAR(36), " +
			    "questionId VARCHAR(36), " +
			    "answerId VARCHAR(36), " +
			    "content VARCHAR(2048), " +
			    "status VARCHAR(20), " +
			    "date DATE)";
		statement.execute(reviewTable);
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role, uuid, reputation, trustedReviewers) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setInt(3, user.getRoleInt());
			pstmt.setString(4, user.getID().toString());
			pstmt.setInt(5, user.getReputation()); // Initialize to
			String trustedString = user.getTrustedReviewers().isEmpty() ? null
					: String.join(",", user.getTrustedReviewers().stream().map(UUID::toString).toList());
			pstmt.setString(6, trustedString);
			pstmt.executeUpdate();
		}
	}

	// Registers a new reviewer in the database.
	public void register(Reviewer reviewer) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role, uuid,  reputation) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, reviewer.getUserName());
			pstmt.setString(2, reviewer.getPassword());
			pstmt.setInt(3, reviewer.getRoleInt());
			pstmt.setString(4, reviewer.getID().toString());
			pstmt.setInt(5, reviewer.getReputation());
			String trustedString = reviewer.getTrustedReviewers().isEmpty() ? null
					: String.join(",", reviewer.getTrustedReviewers().stream().map(UUID::toString).toList());
			pstmt.setString(9, trustedString);
			pstmt.executeUpdate();
		}
	}

	// Retrieves the UUID of a user from the database using their UserName.
	public UUID getUserUUID(String userName) {
		String query = "SELECT uuid FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return UUID.fromString(rs.getString("uuid")); // UUID from string
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // If no user exists or an error occurs
	}

	// Retrieves the reputation of a user from the database using their UserName.
	public int getReputationByUUID(UUID uuid) {
		String query = "SELECT reputation FROM cse360users WHERE uuid = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, uuid.toString());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("reputation"); // UUID from string
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0; // If no user exists or an error occurs
	}

	// Retrieves the reputation of a user from the database using their UserName.
	public List<UUID> getTrustedByUUID(UUID uuid) {
		String query = "SELECT trustedReviewers FROM cse360users WHERE uuid = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, uuid.toString());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String downvotedByStr = rs.getString("trustedReviewers");
				List<UUID> newList = new ArrayList<>();
				if (downvotedByStr != null && !downvotedByStr.isEmpty()) {
					newList = Arrays.stream(downvotedByStr.split(",")).map(UUID::fromString)
							.collect(Collectors.toList());
				}
				return newList; // UUID from string
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>(); // If no user exists or an error occurs
	}

	public int setTrustedByUUID(UUID uuid, List<UUID> trustedReviewers) {
		String query = "UPDATE cse360users SET trustedReviewers = ? WHERE uuid = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			String trustedReviewersStr = null; // Default to null if the list is empty or null
			if (trustedReviewers != null && !trustedReviewers.isEmpty()) {
				trustedReviewersStr = trustedReviewers.stream().map(UUID::toString).collect(Collectors.joining(","));
			}

			pstmt.setString(1, trustedReviewersStr); // Set to null or comma-separated string
			pstmt.setString(2, uuid.toString());

			return pstmt.executeUpdate(); // Returns the number of rows updated (1 if successful, 0 if user not found)

		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // Indicate failure
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
//			pstmt.setInt(3, user.getRoleInt());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}

	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	// Retrieves the role of a user from the database using their UserName.
	public int getUserRole(String userName) {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("role"); // Return the role if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0; // If no user exists or an error occurs
	}

	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
		String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
		String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return code;
	}

	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// Mark the code as used
				markInvitationCodeAsUsed(code);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
		String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//=============================================
// new code for question
//=============================================

	public void saveQuestions(Questions questionsList) throws SQLException {
		// Delete all existing questions from the table
		String deleteQuestionsQuery = "DELETE FROM Questions";
		try (PreparedStatement deletePstmt = connection.prepareStatement(deleteQuestionsQuery)) {
			deletePstmt.executeUpdate();
			System.out.println("Existing questions deleted from database.");
		} catch (SQLException e) {
			System.err.println("Error deleting existing questions: " + e.getMessage());
			throw e; // Re-throw the exception to indicate failure
		}
		for (int i = 0; i < questionsList.getSize(); i++) {
			saveQuestion(questionsList.get(i)); // Save each question individually
		}
		System.out.println("Questions saved to database."); // Confirmation message
	}

	public void saveQuestion(Question question) {
		String query = "INSERT INTO Questions (uuid, name, title, textBody, date, chosenAnswer, uuidUSER, UnderReview) VALUES (?,?,?,?,?,?,?,?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, question.getID().toString());
			pstmt.setString(2, question.getName());
			pstmt.setString(3, question.getTitle());
			pstmt.setString(4, question.getTextBody());
			pstmt.setDate(5, Date.valueOf(question.getDate()));
			pstmt.setString(6, question.getChosenAnswer() != null ? question.getChosenAnswer().toString() : null);
			pstmt.setString(7, question.getUserUUID().toString());
			pstmt.setBoolean(8, question.getUnderReview());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Questions loadQuestions() throws SQLException {
		Questions questionsList = new Questions();
		String query = "SELECT * FROM Questions";
		try (PreparedStatement pstmt = connection.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Question q = new Question();
				q.setID(UUID.fromString(rs.getString("uuid"))); // UUID from string
				q.setName(rs.getString("name"));
				q.setTitle(rs.getString("title"));
				q.setTextBody(rs.getString("textBody"));
				q.setDate(rs.getDate("date").toLocalDate()); // Convert Date to LocalDate
				q.setChosenAnswer(
						rs.getString("chosenAnswer") != null ? UUID.fromString(rs.getString("chosenAnswer")) : null);
				q.setUserUUID(UUID.fromString(rs.getString("uuidUSER"))); // UUID from string
				q.setUnderReview(rs.getBoolean("UnderReview"));
				questionsList.addQuestion(q);
			}
		}
		return questionsList;
	}

	// =============================================
	// code for answer
	// =============================================
	public void saveAnswers(Answers answersList) throws SQLException {
		// Delete all existing questions from the table
		String deleteQuestionsQuery = "DELETE FROM Answers";
		try (PreparedStatement deletePstmt = connection.prepareStatement(deleteQuestionsQuery)) {
			deletePstmt.executeUpdate();
			System.out.println("Existing questions deleted from database.");
		} catch (SQLException e) {
			System.err.println("Error deleting existing questions: " + e.getMessage());
			throw e; // Re-throw the exception to indicate failure
		}
		for (int i = 0; i < answersList.getSize(); i++) {
			saveAnswer(answersList.get(i)); // Save each question individually
		}
		System.out.println("Questions saved to database."); // Confirmation message
	}

	public void saveAnswer(Answer answer) {
		String query = "INSERT INTO Answers (uuid, question_uuid, name, textBody, date, uuidUSER, UnderReview, upvotedBy, downvotedBy) VALUES (?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answer.getID().toString());
			pstmt.setString(2, answer.getQuestionID().toString());
			pstmt.setString(3, answer.getName());
			pstmt.setString(4, answer.getTextBody());
			pstmt.setDate(5, Date.valueOf(answer.getDate()));
			pstmt.setString(6, answer.getUserUUID().toString());
			pstmt.setBoolean(7, answer.getUnderReview());
			pstmt.setBoolean(7, answer.getUnderReview());

			// Handle empty lists by setting NULL
			String upvotedByStr = answer.getUpvotedBy().isEmpty() ? null
					: String.join(",", answer.getUpvotedBy().stream().map(UUID::toString).toList());
			String downvotedByStr = answer.getDownvotedBy().isEmpty() ? null
					: String.join(",", answer.getDownvotedBy().stream().map(UUID::toString).toList());

			pstmt.setString(8, upvotedByStr);
			pstmt.setString(9, downvotedByStr);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Answers loadAnswer() throws SQLException {
		Answers answersList = new Answers();
		String query = "SELECT * FROM Answers";
		try (PreparedStatement pstmt = connection.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Answer ans = new Answer();
				ans.setID(UUID.fromString(rs.getString("uuid")));
				ans.setQuestionID(UUID.fromString(rs.getString("question_uuid")));
				ans.setName(rs.getString("name"));
				ans.setTextBody(rs.getString("textBody"));
				ans.setDate(rs.getDate("date").toLocalDate());
				ans.setUserUUID(UUID.fromString(rs.getString("uuidUSER")));
				ans.setUnderReview(rs.getBoolean("UnderReview"));
				

				// Load upvotedBy (handle NULL)
				String upvotedByStr = rs.getString("upvotedBy");
				if (upvotedByStr != null && !upvotedByStr.isEmpty()) {
					ans.setUpvotedBy(
							Arrays.stream(upvotedByStr.split(",")).map(UUID::fromString).collect(Collectors.toList()));
				} else {
					ans.setUpvotedBy(new ArrayList<>()); // Initialize empty list
				}

				// Load downvotedBy (handle NULL)
				String downvotedByStr = rs.getString("downvotedBy");
				if (downvotedByStr != null && !downvotedByStr.isEmpty()) {
					ans.setDownvotedBy(Arrays.stream(downvotedByStr.split(",")).map(UUID::fromString)
							.collect(Collectors.toList()));
				} else {
					ans.setDownvotedBy(new ArrayList<>()); // Initialize empty list
				}

				answersList.addAnswer(ans);
			}
		}
		return answersList;
	}

	public int updateReputation(UUID userUuid, int change) {
		String selectSql = "SELECT reputation FROM cse360users WHERE uuid = ?";
		String updateSql = "UPDATE cse360users SET reputation = ? WHERE uuid = ?";
		try (PreparedStatement selectStatement = connection.prepareStatement(selectSql);
				PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

			// 1. Get the current reputation from the database
			selectStatement.setString(1, userUuid.toString());
			ResultSet resultSet = selectStatement.executeQuery();

			if (!resultSet.next()) {
				System.err.println("Reputation not found for UUID: " + userUuid);
				return -1; // Indicate failure
			}

			int currentReputation = resultSet.getInt("reputation");

			// 2. Calculate the new reputation
			int newReputation = currentReputation + change;

			// 3. Save the new reputation to the database
			updateStatement.setInt(1, newReputation);
			updateStatement.setString(2, userUuid.toString());

			int affectedRows = updateStatement.executeUpdate();

			if (affectedRows > 0) {
				return newReputation;
			} else {
				System.err.println("Failed to update reputation for UUID: " + userUuid);
				return -1; // Indicate failure
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // Indicate failure
		}
	}

	public int getReputation(UUID userUuid) {
		String sql = "SELECT reputation FROM cse360users WHERE uuid = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, userUuid.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getInt("reputation");
			} else {
				System.err.println("Reputation not found for UUID: " + userUuid);
				return -1; // Or throw an exception, depending on your error handling strategy
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // Or throw an exception
		}
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	// =============================================
	// code for privateMessage
	// =============================================

	// Make a new message
	public int saveMessage(privateMessage message) {
		String sql = "INSERT INTO privateMessage (uuid, fromUUID, toUUID, reviewUUID, textBody, date) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, message.getID().toString());
			pstmt.setString(2, message.getFromUUID().toString());
			pstmt.setString(3, message.getToUUID().toString());
			pstmt.setString(4, message.getReviewUUID().toString());
			pstmt.setString(5, message.getTextBody());
			pstmt.setDate(6, Date.valueOf(message.getDate()));

			return pstmt.executeUpdate(); // Returns the number of rows affected. Should be 1 if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // Indicate failure
		}
	}

	// Get all messages from a specific user
	public privateMessageList getMessagesFromUser(UUID fromUUID) {
		privateMessageList messageList = new privateMessageList();
		String sql = "SELECT * FROM privateMessage WHERE fromUUID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, fromUUID.toString());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				UUID toUUID = UUID.fromString(rs.getString("toUUID"));
				UUID from = UUID.fromString(rs.getString("fromUUID"));
				UUID review = UUID.fromString(rs.getString("reviewUUID"));
				String textBody = rs.getString("textBody");
				LocalDate date = rs.getDate("date").toLocalDate();
				privateMessage message = new privateMessage(uuid, textBody, date, from, toUUID, review);
				messageList.addPrivateMessage(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messageList;
	}

	// Get all messages to a specific user
	public privateMessageList getMessagesToUser(UUID toUUID) {
		privateMessageList messageList = new privateMessageList();
		String sql = "SELECT * FROM privateMessage WHERE toUUID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, toUUID.toString());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				UUID to = UUID.fromString(rs.getString("toUUID"));
				UUID fromUUID = UUID.fromString(rs.getString("fromUUID"));
				UUID review = UUID.fromString(rs.getString("reviewUUID"));
				String textBody = rs.getString("textBody");
				LocalDate date = rs.getDate("date").toLocalDate();
				privateMessage message = new privateMessage(uuid, textBody, date, fromUUID, to, review);
				messageList.addPrivateMessage(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messageList;
	}

	// Get all messages that either include user from or to
	public privateMessageList getMessagesIncludingUser(UUID userUUID) {
		privateMessageList messageList = new privateMessageList();
		String sql = "SELECT * FROM privateMessage WHERE toUUID = ? OR fromUUID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, userUUID.toString());
			pstmt.setString(2, userUUID.toString());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				UUID to = UUID.fromString(rs.getString("toUUID"));
				UUID fromUUID = UUID.fromString(rs.getString("fromUUID"));
				UUID review = UUID.fromString(rs.getString("reviewUUID"));
				String textBody = rs.getString("textBody");
				LocalDate date = rs.getDate("date").toLocalDate();
				privateMessage message = new privateMessage(uuid, textBody, date, fromUUID, to, review);
				messageList.addPrivateMessage(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messageList;
	}

	// Update the text body of a message
	public int updateMessage(UUID uuid, String newTextBody) {
		String sql = "UPDATE privateMessage SET textBody = ? WHERE uuid = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, newTextBody);
			pstmt.setString(2, uuid.toString());

			return pstmt.executeUpdate(); // Returns the number of rows affected. Should be 1 if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // Indicate failure
		}
	}

	// Delete a message
	public int deleteMessage(UUID uuid) {
		String sql = "DELETE FROM privateMessage WHERE uuid = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, uuid.toString());

			return pstmt.executeUpdate(); // Returns the number of rows affected. Should be 1 if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // Indicate failure
		}
	}

	// Get all messages from the database
	public privateMessageList getAllMessages() {
		privateMessageList messageList = new privateMessageList();
		String sql = "SELECT * FROM privateMessage";
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				UUID toUUID = UUID.fromString(rs.getString("toUUID"));
				UUID fromUUID = UUID.fromString(rs.getString("fromUUID"));
				UUID review = UUID.fromString(rs.getString("reviewUUID"));
				String textBody = rs.getString("textBody");
				LocalDate date = rs.getDate("date").toLocalDate();
				privateMessage message = new privateMessage(uuid, textBody, date, fromUUID, toUUID, review);
				messageList.addPrivateMessage(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messageList;
	}

	public privateMessageList getCommunicationBetweenUsers(UUID user1UUID, UUID user2UUID) {
		privateMessageList messageList = new privateMessageList();
		String sql = "SELECT * FROM privateMessage "
				+ "WHERE (fromUUID = ? AND toUUID = ?) OR (fromUUID = ? AND toUUID = ?) " + "ORDER BY date";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, user1UUID.toString());
			pstmt.setString(2, user2UUID.toString());
			pstmt.setString(3, user2UUID.toString());
			pstmt.setString(4, user1UUID.toString());

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				UUID toUUID = UUID.fromString(rs.getString("toUUID"));
				UUID fromUUID = UUID.fromString(rs.getString("fromUUID"));
				UUID review = UUID.fromString(rs.getString("reviewUUID"));
				String textBody = rs.getString("textBody");
				LocalDate date = rs.getDate("date").toLocalDate();
				privateMessage message = new privateMessage(uuid, textBody, date, fromUUID, toUUID, review);
				messageList.addPrivateMessage(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messageList;
	}

	/**
	 * Retrieves a list of unique UUIDs of users that 'otherUUID' has communicated
	 * with (either sent or received messages).
	 */
	public List<UUID> getContactListUUID(UUID otherUUID) {
		List<UUID> contactList = new ArrayList<>();
		String sql = "SELECT DISTINCT " + "CASE " + "   WHEN fromUUID = ? THEN toUUID " + "   ELSE fromUUID "
				+ "END AS contactUUID " + "FROM privateMessage " + "WHERE fromUUID = ? OR toUUID = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, otherUUID.toString());
			pstmt.setString(2, otherUUID.toString());
			pstmt.setString(3, otherUUID.toString());

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String contactUUIDString = rs.getString("contactUUID");
				if (contactUUIDString != null) { // Prevent NullPointerException
					UUID contactUUID = UUID.fromString(contactUUIDString);
					if (!contactList.contains(contactUUID) && !contactUUID.equals(otherUUID)) { // Avoid Duplicates and
																								// self
						contactList.add(contactUUID);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace(); // Consider more robust error handling (logging, throwing exception)
		}

		return contactList;
	}

	// Retrieves the reputation of a user from the database using their UserName.
	public privateMessage getMessageByUUID(UUID searchUUID) {
		String query = "SELECT * FROM privateMessage WHERE uuid = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, searchUUID.toString());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				UUID toUUID = UUID.fromString(rs.getString("toUUID"));
				UUID fromUUID = UUID.fromString(rs.getString("fromUUID"));
				UUID review = UUID.fromString(rs.getString("reviewUUID"));
				String textBody = rs.getString("textBody");
				LocalDate date = rs.getDate("date").toLocalDate();
				privateMessage message = new privateMessage(uuid, textBody, date, fromUUID, toUUID, review);
				return message;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Retrieves the reputation of a user from the database using their UserName.
	public privateMessageList getMessageByResponceUUID(UUID searchUUID) {
		privateMessageList messageList = new privateMessageList();
		String query = "SELECT * FROM privateMessage WHERE reviewUUID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, searchUUID.toString());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				UUID toUUID = UUID.fromString(rs.getString("toUUID"));
				UUID fromUUID = UUID.fromString(rs.getString("fromUUID"));
				UUID review = UUID.fromString(rs.getString("reviewUUID"));
				String textBody = rs.getString("textBody");
				LocalDate date = rs.getDate("date").toLocalDate();
				privateMessage message = new privateMessage(uuid, textBody, date, fromUUID, toUUID, review);
				messageList.addPrivateMessage(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messageList;

	}

	public List<String> getcontactListString(UUID otherUUID) {
		List<String> usernames = new ArrayList<>();
		List<UUID> contactList = getContactListUUID(otherUUID); // Use the existing getContactList function

		if (contactList == null || contactList.isEmpty()) {
			return usernames; // Return empty list if contactList is null or empty
		}

		for (UUID contactUUID : contactList) {
			String sql = "SELECT userName FROM cse360users WHERE uuid = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, contactUUID.toString());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					usernames.add("userName");
				}
			} catch (SQLException e) {
				e.printStackTrace(); // Log the error
			}
		}
		return usernames;
	}
	
	public Review getReviewByAnswerId(UUID answerId) {
	    String query = "SELECT * FROM Review WHERE answerId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, answerId.toString());
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return new Review(
	                UUID.fromString(rs.getString("reviewerId")),
	                UUID.fromString(rs.getString("questionId")),
	                UUID.fromString(rs.getString("answerId")),
	                rs.getString("content")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public void saveReview(Review review) throws SQLException {
	    String query = "INSERT INTO Review (id, reviewerId, questionId, answerId, content, status, date) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, review.getId().toString());
	        pstmt.setString(2, review.getReviewerId().toString());
	        pstmt.setString(3, review.getQuestionId() != null ? review.getQuestionId().toString() : null);
	        pstmt.setString(4, review.getAnswerId() != null ? review.getAnswerId().toString() : null);
	        pstmt.setString(5, review.getContent());
	        pstmt.setString(6, review.getStatus().name());
	        pstmt.setDate(7, Date.valueOf(review.getDate()));
	        pstmt.executeUpdate();
	    }
	}

	public List<Review> loadReviews() throws SQLException {
	    List<Review> reviews = new ArrayList<>();
	    String query = "SELECT * FROM Review";
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            Review review = new Review(
	                UUID.fromString(rs.getString("reviewerId")),
	                UUID.fromString(rs.getString("questionId")),
	                UUID.fromString(rs.getString("answerId")),
	                rs.getString("content")
	            );
	            review.setId(UUID.fromString(rs.getString("id")));
	            review.setStatus(Review.ReviewStatus.valueOf(rs.getString("status")));
	            review.setDate(rs.getDate("date").toLocalDate());
	            reviews.add(review);
	        }
	    }
	    return reviews;
	}
	
	public List<Review> getReviewsByQuestionId(UUID questionId) throws SQLException {
	    String query = "SELECT * FROM Review WHERE questionId = ?";
	    return executeReviewQuery(query, questionId.toString());
	}

	public List<Review> getReviewsByAnswerId(UUID answerId) throws SQLException {
	    String query = "SELECT * FROM Review WHERE answerId = ?";
	    return executeReviewQuery(query, answerId.toString());
	}

	private List<Review> executeReviewQuery(String query, String id) throws SQLException {
	    List<Review> reviews = new ArrayList<>();
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, id);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            Review review = new Review(
	                UUID.fromString(rs.getString("reviewerId")),
	                rs.getString("questionId") != null ? UUID.fromString(rs.getString("questionId")) : null,
	                rs.getString("answerId") != null ? UUID.fromString(rs.getString("answerId")) : null,
	                rs.getString("content")
	            );
	            review.setId(UUID.fromString(rs.getString("id")));
	            review.setStatus(Review.ReviewStatus.valueOf(rs.getString("status")));
	            review.setDate(rs.getDate("date").toLocalDate());
	            reviews.add(review);
	        }
	    }
	    return reviews;
	}

}
