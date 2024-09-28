package at.fhv.lka2.checker.violations;

public class WhileStatementFormatViolations {

    public int bar() {
        int i = bar();
        while (i < 10) System.out.println(i);

        while (i < 13) {
            System.out.println(i);
        }
        return 1;
    }

    public int foo() {
        int i = foo();
        while ( i < 13 ) {
            System.out.println(i);
        }
        return 1;
    }
}
