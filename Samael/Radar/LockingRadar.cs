namespace kenran.Samael.Radar
{
    using Robocode;
    using Robocode.Util;

    internal sealed class LockingRadar
    {
        private const double LockMultiplier = 2.0d;
        private readonly AdvancedRobot bot_;

        internal LockingRadar(AdvancedRobot bot)
        {
            bot_ = bot;
        }

        internal void OnScannedRobot(ScannedRobotEvent evnt)
        {
            double angleToEnemy = bot_.HeadingRadians + evnt.BearingRadians - bot_.RadarHeadingRadians;
            double radarTurn = Utils.NormalRelativeAngle(angleToEnemy);
            bot_.SetTurnRadarRightRadians(LockMultiplier * radarTurn);
        }
    }
}