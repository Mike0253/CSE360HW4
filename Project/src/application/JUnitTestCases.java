package application;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

class JUnitTestCases {
	static final String DATABASE_URL = "jdbc:h2:./TestJunitDatabase";
	static DatabaseHelper helper;
//	static DatabaseHelper helper = new DatabaseHelper();
	static Questions questions = new Questions();
	static Answers answers = new Answers();
	static Question question = new Question();
	static Question newQuestion = new Question();
	static Answer answer = new Answer();
	static Answer secondAnswer = new Answer();

	// private messaging users
	static User user1;
	static User user2;
	static User user3;

	@BeforeAll
	static void setupPM() throws SQLException {
		helper = new DatabaseHelper(DATABASE_URL);
		helper.ResetHard(DATABASE_URL);
		try {
//			helper.createTables();
//		 Create test users
			user1 = makeNewUsers("testuser1", "PasswordPM1!", "student");
			user2 = makeNewUsers("testuser2", "PasswordPM2!", "student");
			user3 = makeNewUsers("testuser3", "PasswordPM3!", "reviewer");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	static User makeNewUsers(String username, String password, String role) throws SQLException {
		User user;
		user = new User(username, password, role);
		helper.register(user); // Register first to get it in the DB
		return user;
	}

	/**
	 * Connects to the SQL database; throws if unable
	 * 
	 * @throws SQLException
	 */
	@Test
	@Order(1)
	void loadDatabase() throws SQLException {
		helper.connectToDatabase();
		questions = helper.loadQuestions();
	}

	/**
	 * Creates a new question and checks that it is properly named
	 */
	@Test
	@Order(2)
	void basicQuestionCreation() {
		question = new Question("Test Name", "Test Title", "Test Text", null);
		questions.addQuestion(question);
		if (!(question.getName().equals("Test Name")))
			fail();
	}

	/**
	 * Creates a new question with a unicode character in the title and checks that
	 * it is properly named
	 */
	@Test
	@Order(3)
	void storeQuestionWithSpecialCharacters() {
		newQuestion = new Question("ΔMario", "ΔTest title", "test text", null);
		questions.addQuestion(newQuestion);
		if (!newQuestion.getName().equals("ΔMario"))
			fail();
	}

	/**
	 * Posts an answer under a previously created question
	 */
	@Test
	@Order(4)
	void replyToQuestion() {
		answer = new Answer("Answer Name", "Answer Text", question.getID(), UUID.randomUUID());
		answers.addAnswer(answer);
		if (answer.getID() == null)
			fail();
	}

	/**
	 * Checks that multiple child answers can be accessed from one parent question
	 */
	@Test
	@Order(5)
	void multipleAnswersUnderSingleQuestion() {
		basicQuestionCreation();
		replyToQuestion();
		secondAnswer = new Answer("Another Answer", "Answer Text 2", question.getID(), UUID.randomUUID());
		answers.addAnswer(secondAnswer);
		int numAnswers = answers.getAnswersByUUID(question.getID()).size();
		if (numAnswers != 2)
			fail();
	}

	/**
	 * Accesses a question given its UUID
	 */
	@Test
	@Order(6)
	void accessQuestionByUUID() {
		Question result = questions.getByUUID(question.getID());
		if (result == null)
			fail();
	}

	/**
	 * Accesses an answer given its UUID
	 */
	@Test
	@Order(7)
	void accessAnswerByUUID() {
		Answer answerResult = answers.getByUUID(answer.getID());
		if (answerResult == null)
			fail();
	}

	/**
	 * Checks that the creation date of a previously created question is not null
	 */
	@Test
	@Order(8)
	void readQuestionDataPosted() {
		if (question.getDate() == null)
			fail();
	}

	/**
	 * Ensures that a question without a solution returns null when queried
	 */
	@Test
	@Order(9)
	void seeCurrentEmptySolution() {
		if (question.getChosenAnswer() != null)
			fail();
	}

	/**
	 * Marks an answer as its parent question's solution
	 */
	@Test
	@Order(10)
	void chooseAnswerAsSolution() {
		basicQuestionCreation();
		replyToQuestion();
		question.setChosenAnswer(answer.getID());
		if (question.getChosenAnswer() == null)
			fail();
	}

	/**
	 * Queries for an answered question's current chosen solution
	 */
	@Test
	@Order(11)
	void seeCurrentSolution() {
		basicQuestionCreation();
		replyToQuestion();
		chooseAnswerAsSolution();
		Answer currentSolution = answers.getByUUID(question.getChosenAnswer());
		if (currentSolution == null)
			fail();
	}

	/**
	 * Changes the creation date of a previously posted question
	 */
	@Test
	@Order(12)
	void changeDateOnQuestion() {
		LocalDate date = LocalDate.ofYearDay(1987, 317);
		question.setDate(date);
		if (question.getDate() != date)
			fail();
	}

	/**
	 * Changes the title of a previously posted question
	 */
	@Test
	@Order(13)
	void changePostTitle() {
		String newTitle = "New Title";
		question.setTitle(newTitle);
		if (question.getTitle() != newTitle)
			fail();
	}

	/**
	 * Searches questions with a query known to produce results
	 */
	@Test
	@Order(14)
	void searchQuestionsWithResults() {
		String newTitle = "New Title";
		List<UUID> results = questions.search(newTitle);
		if (results.size() < 1)
			fail();
	}

	/**
	 * Searches answers with a query known to produce results
	 */
	@Test
	@Order(15)
	void searchAnswersWithResults() {
		List<UUID> ansResults = answers.search(answer.getTextBody());
		if (ansResults.size() < 1)
			fail();
	}

	// 16
	/**
	 * Searches questions with a query known to produce no results
	 */
	@Test
	@Order(16)
	@DisplayName("Questions: Search through database not find")
	void searchQuestionsWithGibberish() {
		helper.ResetHard(DATABASE_URL);
		Question question1 = new Question("User1", "Title q1", "this is a question for q1", user1.getID());
		Question question2 = new Question("Gorge", "Title q2", "", user2.getID());
		Question question3 = new Question("Japan", "Title q3", "this is a substring of the search jaslkn",
				user3.getID());
		Questions questions = new Questions();
		questions.addQuestion(question1);
		questions.addQuestion(question2);
		questions.addQuestion(question3);
		assertEquals(questions.search("alsdkfjaslknv").size(), 0);
		Question question4 = new Question("Japan", "Title q5", "this is the search string alsdkfjaslknv srounded",
				user3.getID());
		questions.addQuestion(question4);
		assertEquals(questions.search("alsdkfjaslknv").size(), 1);
		Question question5 = new Question("Japan", "alsdkfjaslknv", "not here this time", user3.getID());
		questions.addQuestion(question5);
		assertEquals(questions.search("alsdkfjaslknv").size(), 2);
	}

	// 17
	/**
	 * Searches answers with a query known to produce no results
	 */
	@Test
	@Order(17)
	@DisplayName("Answers: Search through database not find")
	void searchAnswersWithGibberish() {
		helper.ResetHard(DATABASE_URL);
		Question quesion = new Question("User1", "Title q1", "this is a question for q1", user1.getID());
		Answer answer1 = new Answer("User1", "this is a question for q1", quesion.getID(), user1.getID());
		Answer answer2 = new Answer("HE", "", quesion.getID(), user2.getID());
		Answer answer3 = new Answer("USA", "this is a substring of the search jaslkn", quesion.getID(), user3.getID());
		Answers answers = new Answers();
		answers.addAnswer(answer1);
		answers.addAnswer(answer2);
		answers.addAnswer(answer3);
		assertEquals(answers.search("alsdkfjaslknv").size(), 0);
		Answer answer4 = new Answer("Japan", "this is the search string alsdkfjaslknv srounded", quesion.getID(),
				user3.getID());
		answers.addAnswer(answer4);
		assertEquals(answers.search("alsdkfjaslknv").size(), 1);
	}

	// 18
	/**
	 * Deletes an answer
	 */
	@Test
	@Order(18)
	@DisplayName("Answers: Delete an ordinary answer")
	void deleteOrdinaryAnswer() {
		helper.ResetHard(DATABASE_URL);
		Questions questions = new Questions();
		Answers answers = new Answers();

		Question question1 = new Question("User1", "Title q1", "This is a question", user1.getID());
		questions.addQuestion(question1);
		Answer answer1 = new Answer("User2", "Answer Text 1", question1.getID(), user2.getID());
		Answer answer2 = new Answer("User3", "Answer Text 2", question1.getID(), user3.getID());
		answers.addAnswer(answer1);
		answers.addAnswer(answer2);

		int initialSize = answers.getSize();
		answers.removeByUUID(answer2.getID());

		assertEquals(initialSize - 1, answers.getSize(), "Size 1 smaller after deletion");
		assertNull(answers.getByUUID(answer2.getID()), "Answer should no longer exist after deletion");
	}

	// 19
	/**
	 * Deletes an answer which is the solution to a question
	 */
	@Test
	@Order(19)
	@DisplayName("Answers: Delete a current solution")
	void deleteCurrentSolution() {
		helper.ResetHard(DATABASE_URL);
		Questions questions = new Questions();
		Answers answers = new Answers();

		Question question1 = new Question("User1", "Title q1", "This is a question", user1.getID());
		questions.addQuestion(question1);
		Answer answer1 = new Answer("User2", "Answer Text 1", question1.getID(), user2.getID());
		answers.addAnswer(answer1);
		question1.setChosenAnswer(answer1.getID()); // Set answer as solution

		int initialSize = answers.getSize();
		question1.setChosenAnswer(null);
		answers.removeByUUID(answer1.getID());

		assertEquals(initialSize - 1, answers.getSize(), "Size 1 smaller after deletion");
		assertNull(answers.getByUUID(answer1.getID()), "Answer should not exist");
		assertNull(question1.getChosenAnswer(), "Question's chosen answer should be null");

	}

	// 20
	/**
	 * Deletes a question with no answers
	 */
	@Test
	@Order(20)
	@DisplayName("Questions: Delete a childless question")
	void deleteChildlessQuestion() {
		helper.ResetHard(DATABASE_URL);
		Questions questions = new Questions();

		Question question1 = new Question("User1", "Title q1", "This is a question", user1.getID());
		questions.addQuestion(question1);

		int initialSize = questions.getSize();
		questions.removeByUUID(question1.getID());

		assertEquals(initialSize - 1, questions.getSize(), "Size 1 smaller after deletion");
		assertNull(questions.getByUUID(question1.getID()), "Question should no longer exist");
	}

	// 21
	/**
	 * Deletes a question with answers as children
	 */
	@Test
	@Order(21)
	@DisplayName("Questions: Delete question with answers and chosen answer")
	void deleteQuestionWithAnswers() {
		helper.ResetHard(DATABASE_URL);
		Questions questions = new Questions();
		Answers answers = new Answers();

		Question question1 = new Question("User1", "Title q1", "This is a question", user1.getID());
		Answer answer1 = new Answer("User2", "Answer Text 1", question1.getID(), user2.getID());
		Answer answer2 = new Answer("User3", "Answer Text 2", question1.getID(), user3.getID());

		questions.addQuestion(question1);
		answers.addAnswer(answer1);
		answers.addAnswer(answer2);

		question1.setChosenAnswer(answer1.getID());

		int initialQuestionSize = questions.getSize();
		int initialAnswerSize = answers.getSize();

		questions.removeByUUID(question1.getID()); // Delete the question

		answers.removeByUUID(answer1.getID());
		answers.removeByUUID(answer2.getID());

		assertEquals(initialQuestionSize - 1, questions.getSize(), "Questions size should decrement");
		assertNull(questions.getByUUID(question1.getID()), "Question should be null");

		// Check answers, all answers to question 1 should also be deleted.
		List<Answer> remainingAnswers = answers.getAnswerArray();
		assertEquals(remainingAnswers.size(), 0, "There should be no answers left");
	}

	// 22
	/*
	 * Create and Add Private Message to List
	 */
	@Test
	@Order(22)
	@DisplayName("PM: Create and Add Private Message to List")
	void testCreateAndAddToList() {
		helper.ResetHard(DATABASE_URL);
		privateMessageList pmList = new privateMessageList();
		assertEquals(0, pmList.getSize(), "Initial list should be empty");

		// Use the users created in @BeforeAll
		privateMessage msg = new privateMessage("Hello User 2!", user1.getID(), user2.getID(), UUID.randomUUID());

		assertNotNull(msg.getID(), "Message UUID should not be null");
		assertEquals("Hello User 2!", msg.getTextBody());
		assertEquals(user1.getID(), msg.getFromUUID());
		assertEquals(user2.getID(), msg.getToUUID());

		pmList.addPrivateMessage(msg);
		assertEquals(1, pmList.getSize(), "List size should be 1 after adding");
		assertSame(msg, pmList.get(0), "Retrieved message should be the same object");
		assertSame(msg, pmList.getByUUID(msg.getID()), "Retrieved message by UUID should be the same object");
	}

	// 23
	/*
	 * Save Private Message to Database
	 */
	@Test
	@Order(23)
	@DisplayName("PM: Save Private Message to Database")
	void testSaveMessageToDatabase() throws SQLException {
		helper.ResetHard(DATABASE_URL);
		// Use the users created in @BeforeAll
		privateMessage msg = new privateMessage("Database Save Test", user1.getID(), user2.getID(), UUID.randomUUID());

		int result = helper.saveMessage(msg);
		assertEquals(1, result, "saveMessage should return 1 on successful insert");

		// Verify by retrieving
		privateMessageList retrievedList = helper.getMessagesIncludingUser(user1.getID());
		assertEquals(1, retrievedList.getSize(), "Should retrieve 1 message for user1");
		privateMessage retrievedMsg = retrievedList.get(0);
		assertEquals(msg.getID(), retrievedMsg.getID());
		assertEquals(msg.getTextBody(), retrievedMsg.getTextBody());
		assertEquals(msg.getFromUUID(), retrievedMsg.getFromUUID());
		assertEquals(msg.getToUUID(), retrievedMsg.getToUUID());
		assertEquals(msg.getReviewUUID(), retrievedMsg.getReviewUUID());
		assertEquals(msg.getDate(), retrievedMsg.getDate());

	}

	// 24
	/*
	 * PM: Save and load from database and search
	 */
	@Test
	@Order(24)
	@DisplayName("PM: Save and load from database and search")
	void testLoadAndSearchMessages() throws SQLException {
		helper.ResetHard(DATABASE_URL);
		// Make questions then save
		privateMessage msg1_2 = new privateMessage("User1 to User2", user1.getID(), user2.getID(), UUID.randomUUID());
		privateMessage msg2_1 = new privateMessage("User2 to User1", user2.getID(), user1.getID(), UUID.randomUUID());
		privateMessage msg1_3 = new privateMessage("User1 to User3", user1.getID(), user3.getID(), UUID.randomUUID());
		assertEquals(1, helper.saveMessage(msg1_2), "Save properly is 1");
		assertEquals(1, helper.saveMessage(msg2_1), "Save properly is 1");
		assertEquals(1, helper.saveMessage(msg1_3), "Save properly is 1");

		// all messages
		privateMessageList allMessages = helper.getAllMessages();
		assertEquals(3, allMessages.getSize(), "Total number of messages should be 3");
		assertEquals(allMessages.get(0).getTextBody(), "User1 to User2");
		assertEquals(allMessages.get(1).getTextBody(), "User2 to User1");
		assertEquals(allMessages.get(2).getTextBody(), "User1 to User3");

		// messages involving user1
		privateMessageList user1Messages = helper.getMessagesIncludingUser(user1.getID());
		assertEquals(3, user1Messages.getSize(), "There should only be one message to user 1");

		// messages from user1
		privateMessageList fromUser1 = helper.getMessagesFromUser(user1.getID());
		assertEquals(2, fromUser1.getSize(), "User 1 has sent 2 messages");
		assertEquals(fromUser1.get(0).getTextBody(), "User1 to User2");
		assertEquals(fromUser1.get(1).getTextBody(), "User1 to User3");

		// messages to user1
		privateMessageList toUser1 = helper.getMessagesToUser(user1.getID());
		assertEquals(1, toUser1.getSize(), "There should only be one message to user 1");
		assertEquals(toUser1.get(0).getTextBody(), "User2 to User1");

		// communication between user 1 and user 2
		privateMessageList user1and2 = helper.getCommunicationBetweenUsers(user1.getID(), user2.getID());
		assertEquals(2, user1and2.getSize(), "User 1 and User 2 have sent 2 messages to eachother");
		assertEquals(user1and2.get(0).getTextBody(), "User1 to User2");
		assertEquals(user1and2.get(1).getTextBody(), "User2 to User1");

		// Search
		List<UUID> searchResults = allMessages.search("User1 to"); // search test
		assertEquals(2, searchResults.size(), "only 2 messages contain 'User1 to'");
		assertEquals(searchResults.get(0), msg1_2.getID());
		assertEquals(searchResults.get(1), msg1_3.getID());

		List<UUID> noResults = allMessages.search("gibberishxxx");
		assertEquals(0, noResults.size(), "No messages should be returned");
	}

	// 25
	/*
	 * PM: Save and update in database
	 */
	@Test
	@Order(25)
	@DisplayName("PM: Save and update in database")
	void testUpdateMessageInDatabase() throws SQLException {
		helper.ResetHard(DATABASE_URL);
		// make message and save
		privateMessage msg = new privateMessage("Original Text PM", user1.getID(), user2.getID(), UUID.randomUUID());
		assertEquals(1, helper.saveMessage(msg));

		// Act: Update the message
		String newText = "Updated Text PM";
		int updateResult = helper.updateMessage(msg.getID(), newText);
		assertEquals(1, updateResult, "One message should be found");

		// Assert: Retrieve and verify the update
		privateMessage updatedMsg = helper.getMessageByUUID(msg.getID()); // Get by UUID for certainty
		assertNotNull(updatedMsg, "Updated message should be found by UUID");
		assertEquals(newText, updatedMsg.getTextBody(), "Message text should be updated");
	}

	// 26
	/*
	 * User: Test user creation
	 */
	@Test
	@Order(26)
	@DisplayName("User: Test user creation")
	void testUserCreation() {
		User newUser = new User("NewTestUser", "TestPass123!", "student", "reviewer");
		assertNotNull(newUser.getID(), "User ID should be initialized");
		assertEquals("NewTestUser", newUser.getUserName(), "User name should match");
		assertEquals("TestPass123!", newUser.getPassword(), "Password should match");
		assertTrue(newUser.getRole().contains("student"), "user should have role student");
		assertTrue(newUser.getRole().contains("reviewer"), "user should have role reviewer");
	}

	// 27
	/*
	 * User: Test user reputation increase and decrease
	 */
	@Test
	@Order(27)
	@DisplayName("User: Test user reputation increase and decrease")
	void testUserReputation() {
		helper.ResetHard(DATABASE_URL);
		User newUser = new User("RepTestUser", "RepPass123!", "reviewer");
		assertEquals(0, newUser.getReputation(), "Initial reputation should be 0");
		newUser.increaseReputation();
		assertEquals(1, newUser.getReputation(), "Reputation shouldbe 1");
		newUser.increaseReputation();
		assertEquals(2, newUser.getReputation(), "Reputation shouldbe 2");
		newUser.decreaseReputation();
		newUser.decreaseReputation();
		assertEquals(0, newUser.getReputation(), "Reputation shouldbe 0");
	}

	// 28
	/*
	 * Database Invite: Test Invitation generation and validation
	 */
	@Test
	@Order(28)
	@DisplayName("Database Invite: Test Invitation generation and validation")
	void testInvitationCode() {
		helper.ResetHard(DATABASE_URL);
		String code = helper.generateInvitationCode();
		assertNotNull(code, "Invitation code should be generated");
		assertTrue(helper.validateInvitationCode(code), "Invitation code should be valid");
		assertFalse(helper.validateInvitationCode(code), "Invitation code valid only once");
	}

	// 29
	/*
	 * Answers: Testing upvoting and downvoting
	 */
	@Test
	@Order(29)
	@DisplayName("Answers: Testing upvoting and downvoting")
	void testUpvotingAndDownvoting() throws SQLException {
		helper.ResetHard(DATABASE_URL);
		Questions questions = new Questions();
		Answers answers = new Answers();

		Question question1 = new Question("User1", "Title q1", "This is a question", user1.getID());
		Answer answer1 = new Answer("User2", "Answer Text 1", question1.getID(), user2.getID());

		questions.addQuestion(question1);
		answers.addAnswer(answer1);

		try {
			helper.saveQuestions(questions);
			helper.saveAnswer(answer1);
			helper.register(user2);
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Failed to save question or answer.");
		}

		int initialReputation = answer1.getRepuation(helper);
		assertEquals(initialReputation, 0, "Reputation should increase by 1");

		System.out.println(answers.increaseReputation(helper, answer1.getID(), user1.getID()));
		assertEquals(initialReputation + 1, answer1.getRepuation(helper), "Reputation should increase by 1");

		answers.decreaseReputation(helper, answer1.getID(), user1.getID());
		assertEquals(initialReputation, answer1.getRepuation(helper), "Reputation should be the same");

	}

	// 30
	/*
	 * Question/Answer: Setting under review and verify
	 */
	@Test
	@Order(30)
	@DisplayName("Question/Answer: Setting under review and verify")
	void testSettingAndVerifyingQuestionUnderReview() {
		helper.ResetHard(DATABASE_URL);

		Question question = new Question("test name", "test title", "Test text", user1.getID());
		Answer answer = new Answer("answer name", "answer text", question.getID(), user2.getID());

		assertFalse(question.getUnderReview(), "question underReview is False");
		assertTrue(answer.getUnderReview(), "answer underReview is True");

		question.isUnderReview();
		answer.isUnderReview();

		assertTrue(question.getUnderReview(), "question underReview is True");
		assertTrue(answer.getUnderReview(), "answer underReview is True");

		question.notUnderReview();
		answer.notUnderReview();

		assertFalse(question.getUnderReview(), "question underReview is False");
		assertFalse(answer.getUnderReview(), "answer underReview is False");
	}

}