package explorer;

import javassist.CtClass;

public class MyClass {
    private final CtClass ctClass;
    private final boolean added;

    public MyClass(CtClass ctClass) {
        this.ctClass = ctClass;
        this.added = false;
    }

    public MyClass(CtClass ctClass, boolean added) {
        this.ctClass = ctClass;
        this.added = added;
    }


    public CtClass getCtClass() {
        return ctClass;
    }

    public boolean getAdded() {
        return added;
    }

    @Override
    public String toString() {
        return Explorer.decodeMod(ctClass.getModifiers()) + ctClass.getName();
    }
}
