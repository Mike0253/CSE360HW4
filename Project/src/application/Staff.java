package application;

public class Staff extends User {

    /**
     * Constructs a new Staff instance with the specified credentials and role bits.
     * 
     * @param userName the username for the student account
     * @param password the password for the student account
     * @param role_bits the bitmask representing the student's roles and permissions
     */
    public Staff(String userName, String password, int role_bits) {
        super(userName, password, role_bits);
    }
}