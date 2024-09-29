package at.fhv.lka2.checker.violations;

public class ConstantNameViolation {

    final public int foo = 1;

    public void goo() {
        final int bar = 1;
    }
}
