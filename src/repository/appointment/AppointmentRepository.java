package repository.appointment;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.Database;
import model.appointment.Appointment;
import model.doctorpatient.Specialization;
import model.user.User;
import repository.patientdoctor.SpecializationRepository;
import repository.user.UserRepository;


public class AppointmentRepository {
	private static AppointmentRepository appointmentRepository;
	private Database database = Database.getInstance();
	private AppointmentRepository() {}
	public static AppointmentRepository getInstance() {
		if(appointmentRepository == null)
			appointmentRepository = new AppointmentRepository();
		return appointmentRepository;
	}
	public boolean saveAppointmentRepository(Appointment appointment) {
		boolean isSuccess = false;
		try {
			if(appointment != null) {
				//load auto appointmentNumber here
				ResultSet result = database.getResult("SELECT IFNULL(MAX(RIGHT(appointment_number, LENGTH(doctor_number) - 3)),0) + 1 As appointment_number FROM appointment", null);
				if(result.next())
					appointment.setAppointmentNumber("AP#" + result.getInt("appointment_number"));
				isSuccess = database.executeStatement("INSERT into appointment(appointment_number, appointmentdate, appointmenttime, specializationid, appointedby) VALUES(?, ?, ?, ?, ?)", 
							Arrays.asList(appointment.getAppointmentNumber(),
										  appointment.getAppointmentDate(),
										  appointment.getAppointmentTime(),
										  appointment.getSpecialization().getSpecializationId(),
										  appointment.getAppointedBy().getUserId()));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public List<Appointment> loadAppointments() {
		List<Appointment> appointments = new ArrayList<>();
		UserRepository userRepo = UserRepository.getInstance();
		SpecializationRepository specializationRepo = SpecializationRepository.getInstance();
		try {
			ResultSet result = database.getResult("SELECT * FROM appointment", null);
			while(result.next()) {
				User user = userRepo.loadUserById(result.getInt("appointedby"));
				
				Specialization specialization = specializationRepo.loadSpecializationById(result.getInt("specializationid"));
				appointments.add(new Appointment(result.getInt(1), result.getString(2), 
												 result.getDate(3).toLocalDate(), 
												 result.getTime(4).toLocalTime(), 
												 specialization, 
												 user));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return appointments;
	}
	public Appointment loadAppointmentById(int appointmentId) {
		Appointment appointment = new Appointment();
		UserRepository userRepo = UserRepository.getInstance();
		SpecializationRepository specializationRepo = SpecializationRepository.getInstance();
		try {
			ResultSet result = database.getResult("SELECT * FROM appointment where appointmentid = ?", Arrays.asList(appointmentId));
			while(result.next()) {
				User user = userRepo.loadUserById(result.getInt("appointedby"));
				
				Specialization specialization = specializationRepo.loadSpecializationById(result.getInt("specializationid"));
				appointment = new Appointment(result.getInt(1), result.getString(2), 
											  result.getDate(3).toLocalDate(), 
											  result.getTime(4).toLocalTime(), 
											  specialization, 
											  user);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return appointment;
	}
	public boolean deleteAppointmentById(int appointmentId) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("DELETE FROM appointment where appointmentid = ?", Arrays.asList(appointmentId));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public boolean updateAppointmentById(Appointment appointment) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("UPDATE appointment SET appointmentdate = ?, appointmenttime = ?, specializationid = ?, appointedby = ? WHERE appointmentid = ?", 
					Arrays.asList(appointment.getAppointmentDate(), appointment.getAppointmentTime(), appointment.getSpecialization().getSpecializationId(), appointment.getAppointedBy().getUserId()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
}
