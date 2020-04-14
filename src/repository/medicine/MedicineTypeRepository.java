package repository.medicine;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.Database;
import model.medicine.MedicineType;

public class MedicineTypeRepository {
	private static MedicineTypeRepository medicineTypeRepository;
	private Database database = Database.getInstance();
	private MedicineTypeRepository() {}
	public static MedicineTypeRepository getInstance() {
		if(medicineTypeRepository == null)
			medicineTypeRepository = new MedicineTypeRepository();
		return medicineTypeRepository;
	}
	public boolean saveMedicineType(MedicineType medicineType) {
		boolean isSuccess = false;
		try {
			if(medicineType != null) 
				isSuccess = database.executeStatement("INSERT into medicinetype(medicinetypename) VALUES(?)", Arrays.asList(medicineType.getMedicineTypeName()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public List<MedicineType> loadMedicineType() {
		List<MedicineType> medicineTypes = new ArrayList<>();
		try {
			ResultSet result = database.getResult("SELECT * FROM medicinetype", null);
			while(result.next()) {
				medicineTypes.add(new MedicineType(result.getInt(1), result.getString(2)));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return medicineTypes;
	}
	public boolean deleteMedicineTypeById(int medicineTypeId) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("DELETE FROM medicinetype where medicinetypeid = ?", Arrays.asList(medicineTypeId));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public boolean updateMedicineTypeById(MedicineType medicineType) {
		boolean isSuccess = false;
		try {
			isSuccess = database.executeStatement("UPDATE medicinetype SET medicinetypename = ? WHERE medicinetypeid = ?", Arrays.asList(medicineType.getMedicineTypeName(), medicineType.getMedicineTypeId()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
}
