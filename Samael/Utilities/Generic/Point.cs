namespace kenran.Samael.Utilities.Generic
{
    using System;

    public struct Point
    {
        public Point(double x, double y)
        {
            X = x;
            Y = y;
        }

        public double X { get; set; }

        public double Y { get; set; }

        public double Distance(Point other)
        {
            double xDist = other.X - X;
            double yDist = other.Y - Y;
            return Math.Sqrt(xDist * xDist + yDist * yDist);
        }
    }
}