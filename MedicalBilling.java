package Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MedicalBilling {
    public static String format(long cents) {

        String sign = cents < 0 ? "-" : "";
        long abs = Math.abs(cents);

        return String.format(Locale.US, "%sPHP %,d.%02d", sign, abs / 100, abs % 100);
    }

    private static class Bill {

        int patientId;
        long totalCents;
        long paidCents;

        Bill(int patientId, long totalCents) {
            this.patientId = patientId;
            this.totalCents = totalCents;
            this.paidCents = 0;
        }

        long getBalance() {
            return totalCents - paidCents;
        }

        String getStatus() {

            if(getBalance() == 0) {
                return "PAID";
            } else if(paidCents > 0) {
                return "PARTIALLY PAID";
            } else {
                return "UNPAID";
            }
        }

        void displayBill() {

            System.out.println("\nPatient ID: " + patientId);

            System.out.println("Total Bill: " + format(totalCents));

            System.out.println("Amount Paid: " + format(paidCents));

            System.out.println("Remaining Balance: " + format(getBalance()));

            System.out.println("Payment Status: " + getStatus());
        }
    }

    private HashMap<Integer, Bill> bills = new HashMap<>();

    // Create the bill from the patient's treatments, or refresh it if new treatments were added.
    // Shows every treatment with its own price, then the overall bill.
    public void createOrUpdateBill(int patientId, ArrayList<Treatment> treatments) {

        long totalCents = 0;

        for(Treatment treatment : treatments) {
            totalCents += treatment.getCostCents();
        }

        if(totalCents <= 0) {
            System.out.println("No billable treatments found. Add a treatment with a cost first.");
            return;
        }

        Bill bill = bills.get(patientId);

        if(bill == null) {

            bill = new Bill(patientId, totalCents);
            bills.put(patientId, bill);

            System.out.println("Medical bill created from treatment charges!");

        } else if(bill.totalCents == totalCents) {

            System.out.println("Bill is already up to date.");

        } else {

            bill.totalCents = totalCents;

            System.out.println("Bill updated with the latest treatment charges!");
        }

        System.out.println("\n===== BILL BREAKDOWN =====");

        for(int i = 0; i < treatments.size(); i++) {
            System.out.println((i + 1) + ". " + treatments.get(i).describe());
        }

        System.out.println("----------------------");

        bill.displayBill();
    }

    // Total currently billed for a patient
    public long getBilledTotal(int patientId) {

        Bill bill = bills.get(patientId);

        return bill == null ? -1 : bill.totalCents;
    }

    // Remaining balance for a patient
    public long getBalanceCents(int patientId) {

        Bill bill = bills.get(patientId);

        return bill == null ? -1 : bill.getBalance();
    }

    // Process payment
    public void processPayment(int patientId, long paymentCents) {

        Bill bill = bills.get(patientId);

        if(bill == null) {
            System.out.println("No bill found.");
            return;
        }

        if(paymentCents <= 0) {
            System.out.println("Invalid payment amount.");
            return;
        }

        if(bill.getBalance() == 0) {
            System.out.println("This bill is already fully paid.");
            return;
        }

        long balance = bill.getBalance();
        long applied = Math.min(paymentCents, balance);
        long change = paymentCents - applied;

        bill.paidCents += applied;

        System.out.println("Payment successful!");
        System.out.println("Amount Tendered: " + format(paymentCents));
        System.out.println("Amount Applied: " + format(applied));

        if(change > 0) {
            System.out.println("Change: " + format(change));
        }

        bill.displayBill();
    }

    // View individual bill
    public void viewBill(int patientId) {

        Bill bill = bills.get(patientId);

        if(bill == null) {
            System.out.println("No billing record found.");
            return;
        }

        bill.displayBill();
    }

    // Display all bills (sorted by patient ID)
    public void displayAllBills() {

        if(bills.isEmpty()) {
            System.out.println("No billing records available.");
            return;
        }

        ArrayList<Bill> all = new ArrayList<>(bills.values());

        all.sort((a, b) -> Integer.compare(a.patientId, b.patientId));

        System.out.println("\n===== ALL BILLING RECORDS =====");

        for(Bill bill : all) {
            bill.displayBill();
            System.out.println("----------------------");
        }
    }
}