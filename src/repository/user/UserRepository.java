package repository.user;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.Database;
import model.doctorpatient.Person;
import model.user.User;
import model.user.UserType;
import repository.patientdoctor.DoctorRepository;
import repository.patientdoctor.PatientRepository;

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
		UserTypeRepository userTypeRepo = UserTypeRepository.getInstance();
		PatientRepository patientRepo = PatientRepository.getInstance();
		DoctorRepository doctorRepo = DoctorRepository.getInstance();
		try {
			ResultSet userResult = database.getResult("SELECT * FROM user", null);
			while(userResult.next()) {
				UserType userType = userTypeRepo.loadUserTypeById(userResult.getInt("usertypeid"));
				
				Person patientOrDoctor = new Person();
				if(userType.getUserTypeId() == 2) //patient
					patientOrDoctor = patientRepo.loadPatientById(userResult.getInt("patientordoctorid"));
				else if(userType.getUserTypeId() == 3) //doctor
					patientOrDoctor = doctorRepo.loadDoctorById(userResult.getInt("patientordoctorid"));
						
				users.add(new User(userResult.getInt("userid"), userResult.getString("username"), 
								   userResult.getString("password"), userResult.getBoolean("islock"), 
								   userType, patientOrDoctor));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return users;
	}
	public User loadUserById(int userId) {
		User user = new User();
		UserTypeRepository userTypeRepo = UserTypeRepository.getInstance();
		PatientRepository patientRepo = PatientRepository.getInstance();
		DoctorRepository doctorRepo = DoctorRepository.getInstance();
		try {
			ResultSet userResult = database.getResult("SELECT * FROM user WHERE userid = ?", Arrays.asList(userId));
			if(userResult.next()) {
				UserType userType = userTypeRepo.loadUserTypeById(userResult.getInt("usertypeid"));
				
				Person patientOrDoctor = new Person();
				if(userType.getUserTypeId() == 2) //patient
					patientOrDoctor = patientRepo.loadPatientById(userResult.getInt("patientordoctorid"));
				else if(userType.getUserTypeId() == 3) //doctor
					patientOrDoctor = doctorRepo.loadDoctorById(userResult.getInt("patientordoctorid"));
						
				user = new User(userResult.getInt("userid"), userResult.getString("username"), 
								userResult.getString("password"), userResult.getBoolean("islock"), 
								userType, patientOrDoctor);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return user;
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
