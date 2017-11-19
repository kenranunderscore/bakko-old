namespace kenran.Samael
{
    using Robocode;
    using Robocode.Util;

    public class Samael : AdvancedRobot
    {
        public override void Run()
        {
            SetTurnRadarRightRadians(double.MaxValue);

            while (true)
            {
                Execute();
            }
        }

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            SetTurnRadarRightRadians(2.0d * Utils.NormalRelativeAngle(HeadingRadians + evnt.BearingRadians - RadarHeadingRadians));
        }
    }
}