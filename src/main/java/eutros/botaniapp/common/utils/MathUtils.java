package eutros.botaniapp.common.utils;

import net.minecraft.util.Direction;

import java.awt.geom.Point2D;

public class MathUtils {

    public static final Direction[] HORIZONTALS = {Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};

    private MathUtils() {
    }

    public static int gcf(int a, int b) {
        while(a != b) {
            if(a > b) a -= b;
            else b -= a;
        }
        return a;
    }

    public static int lcm(int a, int b) {
        return (a * b) / gcf(a, b);
    }

    public static Point2D rotatePointAbout(Point2D in, Point2D about, double degrees) {
        double rad = degrees * Math.PI / 180.0;
        double newX = Math.cos(rad) * (in.getX() - about.getX()) - Math.sin(rad) * (in.getY() - about.getY()) + about.getX();
        double newY = Math.sin(rad) * (in.getX() - about.getX()) + Math.cos(rad) * (in.getY() - about.getY()) + about.getY();
        return new Point2D.Double(newX, newY);
    }

    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        return val.compareTo(min) < 0 ? min :
               val.compareTo(max) > 0 ? max :
               val;
    }

}
