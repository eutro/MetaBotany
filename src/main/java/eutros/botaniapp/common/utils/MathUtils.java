package eutros.botaniapp.common.utils;

import java.awt.*;

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

    public static Point rotatePointAbout(Point in, Point about, double degrees) {
        double rad = degrees * Math.PI / 180.0;
        double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
        double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
        return new Point((int) newX, (int) newY);
    }

    private MathUtils() {}
}
