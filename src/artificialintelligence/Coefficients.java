package artificialintelligence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Coefficients implements Serializable {
    int actualValue;
    double actalValueCoefficient;

    public Coefficients(int actual){
        this.actualValue = actual;
        this.actalValueCoefficient = 0.5;
    }
    public Coefficients(int actual, double coefficient){
        this.actualValue = actual;
        this.actalValueCoefficient = coefficient;
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

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        actualValue = is.readInt();
        actalValueCoefficient = is.readDouble();
    }

    private void writeObject(ObjectOutputStream os) throws IOException{
        os.writeInt(actualValue);
        os.writeDouble(actalValueCoefficient);
    }
}
