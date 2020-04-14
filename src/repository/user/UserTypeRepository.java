package repository.user;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.Database;
import model.user.UserType;

public class UserTypeRepository {
	private static UserTypeRepository userTypeRepository;
	private Database database = Database.getInstance();
	private UserTypeRepository() {}
	public static UserTypeRepository getInstance() {
		if(userTypeRepository == null)
			userTypeRepository = new UserTypeRepository();
		return userTypeRepository;
	}
	public boolean saveUserType(UserType userType) {
		boolean isSuccess = false;
		try {
			if(userType != null) 
				isSuccess = database.executeStatement("INSERT into usertype(usertypename) VALUES(?)", Arrays.asList(userType.getUserTypeName()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public List<UserType> loadUserTypes() {
		List<UserType> userTypes = new ArrayList<>();
		try {
			ResultSet result = database.getResult("SELECT * FROM usertype", null);
			while(result.next()) {
				userTypes.add(new UserType(result.getInt(1), result.getString(2)));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return userTypes;
	}
	public boolean deleteUserTypeById(int userTypeId) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("DELETE FROM usertype where usertypeid = ?", Arrays.asList(userTypeId));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public boolean updateUserTypeById(UserType userType) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("UPDATE usertype SET usertypename = ? WHERE usertypeid = ?", Arrays.asList(userType.getUserTypeName(), userType.getUserTypeId()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
}
