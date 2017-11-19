namespace kenran.Samael.Radar
{
    using Robocode;
    using Robocode.Util;

    internal sealed class LockingRadar
    {
        private const double LockMultiplier = 2.0d;
        private readonly AdvancedRobot bot;

        internal LockingRadar(AdvancedRobot bot)
        {
            this.bot = bot;
        }

        internal void OnScannedRobot(ScannedRobotEvent evnt)
        {
            double radarTurn = Utils.NormalRelativeAngle(bot.HeadingRadians + evnt.BearingRadians - bot.RadarHeadingRadians);
            bot.SetTurnRadarRightRadians(LockMultiplier * radarTurn);
        }
    }
}