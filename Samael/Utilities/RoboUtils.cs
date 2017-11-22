namespace kenran.Samael.Utilities
{
    using System;
    using kenran.Samael.Utilities.Generic;
    using Robocode;
    using Robocode.Util;

    internal static class RoboUtils
    {
        public static double WallSmoothing(Rectangle field, Point botPosition, double currentAngle, int orientation, double stickLength)
        {
            double targetAngle = currentAngle;
            while (!field.Contains(Project(botPosition, targetAngle, stickLength)))
            {
                targetAngle += orientation * 0.05;
            }

            return targetAngle;
        }

        public static Point Project(Point position, double angle, double distance)
        {
            var x = position.X + Math.Sin(angle) * distance;
            var y = position.Y + Math.Cos(angle) * distance;
            return new Point(x, y);
        }

        public static double AbsoluteBearing(Point position, Point targetPosition) =>
            Math.Atan2(targetPosition.X - position.X, targetPosition.Y - position.Y);

        public static double Limit(double min, double value, double max) =>
            Math.Max(min, Math.Min(value, max));

        public static double BulletSpeed(double power) =>
            20.0d - 3.0d * power;

        public static double MaxEscapeAngle(double speed) =>
            Math.Asin(8.0d / speed);

        public static void SetBackAsFront(AdvancedRobot bot, double targetAngle)
        {
            var angle = Utils.NormalRelativeAngle(targetAngle - bot.HeadingRadians);
            if (Math.Abs(angle) > Math.PI / 2.0d)
            {
                if (angle < 0)
                {
                    bot.SetTurnRightRadians(Math.PI + angle);
                }
                else
                {
                    bot.SetTurnLeftRadians(Math.PI - angle);
                }

                bot.SetBack(100);
            }
            else
            {
                if (angle < 0)
                {
                    bot.SetTurnLeftRadians(-angle);
                }
                else
                {
                    bot.SetTurnRightRadians(angle);
                }

                bot.SetAhead(100);
            }
        }
    }
}