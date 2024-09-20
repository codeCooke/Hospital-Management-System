package HospitalManagementSystem;

import javax.print.Doc;
import java.sql.*;
import java.util.*;

public class HospitalManagementtSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "MySQL@1234";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while(true){
                System.out.println("Hospital Management System");
                System.out.println("1. Add patient");
                System.out.println("2. View patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice");

                int choice = scanner.nextInt();

                switch(choice){
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;

                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient , doctor , connection , scanner);
                        System.out.println();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Enter valid choice");
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.println("Enter patient id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter doctor id");
        int doctorId = scanner.nextInt();
        System.out.println("enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if(patient.getPatientById(patientId) && doctor.getDoctorsById(doctorId)){
            if(checkDoctorAvailability(doctorId , appointmentDate , connection)){
                String appointmentQuery = " INSERT INTO appointments(patient_id , doctor_id , appointment_date) VALUES(?,?,?)";

                try{
                    PreparedStatement ps = connection.prepareStatement(appointmentQuery);
                    ps.setInt(1,patientId);
                    ps.setInt(2,doctorId);
                    ps.setString(3,appointmentDate);
                    int rowsaffected = ps.executeUpdate();
                    if(rowsaffected>0){
                        System.out.println("appointment booked");
                    }else{
                        System.out.println("failed to book appointment");
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }

            }else{
                System.out.println("Doctor not available on this dste");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exist");
        }


    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate , Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, doctorId);
            ps.setString(2, appointmentDate);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                int count = rs.getInt(1);
                if(count == 0){
                    return true;
                }else{
                    return false;
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;


    }

}
