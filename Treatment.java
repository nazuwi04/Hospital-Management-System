package Project;

// A treatment given by the hospital: the doctor, the diagnosis, what was done, and the charge.
public class Treatment {

    private String id;
    private int patientId;
    private String date;
    private String doctor;
    private String diagnosis;
    private String description;
    private long costCents;

    public Treatment(String id, int patientId, String date, String doctor,
                     String diagnosis, String description, long costCents) {
        this.id = id;
        this.patientId = patientId;
        this.date = date;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.description = description;
        this.costCents = costCents;
    }

    public String getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getDate() {
        return date;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getDescription() {
        return description;
    }

    public long getCostCents() {
        return costCents;
    }

    public String describe() {
        return "[" + id + "] " + date + " | Dr. " + doctor
                + " | Diagnosis: " + diagnosis
                + " | Treatment: " + description
                + " | " + MedicalBilling.format(costCents);
    }
}