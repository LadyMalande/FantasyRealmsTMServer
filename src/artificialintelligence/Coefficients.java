package artificialintelligence;

public class Coefficients {
    int actualValue;
    double actalValueCoefficient;

    public Coefficients(int actual){
        this.actualValue = actual;
        this.actalValueCoefficient = 0.5;
    }

    public void setActualValue(int actualValue) {
        this.actualValue = actualValue;
    }

    public void setActalValueCoefficient(double actalValueCoefficient) {
        this.actalValueCoefficient = actalValueCoefficient;
    }

    public double getActalValueCoefficient() {
        return actalValueCoefficient;
    }

    public int getActualValue() {
        return actualValue;
    }
}
