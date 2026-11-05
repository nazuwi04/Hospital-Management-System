package Project;
public class WaitingList {

    private class Node {
        Appointment appointment;
        Node next;

        Node(Appointment appointment) {
            this.appointment = appointment;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;

    public boolean contains(int patientId) {

        Node current = head;

        while(current != null) {

            if(current.appointment.getPatient().getId() == patientId) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public void addPatient(Patient patient, String doctor, boolean emergency) {

        if(contains(patient.getId())) {
            System.out.println(patient.getName() + " is already on the waiting list.");
            return;
        }

        Node newNode = new Node(new Appointment(patient, doctor, emergency));
        int position = 1;

        if(head == null) {

            head = tail = newNode;

        } else if(!emergency) {

            // Regular: join at the end
            Node current = head;

            while(current != null) {
                position++;
                current = current.next;
            }

            tail.next = newNode;
            tail = newNode;

        } else {

            // Emergency: go after the last emergency already waiting
            Node previous = null;
            Node current = head;

            while(current != null && current.appointment.isEmergency()) {
                previous = current;
                current = current.next;
                position++;
            }

            newNode.next = current;

            if(previous == null) {
                head = newNode;
            } else {
                previous.next = newNode;
            }

            if(current == null) {
                tail = newNode;
            }
        }

        System.out.println("Patient added to waiting list! Position: " + position
                + " (" + newNode.appointment.getPriorityLabel() + ", Dr. " + doctor + ")");
    }

    // Cancel a patient from anywhere in the list
    public boolean removePatient(int patientId) {

        Node previous = null;
        Node current = head;

        while(current != null && current.appointment.getPatient().getId() != patientId) {
            previous = current;
            current = current.next;
        }

        if(current == null) {
            System.out.println("Patient is not on the waiting list.");
            return false;
        }

        if(previous == null) {
            head = current.next;
        } else {
            previous.next = current.next;
        }

        if(current == tail) {
            tail = previous;
        }

        System.out.println("Removed " + current.appointment.getPatient().getName() + " from the waiting list.");

        return true;
    }

    // Display waiting list
    public void displayWaitingList() {

        if(head == null) {
            System.out.println("Waiting list is empty.");
            return;
        }

        Node current = head;
        int position = 1;

        System.out.println("\n===== WAITING LIST =====");

        while(current != null) {

            Appointment appointment = current.appointment;

            System.out.println(position + ". [" + appointment.getPriorityLabel() + "] ID: "
                    + appointment.getPatient().getId()
                    + " | Name: " + appointment.getPatient().getName()
                    + " | Doctor: Dr. " + appointment.getDoctor());

            current = current.next;
            position++;
        }
    }

    // Process next patient
    public Appointment processNextPatient() {

        if(head == null) {
            System.out.println("No patients waiting.");
            return null;
        }

        Appointment appointment = head.appointment;

        head = head.next;

        if(head == null) {
            tail = null;
        }

        System.out.println("Now processing: " + appointment.getPatient().getName()
                + " (" + appointment.getPriorityLabel() + ")");

        return appointment;
    }
}