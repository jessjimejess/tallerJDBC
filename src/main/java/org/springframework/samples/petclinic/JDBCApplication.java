// El ejercicio comienza en la línea 132
// Jesús Jiménez Romero

package org.springframework.samples.petclinic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

public class JDBCApplication {

	// Función para introducir Owner en BD, devuelve el id del objeto introducido
	// para poder llevar a cabo la relación con Pet
	public static int insertOwner(Owner owner, PreparedStatement preparedstatement, Connection connection) {

		String sql = "INSERT INTO owners (first_name, last_name, address, city, telephone) VALUES (?,?,?,?,?)";
		int result = 0;
		try {
			preparedstatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedstatement.setString(1, owner.getFirstName());
			preparedstatement.setString(2, owner.getLastName());
			preparedstatement.setString(3, owner.getAddress());
			preparedstatement.setString(4, owner.getCity());
			preparedstatement.setString(5, owner.getTelephone());

			preparedstatement.executeUpdate();
			ResultSet rs = preparedstatement.getGeneratedKeys();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			preparedstatement.close();

		} catch (SQLException e) {
			System.out.println("Error al introducir los datos");
			e.printStackTrace();

		}
		return result;

	}

	public static int insertPet(Pet pet, int result, PreparedStatement preparedstatement, Connection connection) {

		String sql = "INSERT INTO pets (name, birth_date, type_id, owner_id) VALUES (?,?,?,?)";

		// ---------- Conversión de Date util a Date sql -------
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");
		String str = formatter.format(pet.getBirthDate());
		java.sql.Date date = java.sql.Date.valueOf(str);
		// -----------------------------------------------------

		try {
			preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setString(1, pet.getName());
			preparedstatement.setDate(2, date);
			preparedstatement.setInt(3, 2);
			preparedstatement.setInt(4, result);

			preparedstatement.executeUpdate();
			preparedstatement.close();

		} catch (SQLException e) {
			System.out.println("Error al introducir los datos mascota");
			e.printStackTrace();
		}
		return result;

	}
	
	public static void deleteAll(int result, PreparedStatement preparedstatement, Connection connection) {

		String sql = "DELETE FROM pets WHERE owner_id = ?";
		
		
		try {
			preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setInt(1,result);
			preparedstatement.executeUpdate();
			preparedstatement.close();

		} catch (SQLException e) {
			System.out.println("Error al eliminar");
			e.printStackTrace();
		}
		
		String sql2 = "DELETE FROM owners WHERE id = ?";
		try {
			preparedstatement = connection.prepareStatement(sql2);
			preparedstatement.setInt(1,result);
			preparedstatement.executeUpdate();
			preparedstatement.close();

		} catch (SQLException e) {
			System.out.println("Error al eliminar");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.out.println("-------- Test de conexión con MySQL ------------");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("No encuentro el driver en el Classpath");
			e.printStackTrace();
			return;
		}

		System.out.println("Driver instalado y funcionando");
		Connection connection = null;
		Statement statement = null;
		PreparedStatement preparedstatement = null;

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/petclinic?useSSL=false&serverTimezone=UTC", "root", "jessjimejess");
			if (connection != null)
				System.out.println("Conexión establecida");

			// Comienzo del ejercicio - Creación de objeto Owner
			Owner owner = new Owner();
			owner.setFirstName("Jesus");
			owner.setLastName("Jimenez");
			owner.setAddress("calle");
			owner.setCity("ciudad");
			owner.setTelephone("666555444");

			int result = insertOwner(owner, preparedstatement, connection);

			// Creación de objeto Pet
			Pet pet = new Pet();
			PetType type = new PetType();
			pet.setName("Manchitas");
			pet.setType(type);
			SimpleDateFormat dateformat3 = new SimpleDateFormat("dd-MM-yyyy");
			Date date2 = null;
			try {
				date2 = dateformat3.parse("1-1-2006");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			pet.setBirthDate(date2);

			insertPet(pet, result, preparedstatement, connection);
			deleteAll(result, preparedstatement, connection);
			// Fin del ejercicio

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		} finally {
			try {
				if (statement != null)
					connection.close();
			} catch (SQLException se) {

			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

	}

}
