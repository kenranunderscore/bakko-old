using kenran.Samael.Radar;

namespace kenran.Samael
{
    using Robocode;

    public class Samael : AdvancedRobot
    {
        private readonly LockingRadar radar;

        public Samael()
        {
            radar = new LockingRadar(this);
        }

        public override void Run()
        {
            SetTurnRadarRightRadians(double.MaxValue);

            while (true)
            {
                Scan();
            }
        }

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            radar.OnScannedRobot(evnt);
        }
    }
}