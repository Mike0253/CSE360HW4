package application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The User class represents a user entity in the system. It contains the user's
 * details such as userName, password, and role.
 */
public class User {
	private String userName;
	private String password;
	private int role;
	private int reputation;
	private UUID ID;
	private List<UUID> trustedReviewers = new ArrayList<>();
	
	/**
	 * strToBitFlag
	 * 
	 * <p> user roles are represented using bits.
	 * this is because the user can hold multiple roles.
	 * best way to represent this is through bitflags.
	 * 
	 * @param role
	 * @return bit flag of the role.
	 */
	private static int strToBitFlag(String role) {
		switch (role.toLowerCase()) {
		case "student":
			return 1 << 0;
		case "admin":
			return 1 << 1;
		case "reviewer":
			return 1 << 2;
		case "instructor":
			return 1 << 3;
		case "staff":
			return 1 << 4;
		default:
			return 0;
		}
	}

	/**
	 * bitFlagsToStr
	 * 
	 * @param role_bits
	 * @return string of the user's roles.
	 */
	private static String bitFlagsToStr(int role_bits) {
		String flags_str = "";
		if ((role_bits & (1 << 0)) > 0) {
			flags_str += "student";
		}
		if ((role_bits & (1 << 1)) > 0) {
			if (flags_str.length() > 0) {
				flags_str += ",";
			}
			flags_str += "admin";
		}
		if ((role_bits & (1 << 2)) > 0) {
			if (flags_str.length() > 0) {
				flags_str += ",";
			}
			flags_str += "reviewer";
		}
		if ((role_bits & (1 << 3)) > 0) {
			if (flags_str.length() > 0) {
				flags_str += ",";
			}
			flags_str += "instructor";
		}
		if ((role_bits & (1 << 4)) > 0) {
			if (flags_str.length() > 0) {
				flags_str += ",";
			}
			flags_str += "staff";
		}
//		if (role_bits == 0) {
//			flags_str = "user";
//		}
		return flags_str;
	}

	/// Constructor to initialize a new User object with userName, password, and
	/// role.

	/// User(name, pass, "admin", "student", "staff");
	/// User(name, pass);
	public User(String userName, String password, String... roles) {
		this.userName = userName;
		this.password = password;
		for (var role_str : roles) {
			this.role |= strToBitFlag(role_str);
		}
		this.reputation = 0;
		ID = UUID.randomUUID();
	}

	public User(String userName, String password, List<String> roles) {
		this.userName = userName;
		this.password = password;
		for (var role_str : roles) {
			this.role |= strToBitFlag(role_str);
		}
		this.reputation = 0;
		ID = UUID.randomUUID();
	}

	public User(String userName, String password, int role_bits) {
		this.userName = userName;
		this.password = password;
		this.role = role_bits;
		this.reputation = 0;
		ID = UUID.randomUUID();
	}

	/// Sets the role of the user.
	public void setRole(String role) {
		this.role |= strToBitFlag(role);
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getRole() {
		return bitFlagsToStr(role);
	}

	public int getRoleInt() {
		return role;
	}

	public void setRoleInt(int role) {
		this.role = role;
	}

	public int getReputation() {
		return reputation;
	}

	public void increaseReputation() {
		reputation += 1;
	}

	public void decreaseReputation() {
		reputation -= 1;
	}

	public void setReputation(int rep) {
		reputation = rep;
	}

	// get answer UUID
	public UUID getID() {
		return ID;
	}

	public void setID(UUID iD) {
		ID = iD;
	}

	public List<UUID> getTrustedReviewers() {
		return trustedReviewers;
	}

	public void setTrustedReviewers(List<UUID> trustedReviewers) {
		this.trustedReviewers = trustedReviewers;
	}

	public boolean addTrustedReviewer(UUID reviewerUUID) {
		if (!trustedReviewers.contains(reviewerUUID)) {
			trustedReviewers.add(reviewerUUID);
			return true;
		} else {
			return false; /// Reviewer already in the list
		}
	}

	
	public boolean removeTrustedReviewer(UUID reviewerUUID) {
		return trustedReviewers.remove(reviewerUUID); /// Returns true if the list contained the specified element
	}
}