package explorer;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.Objects;

public class MyMethod {
    private final CtMethod ctMethod;

    public MyMethod(CtMethod ctMethod) {
        this.ctMethod = ctMethod;
    }

    public CtMethod getCtMethod() {
        return ctMethod;
    }

    @Override
    public String toString() {
        StringBuilder fullName = null;
        try {
            fullName = new StringBuilder(ctMethod.getReturnType().getName() + " " + ctMethod.getName() + "(");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        CtClass params[] = new CtClass[0];
        try {
            params = ctMethod.getParameterTypes();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        int p;
        if (params.length == 0) {
            Objects.requireNonNull(fullName).append(")");
        } else if (params.length == 1) {
            Objects.requireNonNull(fullName).append(params[0].getName()).append(")");
        } else {
            for (p = 0; p < params.length - 1; p++)
                Objects.requireNonNull(fullName).append(params[p].getName()).append(", ");
            fullName.append(params[((params.length)) - 1].getName()).append(")");
        }
        return Explorer.decodeMod(ctMethod.getModifiers()) + fullName.toString();
    }
}
