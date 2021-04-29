package db.dao;

import db.models.Project;
import oracle.jdbc.OracleTypes;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO implements DAO<Project> {
    private final DataSource ds;

    public ProjectDAO(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public int add(Project project) {
        try (Connection connection = ds.getConnection();
             CallableStatement cs = connection.prepareCall("BEGIN INSERT INTO PROJECTS (NAME, COST, DEPARTMENT_ID, DATE_BEG, DATE_END) VALUES (?, ?, ?, ?, ?) RETURNING ID INTO ?; END;")) {

            cs.setString(1, project.getName());
            cs.setDouble(2, project.getCost());
            cs.setInt(3, project.getDepartmentID());
            cs.setDate(4, project.getDateBegin());
            cs.setDate(5, project.getDateEnd());
            cs.registerOutParameter(6, OracleTypes.NUMBER);

            cs.execute();

            return cs.getInt(6);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding project", e);
        }
    }

    @Override
    public Project get(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT ID, NAME, COST, DEPARTMENT_ID, DATE_BEG, " +
                     "DATE_END, DATE_END_REAL FROM PROJECTS WHERE ID = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractProject(rs);
                }

                throw new IllegalArgumentException("No project with id " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting project", e);
        }
    }

    @Override
    public void update(Project project) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE PROJECTS SET NAME = ?, COST = ?, " +
                     "DEPARTMENT_ID = ?, DATE_BEG = ?, DATE_END = ?, DATE_END_REAL = ? WHERE ID = ?")) {

            ps.setString(1, project.getName());
            ps.setDouble(2, project.getCost());
            ps.setInt(3, project.getDepartmentID());
            ps.setDate(4, project.getDateBegin());
            ps.setDate(5, project.getDateEnd());
            ps.setDate(6, project.getDateEndReal());
            ps.setInt(7, project.getId());

            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("No project with id " + project.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating project", e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM PROJECTS WHERE ID = ?")) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project", e);
        }
    }

    @Override
    public List<Project> getAll() {
        try (Connection connection = ds.getConnection();
             Statement ps = connection.createStatement()) {

            try (ResultSet rs = ps.executeQuery("SELECT ID, NAME, COST, DEPARTMENT_ID, DATE_BEG, " +
                    "DATE_END, DATE_END_REAL FROM PROJECTS")) {
                List<Project> projects = new ArrayList<>();
                while (rs.next()) {
                    projects.add(extractProject(rs));
                }

                return projects;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting projects", e);
        }
    }

    public List<Project> getByDepartment(int id) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT ID, NAME, COST, DEPARTMENT_ID, DATE_BEG, " +
                     "DATE_END, DATE_END_REAL FROM PROJECTS WHERE DEPARTMENT_ID = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                List<Project> projects = new ArrayList<>();
                while (rs.next()) {
                    projects.add(extractProject(rs));
                }

                return projects;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting projects", e);
        }
    }

    public List<Project> getWithIncomes() {
        try (Connection connection = ds.getConnection();
             Statement ps = connection.createStatement()) {

            try (ResultSet rs = ps.executeQuery("SELECT ID, NAME, INCOME FROM PROJECTS_WITH_INCOME")) {
                List<Project> projects = new ArrayList<>();
                while (rs.next()) {
                    Project cur = new Project();
                    cur.setId(rs.getInt("ID"));
                    cur.setName(rs.getString("NAME"));
                    cur.setIncome(rs.getDouble("INCOME"));

                    projects.add(cur);
                }

                return projects;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting projects", e);
        }
    }

    private Project extractProject(ResultSet rs) {
        Project project = new Project();
        try {
            project.setId(rs.getInt("ID"));
            project.setName(rs.getString("NAME"));
            project.setCost(rs.getDouble("COST"));
            project.setDepartmentID(rs.getInt("DEPARTMENT_ID"));
            project.setDateBegin(rs.getDate("DATE_BEG"));
            project.setDateEnd(rs.getDate("DATE_END"));
            project.setDateEndReal(rs.getDate("DATE_END_REAL"));

            return project;
        } catch (SQLException e) {
            throw new RuntimeException("Error extracting data");
        }
    }
}
