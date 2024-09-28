package at.fhv.lka2.checker.violations;

public class ForStatementFormatViolations {

    public int bar() {
        for (int i = 0; i <= 10; i++) System.out.println(i);


        for (int i = 0; i <= 10; i++) {
            System.out.println(i);
        }
        return 1;
    }

    public int foo() {
        for ( int i = 0   ;   i <= 10  ;   i++   ) {
            System.out.println(i);
        }
        return 1;
    }
}
