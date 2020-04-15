package repository.patientdoctor;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.Database;
import model.doctorpatient.Person;

public class DoctorRepository {
	private static DoctorRepository doctorRepository;
	private Database database = Database.getInstance();
	private DoctorRepository() {}
	public static DoctorRepository getInstance() {
		if(doctorRepository == null)
			doctorRepository = new DoctorRepository();
		return doctorRepository;
	}
	public boolean saveDoctor(Person doctor) {
		boolean isSuccess = false;
		try {
			if(doctor != null) {
				//load auto patientNumber here
				ResultSet result = database.getResult("SELECT IFNULL(MAX(RIGHT(doctor_number, LENGTH(doctor_number) - 3)),0) + 1 As doctor_number FROM doctor", null);
				if(result.next())
					doctor.setPersonNumber("DT#" + result.getInt("doctor_number"));
				List<Object> parameters = Arrays.asList(doctor.getPersonNumber(), doctor.getFirstName(), 
						doctor.getMiddleName(), doctor.getLastName(), doctor.getContactPhone(), doctor.getAddress());
				isSuccess = database.executeStatement("INSERT INTO doctor(doctor_number, firstname, middlename, lastname, contactphone, address) "
						+ "VALUES(?, ?, ?, ?, ?, ?)", parameters);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public List<Person> loadDoctors() {
		List<Person> doctors = new ArrayList<>();
		try {
			ResultSet result = database.getResult("SELECT * FROM doctor", null);
			while(result.next()) {
				doctors.add(new Person(result.getInt(1), 
										result.getString(2), 
										result.getString(3), 
										result.getString(4), 
										result.getString(5), 
										result.getString(6), 
										result.getString(7)));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return doctors;
	}
	public Person loadDoctorById(int doctorId) {
		Person doctor = new Person();
		try {
			ResultSet result = database.getResult("SELECT * FROM doctor WHERE doctorid = ?", Arrays.asList(doctorId));
			if(result.next()) {
				doctor = new Person(result.getInt(1), 
									result.getString(2), 
									result.getString(3), 
									result.getString(4), 
									result.getString(5), 
									result.getString(6), 
									result.getString(7));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return doctor;
	}
	public boolean deleteDoctorById(int doctorId) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("DELETE FROM doctor where doctorid = ?", Arrays.asList(doctorId));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public boolean updateDoctorById(Person doctor) {
		boolean isSuccess = false;
		try {
			List<Object> parameters = Arrays.asList(doctor.getFirstName(), 
					doctor.getMiddleName(), doctor.getLastName(), doctor.getContactPhone(), doctor.getAddress(), doctor.getPersonId());
			isSuccess = database.executeStatement("UPDATE doctor SET firstname = ?, middlename = ?, lastname = ?, contactphone = ?, address = ? WHERE doctorid = ?", parameters);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
}
