package Project;

public class Appointment {

    private Patient patient;
    private String doctor;
    private boolean emergency;

    public Appointment(Patient patient, String doctor, boolean emergency) {
        this.patient = patient;
        this.doctor = doctor;
        this.emergency = emergency;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getDoctor() {
        return doctor;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public String getPriorityLabel() {
        return emergency ? "EMERGENCY" : "REGULAR";
    }
}