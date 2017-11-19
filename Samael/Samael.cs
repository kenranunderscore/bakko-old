namespace kenran.Samael
{
    using kenran.Samael.Radar;
    using Robocode;

    public class Samael : AdvancedRobot
    {
        private readonly LockingRadar radar_;

        public Samael()
        {
            radar_ = new LockingRadar(this);
        }

        public override void Run()
        {
            IsAdjustRadarForGunTurn = true;
            IsAdjustGunForRobotTurn = true;

            SetTurnRadarRightRadians(double.MaxValue);

            while (true)
            {
                Scan();
            }
        }

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            radar_.OnScannedRobot(evnt);
        }
    }
}