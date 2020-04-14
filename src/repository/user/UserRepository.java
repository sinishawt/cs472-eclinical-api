package repository.user;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.Database;
import model.doctorpatient.Person;
import model.user.User;
import model.user.UserType;

public class UserRepository {
	private static UserRepository userRepository;
	private Database database = Database.getInstance();
	private UserRepository() {}
	public static UserRepository getInstance() {
		if(userRepository == null)
			userRepository = new UserRepository();
		return userRepository;
	}
	public boolean saveUser(User user) {
		boolean isSuccess = false;
		try {
			if(user != null) 
				isSuccess = database.executeStatement("INSERT into user(username, password, usertypeid, islock, patientordoctorid) VALUES(?, ?, ?, 0, ?)", 
													  Arrays.asList(user.getUsername(), user.getPassword(), user.getUserType().getUserTypeId(), user.getPerson().getPersonId()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public List<User> loadUsers() {
		List<User> users = new ArrayList<>();
		try {
			ResultSet userResult = database.getResult("SELECT * FROM user", null);
			while(userResult.next()) {
				UserType userType = new UserType();
				ResultSet userTypeResult = database.getResult("SELECT * FROM usertype WHERE usertypeid = ?", Arrays.asList(userResult.getInt("usertypeid")));
				if(userTypeResult.next()) 
					userType = new UserType(userTypeResult.getInt(1), userTypeResult.getString(2));
				
				ResultSet patientOrDoctorResult = null;
				Person patientOrDoctor = new Person();
				if(userType.getUserTypeId() == 2) //patient
					patientOrDoctorResult = database.getResult("SELECT * FROM patient WHERE patientid = ?", Arrays.asList(userResult.getInt("patientordoctorid")));
				else if(userType.getUserTypeId() == 3) //doctor
					patientOrDoctorResult = database.getResult("SELECT * FROM doctor WHERE doctorid = ?", Arrays.asList(userResult.getInt("patientordoctorid")));
				
				if(patientOrDoctorResult != null) { 
					if(patientOrDoctorResult.next()) 
						patientOrDoctor = new Person(patientOrDoctorResult.getInt(1), 
													 patientOrDoctorResult.getString(2), 
													 patientOrDoctorResult.getString(3), 
													 patientOrDoctorResult.getString(4), 
													 patientOrDoctorResult.getString(5), 
													 patientOrDoctorResult.getString(6), 
													 patientOrDoctorResult.getString(7));
				}
				
				users.add(new User(userResult.getInt("userid"), userResult.getString("username"), 
								   userResult.getString("password"), userResult.getBoolean("islock"), 
								   userType, patientOrDoctor));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return users;
	}
	public boolean deleteUserById(int userId) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("DELETE FROM user where userid = ?", Arrays.asList(userId));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public boolean updateUserById(User user) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("UPDATE user SET username = ?, password = ?, islock = ? WHERE userid = ?", 
												   Arrays.asList(user.getUsername(), 
																 user.getPassword(),
																 user.isLock()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
}
