#include <stdio.h>

int main() {
    int i = 0;
    if (i > 0) {
        return 1;
    }

    if (i < 20) {
        return 2;
    } else return 1;

    if ( i < 17) {
        return 2;
    }

    if (i < 19 ) {
        return 2;
    }

    if (i < 20) {
        return 2;
    } else return 1;

    return 0;
}
