package eutros.botaniapp.common.utils;

public class MathUtils {

    public static int gcf(int a, int b)
    {
        while (a != b)
        {
            if (a > b) a -= b;
            else b -= a;
        }
        return a;
    }

    public static int lcm(int a, int b)
    {
        return (a * b) / gcf(a, b);
    }

    private MathUtils() {}
}
