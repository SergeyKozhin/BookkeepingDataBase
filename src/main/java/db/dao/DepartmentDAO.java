package db.dao;

import db.models.Department;
import oracle.jdbc.OracleTypes;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO implements DAO<Department> {
    private final DataSource ds;

    public DepartmentDAO(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public int add(Department department) {
        try (Connection connection = ds.getConnection();
             CallableStatement cs = connection.prepareCall("BEGIN INSERT INTO DEPARTMENTS (NAME) VALUES (?) RETURNING ID INTO ?; END;")) {

            cs.setString(1, department.getName());
            cs.registerOutParameter(2, OracleTypes.NUMBER);

            cs.execute();

            return cs.getInt(2);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding department", e);
        }
    }

    @Override
    public Department get(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT ID, NAME FROM DEPARTMENTS WHERE id = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractDepartment(rs);
                }

                throw new IllegalArgumentException("No department with id " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting department", e);
        }
    }

    @Override
    public void update(Department department) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE DEPARTMENTS SET NAME = ? WHERE ID = ?")) {

            ps.setString(1, department.getName());
            ps.setInt(2, department.getId());

            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("No department with id " + department.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating department", e);
        }
    }

    public void addEmployee(int depID, int employeeID) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO DEPARTMENTS_EMPLOYEES (DEPARTMENT_ID, EMPLOYEE_ID) VALUES (?, ?)")) {

            ps.setInt(1, depID);
            ps.setInt(2, employeeID);

            ps.execute();

        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                throw new IllegalArgumentException("No department with id " + depID + " or employee with id " + employeeID);
            }
            throw new RuntimeException("Error adding employee", e);
        }
    }

    public void deleteEmployee(int depID, int employeeID) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM DEPARTMENTS_EMPLOYEES WHERE DEPARTMENT_ID = ? AND EMPLOYEE_ID = ?")) {

            ps.setInt(1, depID);
            ps.setInt(2, employeeID);

            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting employee", e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM DEPARTMENTS WHERE ID = ?")) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting department", e);
        }
    }

    @Override
    public List<Department> getAll() {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT ID, NAME FROM DEPARTMENTS")) {

            try (ResultSet rs = ps.executeQuery()) {
                List<Department> departments = new ArrayList<>();
                while (rs.next()) {
                    departments.add(extractDepartment(rs));
                }

                return departments;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting departments", e);
        }
    }

    public List<Department> getByEmployee(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT D.ID, D.NAME FROM DEPARTMENTS D " +
                     "JOIN DEPARTMENTS_EMPLOYEES DE ON D.ID = DE.DEPARTMENT_ID WHERE DE.EMPLOYEE_ID = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                List<Department> departments = new ArrayList<>();
                while (rs.next()) {
                    departments.add(extractDepartment(rs));
                }

                return departments;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting departments", e);
        }
    }

    private Department extractDepartment(ResultSet rs) {
        Department department = new Department();
        try {
            department.setId(rs.getInt("id"));
            department.setName(rs.getString("name"));

            return department;
        } catch (SQLException e) {
            throw new RuntimeException("Error extracting data");
        }
    }
}
