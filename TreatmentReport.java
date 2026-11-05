package Project;

import java.util.ArrayList;

public class TreatmentReport {
    public void generateReport(Patient patient, ArrayList<Treatment> treatments, int depth) {

        if(patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.println("\n===== TREATMENT REPORT =====");

        patient.display();

        System.out.println("\nTreatment Details (" + (depth <= 0 ? "full history" : "latest " + depth) + "):");

        if(treatments.isEmpty()) {

            System.out.println("No treatment records available.");

        } else {

            int start = (depth > 0 && depth < treatments.size()) ? treatments.size() - depth : 0;

            if(start > 0) {
                System.out.println("... " + start + " older treatment(s) not shown");
            }

            printRecursive(treatments, start);

            System.out.println("\nTotal charges (all treatments): "
                    + MedicalBilling.format(sumRecursive(treatments, 0)));
        }

        System.out.println("\n===== END OF REPORT =====");
    }

    private void printRecursive(ArrayList<Treatment> treatments, int index) {

        // Base case
        if(index >= treatments.size()) {
            return;
        }

        System.out.println((index + 1) + ". " + treatments.get(index).describe());

        // Recursive call
        printRecursive(treatments, index + 1);
    }

    private long sumRecursive(ArrayList<Treatment> treatments, int index) {

        // Base case
        if(index >= treatments.size()) {
            return 0;
        }

        // Recursive call
        return treatments.get(index).getCostCents() + sumRecursive(treatments, index + 1);
    }
}