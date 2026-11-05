package Project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class Main {

    static Scanner input = new Scanner(System.in);

    // Initialize classes
    static WaitingList waitingList = new WaitingList();
    static PatientRecords patientRecords = new PatientRecords();
    static TreatmentHistory treatmentHistory = new TreatmentHistory();
    static TreatmentReport treatmentReport = new TreatmentReport();
    static MedicalBilling medicalBilling = new MedicalBilling();

    static int nextPatientId = 1001;
    static final int MAX_AGE = 120;
    static final long MAX_AMOUNT_CENTS = 100_000_000L;

    static final int REGISTER = 1, SEARCH_PATIENT = 2,
            ADD_TO_QUEUE = 3, VIEW_QUEUE = 4, PROCESS_NEXT = 5, CANCEL_QUEUE = 6,
            ADD_TREATMENT = 7, VIEW_TREATMENTS = 8, SEARCH_TREATMENT = 9, REPORT = 10,
            CREATE_BILL = 11, PAY = 12, VIEW_BILLS = 13,
            EXIT = 14;

    static final String LINE = "======================================";

    // ---------- Display helpers ----------

    static void header(String title) {

        System.out.println("\n===== " + title + " =====");
    }

    static void pause() {

        System.out.print("\nPress Enter to return to the menu...");
        input.nextLine();
    }

    // ---------- Input validators ----------

    public static int getInt(String message) {

        while(true) {
            try {
                System.out.print(message);
                return Integer.parseInt(input.nextLine().trim());

            } catch(NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public static int getIntInRange(String message, int min, int max) {

        while(true) {

            int value = getInt(message);

            if(value >= min && value <= max) {
                return value;
            }

            System.out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }

    public static String getName(String message) {

        while(true) {

            System.out.print(message);

            String name = input.nextLine().trim().replaceAll("\\s+", " ");

            if(name.length() < 2 || name.length() > 50) {
                System.out.println("Name must be 2 to 50 characters.");
            } else if(!name.matches("\\p{L}[\\p{L} .'\\-]*")) {
                System.out.println("Name may only contain letters, spaces, periods, apostrophes and hyphens.");
            } else {
                return toTitleCase(name);
            }
        }
    }

    public static String getDoctorName(String message) {

        while(true) {

            String name = getName(message);

            if(name.toLowerCase().startsWith("dr. ")) {
                name = name.substring(4).trim();
            } else if(name.toLowerCase().startsWith("dr ")) {
                name = name.substring(3).trim();
            }

            if(name.length() >= 2) {
                return name;
            }

            System.out.println("Please enter the doctor's name.");
        }
    }

    public static String getDescription(String message, String label) {

        while(true) {

            System.out.print(message);

            String text = input.nextLine().trim().replaceAll("\\s+", " ");

            if(text.length() < 3 || text.length() > 100) {
                System.out.println(label + " must be 3 to 100 characters.");
            } else if(!text.matches("[\\p{L}\\p{N} ,.'()/:\\-]+") || !text.matches(".*\\p{L}.*")) {
                System.out.println(label + " must contain letters and may only use letters, numbers and , . ' ( ) / : -");
            } else {
                return text;
            }
        }
    }

    public static boolean getYesNo(String message) {

        while(true) {

            System.out.print(message + " (Y/N): ");

            String answer = input.nextLine().trim().toUpperCase();

            if(answer.equals("Y") || answer.equals("YES")) {
                return true;
            }

            if(answer.equals("N") || answer.equals("NO")) {
                return false;
            }

            System.out.println("Please answer Y or N.");
        }
    }

    public static long getMoneyCents(String message) {

        while(true) {

            System.out.print(message);

            String text = input.nextLine().trim().replace(",", "");

            if(!text.matches("\\d{1,9}(\\.\\d{1,2})?")) {
                System.out.println("Please enter a valid amount (numbers only, up to 2 decimal places).");
                continue;
            }

            long cents = new BigDecimal(text).movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact();

            if(cents <= 0) {
                System.out.println("Amount must be greater than zero.");
            } else if(cents > MAX_AMOUNT_CENTS) {
                System.out.println("Amount is too large. Maximum is " + MedicalBilling.format(MAX_AMOUNT_CENTS) + ".");
            } else {
                return cents;
            }
        }
    }

    private static String toTitleCase(String text) {

        StringBuilder result = new StringBuilder();
        boolean startOfWord = true;

        for(char c : text.toCharArray()) {

            result.append(startOfWord ? Character.toUpperCase(c) : Character.toLowerCase(c));

            startOfWord = (c == ' ' || c == '-' || c == '\'' || c == '.');
        }

        return result.toString();
    }

    // ---------- Patient lookup ----------

    public static Patient findPatient() {

        int id = getInt("Enter Patient ID: ");

        if(id <= 0) {
            System.out.println("Patient ID must be a positive number (IDs start at 1001).");
            return null;
        }

        Patient patient = patientRecords.search(id);

        if(patient == null) {
            System.out.println("Patient not found.");
        }

        return patient;
    }

    public static int findPatientIdOrAll(String message) {

        int id = getInt(message);

        if(id == 0) {
            return 0;
        }

        if(id < 0) {
            System.out.println("Patient ID must be a positive number (IDs start at 1001).");
            return -1;
        }

        if(patientRecords.search(id) == null) {
            System.out.println("Patient not found.");
            return -1;
        }

        return id;
    }

    // ---------- Menu actions ----------

    // 1. Register patient
    public static void registerPatient() {

        header("REGISTER PATIENT");

        String name = getName("Enter Name: ");

        int age = getIntInRange("Enter Age: ", 0, MAX_AGE);

        Patient similar = patientRecords.findSimilar(name, age);

        if(similar != null) {

            System.out.println("\nA patient with the same name and age already exists:");
            similar.display();

            if(!getYesNo("Register as a NEW patient anyway?")) {
                System.out.println("Registration cancelled.");
                return;
            }
        }

        Patient patient = new Patient(nextPatientId, name, age);

        if(patientRecords.insert(patient)) {

            nextPatientId++;

            System.out.println("\nPatient registered successfully!");
            patient.display();

        } else {
            System.out.println("Patient registration failed.");
        }
    }

    // 2. Search patient
    public static void searchPatient() {

        header("SEARCH PATIENT RECORDS");

        int id = findPatientIdOrAll("Enter Patient ID (0 = all): ");

        if(id == 0) {
            patientRecords.displayRecords();
        } else if(id > 0) {
            header("PATIENT DETAILS");
            patientRecords.search(id).display();
        }
    }

    // 3. Add to waiting list
    public static void addToWaitingList() {

        header("ADD TO WAITING LIST");

        Patient patient = findPatient();

        if(patient == null) {
            return;
        }

        if(waitingList.contains(patient.getId())) {
            System.out.println(patient.getName() + " is already on the waiting list.");
            return;
        }

        String doctor = getDoctorName("Enter Doctor's Name: ");

        boolean emergency = getYesNo("Is this an emergency case?");

        waitingList.addPatient(patient, doctor, emergency);
    }

    // 4. View waiting list
    public static void viewWaitingList() {

        waitingList.displayWaitingList();
    }

    // 5. Process next patient
    public static void processNextPatient() {

        Appointment appointment = waitingList.processNextPatient();

        if(appointment != null) {
            header("PATIENT FOR CONSULTATION");
            appointment.getPatient().display();
            System.out.println("Doctor: Dr. " + appointment.getDoctor());
            System.out.println("Priority: " + appointment.getPriorityLabel());
        }
    }

    // 6. Cancel a waiting list entry
    public static void cancelWaitingEntry() {

        header("CANCEL WAITING LIST ENTRY");

        Patient patient = findPatient();

        if(patient != null) {
            waitingList.removePatient(patient.getId());
        }
    }

    // 7. Add treatment
    public static void addTreatment() {

        header("ADD TREATMENT");

        Patient patient = findPatient();

        if(patient == null) {
            return;
        }

        String doctor = getDoctorName("Enter Attending Doctor's Name: ");

        String diagnosis = getDescription("Enter Diagnosis: ", "Diagnosis");

        String treatment = getDescription("Enter Treatment Details: ", "Treatment");

        long cost = getMoneyCents("Enter Treatment Cost (PHP): ");

        treatmentHistory.addTreatment(patient.getId(), doctor, diagnosis, treatment, cost);
    }

    // 8. View treatment history
    public static void viewTreatmentHistory() {

        header("TREATMENT HISTORY");

        Patient patient = findPatient();

        if(patient != null) {
            treatmentHistory.displayHistory(patient.getId());
        }
    }

    // 9. Search treatment by its unique ID
    public static void searchTreatmentById() {

        header("SEARCH TREATMENT");

        System.out.print("Enter Treatment ID (example: T0001): ");

        String treatmentId = input.nextLine().trim().toUpperCase();

        if(!treatmentId.matches("T\\d{4,}")) {
            System.out.println("Invalid format. A treatment ID looks like T0001.");
            return;
        }

        Treatment treatment = treatmentHistory.findById(treatmentId);

        if(treatment == null) {
            System.out.println("Treatment not found.");
            return;
        }

        Patient patient = patientRecords.search(treatment.getPatientId());

        header("TREATMENT DETAILS");
        System.out.println("Treatment ID: " + treatment.getId());
        System.out.println("Patient: " + (patient == null
                ? "ID " + treatment.getPatientId()
                : patient.getName() + " (ID " + patient.getId() + ")"));
        System.out.println("Date: " + treatment.getDate());
        System.out.println("Doctor: Dr. " + treatment.getDoctor());
        System.out.println("Diagnosis: " + treatment.getDiagnosis());
        System.out.println("Treatment: " + treatment.getDescription());
        System.out.println("Cost: " + MedicalBilling.format(treatment.getCostCents()));
    }

    // 10. Generate treatment report
    public static void generateTreatmentReport() {

        header("TREATMENT REPORT");

        Patient patient = findPatient();

        if(patient == null) {
            return;
        }

        int depth = getIntInRange("Show how many of the most recent treatments? (0 = all): ", 0, 1000);

        treatmentReport.generateReport(patient, treatmentHistory.getHistory(patient.getId()), depth);
    }

    // 11. Create / refresh medical bill
    public static void createMedicalBill() {

        header("CREATE / UPDATE MEDICAL BILL");

        Patient patient = findPatient();

        if(patient == null) {
            return;
        }

        medicalBilling.createOrUpdateBill(patient.getId(), treatmentHistory.getHistory(patient.getId()));
    }

    // 12. Process payment
    public static void processPayment() {

        header("PROCESS PAYMENT");

        Patient patient = findPatient();

        if(patient == null) {
            return;
        }

        int id = patient.getId();

        long balance = medicalBilling.getBalanceCents(id);

        if(balance < 0) {
            System.out.println("No bill found. Create the bill first.");
            return;
        }

        if(medicalBilling.getBilledTotal(id) != treatmentHistory.getTotalCents(id)) {
            System.out.println("The bill is outdated. Update it first.");
            return;
        }

        if(balance == 0) {
            System.out.println("This bill is already fully paid.");
            return;
        }

        System.out.println("Remaining balance: " + MedicalBilling.format(balance));

        long payment = getMoneyCents("Enter Payment Amount (PHP): ");

        medicalBilling.processPayment(id, payment);
    }

    // 13. View bill
    public static void viewBills() {

        header("VIEW BILL");

        int id = findPatientIdOrAll("Enter Patient ID (0 = all): ");

        if(id == 0) {
            medicalBilling.displayAllBills();
        } else if(id > 0) {
            medicalBilling.viewBill(id);
        }
    }

    // ---------- Main menu ----------

    static void showMenu() {

        System.out.println("\n" + LINE);
        System.out.println("       PATIENT MANAGEMENT SYSTEM");
        System.out.println(LINE);

        System.out.println(" PATIENTS");
        System.out.println("   1.  Register New Patient");
        System.out.println("   2.  Search Patient Records");

        System.out.println("\n WAITING LIST");
        System.out.println("   3.  Add Patient to Waiting List");
        System.out.println("   4.  View Waiting List");
        System.out.println("   5.  Process Next Patient");
        System.out.println("   6.  Cancel Waiting List Entry");

        System.out.println("\n TREATMENTS");
        System.out.println("   7.  Add Treatment History");
        System.out.println("   8.  View Treatment History");
        System.out.println("   9.  Search Treatment by ID");
        System.out.println("   10. Generate Treatment Report");

        System.out.println("\n BILLING");
        System.out.println("   11. Create / Update Medical Bill");
        System.out.println("   12. Process Payment");
        System.out.println("   13. View Bill");

        System.out.println("\n   14. Exit");
        System.out.println(LINE);
    }

    public static void main(String[] args) {

        int choice;
        do {

            showMenu();

            choice = getIntInRange("Enter your choice: ", 1, 14);

            switch(choice) {

                case REGISTER:         registerPatient();        break;
                case SEARCH_PATIENT:   searchPatient();          break;

                case ADD_TO_QUEUE:     addToWaitingList();       break;
                case VIEW_QUEUE:       viewWaitingList();        break;
                case PROCESS_NEXT:     processNextPatient();     break;
                case CANCEL_QUEUE:     cancelWaitingEntry();     break;

                case ADD_TREATMENT:    addTreatment();           break;
                case VIEW_TREATMENTS:  viewTreatmentHistory();   break;
                case SEARCH_TREATMENT: searchTreatmentById();    break;
                case REPORT:           generateTreatmentReport(); break;

                case CREATE_BILL:      createMedicalBill();      break;
                case PAY:              processPayment();         break;
                case VIEW_BILLS:       viewBills();              break;

                case EXIT:
                    System.out.println("\nThank you for using Patient Management System!");
                    break;
            }

            if(choice != EXIT) {
                pause();
            }

        } while(choice != EXIT);

        input.close();
    }
}