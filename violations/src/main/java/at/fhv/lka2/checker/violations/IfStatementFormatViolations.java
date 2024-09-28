package at.fhv.lka2.checker.violations;

public class IfStatementFormatViolations {

    public int bar() {
        int i = bar();

        if (i < 20) {
            return 2;
        } else return 1;
    }

    public int foo() {
        int i = foo();

        if (i < 10) {
            return 2;
        }

        if (i < 15) return 2;

        if ( i < 17) {
            return 2;
        }

        if (i < 19 ) {
            return 2;
        }

        return 20;
    }
}
