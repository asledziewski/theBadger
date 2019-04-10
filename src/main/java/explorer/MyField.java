package explorer;

import javassist.CtField;
import javassist.NotFoundException;

public class MyField {
    private final CtField ctField;

    public MyField(CtField ctField) {
        this.ctField = ctField;
    }

    @Override
    public String toString() {
        String fullName = "";
        try {
            return (ctField.getType().getName() + " " + ctField.getName());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return Explorer.decodeMod(ctField.getModifiers()) + fullName;
    }

    public CtField getCtField() {
        return ctField;
    }
}
