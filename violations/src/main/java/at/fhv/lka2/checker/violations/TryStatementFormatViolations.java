package at.fhv.lka2.checker.violations;

public class TryStatementFormatViolations {

    public int bar() {
        int i = bar();

        try {

        } catch (Exception e) {
            throw e;
        }

        try    {

        }   catch    (Exception e) {
            throw e;
        }
        return 1;
    }
}
