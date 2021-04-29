package db.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Date;

public class Project {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty cost = new SimpleDoubleProperty();
    private final IntegerProperty departmentID = new SimpleIntegerProperty();
    private final ObjectProperty<Date> dateBegin = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> dateEnd = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> dateEndReal = new SimpleObjectProperty<>();
    private final DoubleProperty income = new SimpleDoubleProperty();

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getCost() {
        return cost.get();
    }

    public DoubleProperty costProperty() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost.set(cost);
    }


    public Date getDateBegin() {
        return dateBegin.get();
    }

    public ObjectProperty<Date> dateBeginProperty() {
        return dateBegin;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin.set(dateBegin);
    }

    public Date getDateEnd() {
        return dateEnd.get();
    }

    public ObjectProperty<Date> dateEndProperty() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd.set(dateEnd);
    }

    public Date getDateEndReal() {
        return dateEndReal.get();
    }

    public ObjectProperty<Date> dateEndRealProperty() {
        return dateEndReal;
    }

    public void setDateEndReal(Date dateEndReal) {
        this.dateEndReal.set(dateEndReal);
    }

    public int getDepartmentID() {
        return departmentID.get();
    }

    public IntegerProperty departmentIDProperty() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID.set(departmentID);
    }

    public double getIncome() {
        return income.get();
    }

    public DoubleProperty incomeProperty() {
        return income;
    }

    public void setIncome(double income) {
        this.income.set(income);
    }
}
