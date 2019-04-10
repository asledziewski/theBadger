package explorer;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;

public class MyConstructor {
    private final CtConstructor ctConstructor;

    public MyConstructor(CtConstructor ctConstructor) {
        this.ctConstructor = ctConstructor;
    }

    public CtConstructor getCtConstructor() {
        return ctConstructor;
    }

    @Override
    public String toString() {
        StringBuilder fullName = new StringBuilder(ctConstructor.getName() + "(");
        CtClass params[] = new CtClass[0];
        try {
            params = ctConstructor.getParameterTypes();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        int p;
        if (params.length == 0) {
            fullName.append(")");
        } else if (params.length == 1) {
            fullName.append(params[0].getName()).append(")");
        } else {
            for (p = 0; p < params.length - 1; p++)
                fullName.append(params[p].getName()).append(", ");
            fullName.append(params[((params.length)) - 1].getName()).append(")");
        }
        return Explorer.decodeMod(ctConstructor.getModifiers()) + fullName.toString();
    }


}
