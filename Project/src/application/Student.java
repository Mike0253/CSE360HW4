package application;

/**
 * The Student class represents a student user in the application.
 * It extends the base User class, inheriting all user functionality
 * while serving as a marker for student-specific capabilities.
 * 
 * <p>This class currently doesn't add any additional functionality
 * beyond the User class but serves as a distinct type for role-based
 * operations and may be extended in the future with student-specific
 * features.</p>
 */
public class Student extends User {

    /**
     * Constructs a new Student instance with the specified credentials and role bits.
     * 
     * @param userName the username for the student account
     * @param password the password for the student account
     * @param role_bits the bitmask representing the student's roles and permissions
     */
    public Student(String userName, String password, int role_bits) {
        super(userName, password, role_bits);
    }

}