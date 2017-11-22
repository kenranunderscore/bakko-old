namespace kenran.Samael
{
    using kenran.Samael.Movement;
    using kenran.Samael.Radar;
    using Robocode;

    public class Samael : AdvancedRobot
    {
        private LockingRadar radar_;
        private Surfboard surfboard_;

        public Samael()
        {
        }

        public override void Run()
        {
            IsAdjustRadarForGunTurn = true;
            IsAdjustGunForRobotTurn = true;

            radar_ = new LockingRadar(this);
            surfboard_ = new Surfboard(this);

            SetTurnRadarRightRadians(double.MaxValue);

            while (true)
            {
                Scan();
            }
        }

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            radar_.OnScannedRobot(evnt);
            surfboard_.OnScannedRobot(evnt);
        }

        public override void OnHitByBullet(HitByBulletEvent evnt)
        {
            surfboard_.OnHitByBullet(evnt);
        }
    }
}