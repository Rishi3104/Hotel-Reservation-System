
import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String url="jdbc:mysql://localhost:3306/hotel_db";
    private static final String username="root";
    private static final String password="Rishi#3104";

    public static void main(String[] args) throws ClassNotFoundException,SQLException {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            while (true){
                System.out.println();
                System.out.println("HOTEL RESERVATION SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Make a Reservation");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice){
                    case 1 ->{
                        Reservation(connection,scanner);
                        break;
                    }
                    case 2 ->{
                        viewReservation(connection);
                        break;
                    }
                    case 3->{
                        getRoomNumber(connection,scanner);
                        break;
                    }
                    case 4->{
                        updateReservation(connection,scanner);
                        break;
                    }
                    case 5->{
                        deleteReservation(connection,scanner);
                        break;
                    }
                    case 6->{
                        scanner.close();
                        exit();
                        System.exit(0);
                    }
                    default -> {
                        System.out.println("Invalid choice, Enter a valid choice");
                    }
                }

            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException();
        }
    }

    private static void Reservation(Connection connection,Scanner scanner){

        System.out.print("Enter guest name: ");
        String guestName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter room number: ");
        int roomNumber = scanner.nextInt();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.next();

        String sql = "INSERT INTO reservation (guest_name, room_number, contact_number) " +
                "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

        try{
            Statement statement = connection.createStatement();
            int affectedRow = statement.executeUpdate(sql);
            if(affectedRow>0){
                System.out.println("Reservation Successfull");
            }else{
                System.out.println("Reservation Failed");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection){

        String sql="SELECT * from reservation";
        try(Statement statement = connection.createStatement();){
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void getRoomNumber(Connection connection,Scanner scanner){
        System.out.println("Enter Reservation ID: ");
        int reservationId=scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter guest name: ");
        String guestName=scanner.next();

        String sql = "SELECT room_number FROM reservation " +
                "WHERE reservation_id = " + reservationId +
                " AND guest_name = '" + guestName + "'";

        try(Statement statement= connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room number for Reservation ID " + reservationId +
                        " and Guest " + guestName + " is: " + roomNumber);
            }else{
                System.out.println("No guest found with name, "+guestName+" and reservation ID "+reservationId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection,Scanner scanner){
        System.out.print("Enter reservation ID to update: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        System.out.print("Enter new guest name: ");
        String newGuestName = scanner.nextLine();
        System.out.print("Enter new room number: ");
        int newRoomNumber = scanner.nextInt();
        System.out.print("Enter new contact number: ");
        String newContactNumber = scanner.next();

        String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                "room_number = " + newRoomNumber + ", " +
                "contact_number = '" + newContactNumber + "' " +
                "WHERE reservation_id = " + reservationId;

        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows > 0) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Reservation update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection,Scanner scanner){

        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }

    private static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}
