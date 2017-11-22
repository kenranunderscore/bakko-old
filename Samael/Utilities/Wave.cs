namespace kenran.Samael.Utilities
{
    using kenran.Samael.Utilities.Generic;

    internal struct Wave
    {
        public Point FirePosition { get; set; }

        public long FireTime { get; set; }

        public double BulletSpeed { get; set; }

        public double Angle { get; set; }

        public double DistanceTraveled { get; set; }

        public int Direction { get; set; }

        public bool IsEmpty => FireTime == 0;
    }
}