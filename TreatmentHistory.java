package Project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class TreatmentHistory {

    private HashMap<String, Treatment> byId = new HashMap<>();
    private HashMap<Integer, ArrayList<Treatment>> byPatient = new HashMap<>();
    private int nextTreatmentNumber = 1;

    // Add treatment
    public Treatment addTreatment(int patientId, String doctor, String diagnosis, String description, long costCents) {

        String id = String.format("T%04d", nextTreatmentNumber++);

        Treatment treatment = new Treatment(id, patientId, LocalDate.now().toString(), doctor, diagnosis, description, costCents);

        byId.put(id, treatment);

        byPatient.putIfAbsent(patientId, new ArrayList<>());

        byPatient.get(patientId).add(treatment);

        System.out.println("Treatment history saved! Treatment ID: " + id);

        return treatment;
    }

    // Search one treatment by its unique ID
    public Treatment findById(String id) {

        return byId.get(id.trim().toUpperCase());
    }

    // Get treatment history
    public ArrayList<Treatment> getHistory(int patientId) {

        return new ArrayList<>(byPatient.getOrDefault(patientId, new ArrayList<>()));
    }

    // Total charges for a patient
    public long getTotalCents(int patientId) {

        long total = 0;

        for(Treatment treatment : getHistory(patientId)) {
            total += treatment.getCostCents();
        }

        return total;
    }

    // Display treatment history
    public void displayHistory(int patientId) {

        ArrayList<Treatment> treatments = getHistory(patientId);

        if(treatments.isEmpty()) {
            System.out.println("No treatment history found.");
            return;
        }

        System.out.println("\n===== TREATMENT HISTORY =====");

        for(int i = 0; i < treatments.size(); i++) {
            System.out.println((i + 1) + ". " + treatments.get(i).describe());
        }

        System.out.println("Total charges: " + MedicalBilling.format(getTotalCents(patientId)));
    }
}