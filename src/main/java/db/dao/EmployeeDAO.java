package db.dao;

import db.models.Employee;
import oracle.jdbc.OracleTypes;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmployeeDAO implements DAO<Employee> {
    private final DataSource ds;

    public EmployeeDAO(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public int add(Employee employee) {
        try (Connection connection = ds.getConnection();
             CallableStatement cs = connection.prepareCall("BEGIN INSERT INTO EMPLOYESS (FIRST_NAME, LAST_NAME, PATHER_NAME, POSITION, SALARY) VALUES (?, ?, ?, ?, ?) RETURNING ID INTO ?; END;")) {

            cs.setString(1, employee.getFirstName());
            cs.setString(2, employee.getLastName());
            cs.setString(3, employee.getPatherName());
            cs.setString(4, employee.getPosition());
            cs.setDouble(5, employee.getSalary());
            cs.registerOutParameter(6, OracleTypes.NUMBER);

            cs.execute();

            return cs.getInt(6);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding employee", e);
        }
    }

    @Override
    public Employee get(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT ID, FIRST_NAME, LAST_NAME, PATHER_NAME, " +
                     "POSITION, SALARY FROM EMPLOYESS WHERE ID = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }

                throw new IllegalArgumentException("No employee with id " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting employee", e);
        }
    }

    @Override
    public void update(Employee employee) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE EMPLOYESS SET FIRST_NAME = ?, LAST_NAME = ?, PATHER_NAME = ?, POSITION = ?, SALARY = ? WHERE ID = ?")) {

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getPatherName());
            ps.setString(4, employee.getPosition());
            ps.setDouble(5, employee.getSalary());
            ps.setInt(6, employee.getId());

            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("No employee with id " + employee.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating employee", e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM EMPLOYESS WHERE ID = ?")) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting employee", e);
        }
    }

    @Override
    public List<Employee> getAll() {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT ID, FIRST_NAME, LAST_NAME, PATHER_NAME, " +
                     "POSITION, SALARY FROM EMPLOYESS")) {

            try (ResultSet rs = ps.executeQuery()) {
                List<Employee> employees = new ArrayList<>();
                while (rs.next()) {
                    employees.add(extractEmployee(rs));
                }

                return employees;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting employees", e);
        }
    }

    public List<Employee> getByDepartment(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT E.ID, FIRST_NAME, LAST_NAME, PATHER_NAME, POSITION, SALARY FROM EMPLOYESS E " +
                     "JOIN DEPARTMENTS_EMPLOYEES DE on E.ID = DE.EMPLOYEE_ID WHERE DE.DEPARTMENT_ID = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                List<Employee> employees = new ArrayList<>();
                while (rs.next()) {
                    employees.add(extractEmployee(rs));
                }

                return employees;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting employees", e);
        }
    }

    private Employee extractEmployee(ResultSet rs) {
        Employee employee = new Employee();
        try {
            employee.setId(rs.getInt("ID"));
            employee.setFirstName(rs.getString("FIRST_NAME"));
            employee.setLastName(rs.getString("LAST_NAME"));
            employee.setPatherName(Objects.requireNonNullElse(rs.getString("PATHER_NAME"), ""));
            employee.setPosition(rs.getString("POSITION"));
            employee.setSalary(rs.getDouble("SALARY"));

            return employee;
        } catch (SQLException e) {
            throw new RuntimeException("Error extracting data");
        }
    }
}
