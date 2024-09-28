package at.fhv.lka2.checker.violations;

public class SwitchStatementFormatViolations {

    public int bar() {
        int i = bar();

        switch   (   i  )  {
            case 3:
                break;
        }

        switch (i) {
            case 3:
                break;
        }

        return 1;
    }
}
