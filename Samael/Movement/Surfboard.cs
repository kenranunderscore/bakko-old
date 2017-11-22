namespace kenran.Samael.Movement
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Runtime.InteropServices;
    using kenran.Samael.Utilities;
    using kenran.Samael.Utilities.Generic;
    using Robocode;
    using Robocode.Util;

    internal class Surfboard
    {
        private const int Bins = 47;
        private const double WallStickLength = 160d;
        private static readonly double[] SurfStats = new double[Bins];
        private static double enemyEnergy_ = 100d;
        private Point position_;
        private Point enemyPosition_;
        private readonly Rectangle field_;
        private readonly AdvancedRobot bot_;

        private readonly IList<Wave> enemyWaves_ = new List<Wave>();
        private readonly IList<int> surfDirections_ = new List<int>();
        private readonly IList<double> absoluteBearings_ = new List<double>();

        private static double MiddleBin => (Bins - 1d) / 2d;

        public Surfboard(AdvancedRobot bot)
        {
            bot_ = bot;
            double halfBotWidth = bot_.Width / 2d;
            field_ = new Rectangle(halfBotWidth, halfBotWidth, bot_.BattleFieldWidth - bot_.Width, bot_.BattleFieldHeight - bot.Width);
        }

        public void OnScannedRobot(ScannedRobotEvent evnt)
        {
            position_ = new Point(bot_.X, bot_.Y);
            double lateralVelocity = bot_.Velocity * Math.Sin(evnt.BearingRadians);
            double absoluteBearing = evnt.BearingRadians + bot_.HeadingRadians;

            surfDirections_.Insert(0, lateralVelocity >= 0 ? 1 : -1);
            absoluteBearings_.Insert(0, absoluteBearing + Math.PI);

            double bulletPower = enemyEnergy_ - evnt.Energy;
            if (bulletPower < 3.01d && bulletPower > 0.09d && surfDirections_.Count > 2)
            {
                var wave = new Wave
                {
                    FireTime = bot_.Time - 1,
                    BulletSpeed = RoboUtils.BulletSpeed(bulletPower),
                    DistanceTraveled = RoboUtils.BulletSpeed(bulletPower),
                    Direction = surfDirections_[2],
                    Angle = absoluteBearings_[2],
                    FirePosition = enemyPosition_
                };

                enemyWaves_.Add(wave);
            }

            enemyEnergy_ = evnt.Energy;
            enemyPosition_ = RoboUtils.Project(position_, absoluteBearing, evnt.Distance);

            UpdateWaves();
            Surf();
        }

        public void OnHitByBullet(HitByBulletEvent e)
        {
            if (enemyWaves_.Any())
            {
                var hitBulletPosition = new Point(e.Bullet.X, e.Bullet.Y);
                Wave hitWave = default;

                for (int i = 0; i < enemyWaves_.Count; i++)
                {
                    var wave = enemyWaves_[i];

                    if (Math.Abs(wave.DistanceTraveled - position_.Distance(wave.FirePosition)) < 50d
                        && Math.Abs(RoboUtils.BulletSpeed(e.Bullet.Power) - wave.BulletSpeed) < 0.001d)
                    {
                        hitWave = wave;
                        break;
                    }
                }

                if (!hitWave.IsEmpty)
                {
                    LogHit(hitWave, hitBulletPosition);
                    enemyWaves_.Remove(hitWave);
                }
            }
        }

        private void UpdateWaves()
        {
            for (int i = 0; i < enemyWaves_.Count; i++)
            {
                var wave = enemyWaves_[i];
                wave.DistanceTraveled = (bot_.Time - wave.FireTime) * wave.BulletSpeed;
                if (wave.DistanceTraveled > position_.Distance(wave.FirePosition) + 50)
                {
                    enemyWaves_.RemoveAt(i);
                    i--;
                }
            }
        }

        private Wave ClosestSurfableWave()
        {
            double closestDistance = 50000;
            Wave targetWave = default;
            for (int i = 0; i < enemyWaves_.Count; i++)
            {
                var wave = enemyWaves_[i];
                double distance = position_.Distance(wave.FirePosition) - wave.DistanceTraveled;
                if (distance > wave.BulletSpeed && distance < closestDistance)
                {
                    targetWave = wave;
                    closestDistance = distance;
                }
            }

            return targetWave;
        }

        private static int GetFactorIndex(Wave wave, Point targetPosition)
        {
            double offsetAngle = RoboUtils.AbsoluteBearing(wave.FirePosition, targetPosition) - wave.Angle;
            double factor = Utils.NormalRelativeAngle(offsetAngle) / RoboUtils.MaxEscapeAngle(wave.BulletSpeed) * wave.Direction;
            return (int)RoboUtils.Limit(0, (factor + 1) * MiddleBin, Bins - 1d);
        }

        private void LogHit(Wave wave, Point targetPosition)
        {
            int index = GetFactorIndex(wave, targetPosition);
            for (int i = 0; i < Bins; i++)
            {
                SurfStats[i] += 1d / (1 + Math.Pow(index - i, 2));
            }
        }

        private Point PredictPosition(Wave surfWave, int direction)
        {
            Point predictedPosition = position_;
            double predictedVelocity = bot_.Velocity;
            double predictedHeading = bot_.HeadingRadians;

            int counter = 0;
            bool intercepted = default;

            do
            {
                var moveAngle = RoboUtils.WallSmoothing(
                                       field_,
                                       predictedPosition,
                                       RoboUtils.AbsoluteBearing(surfWave.FirePosition, predictedPosition) + direction * (Math.PI / 2d),
                                       direction,
                                       WallStickLength) - predictedHeading;
                double moveDir = 1;

                if (Math.Cos(moveAngle) < 0)
                {
                    moveAngle += Math.PI;
                    moveDir = -1;
                }

                moveAngle = Utils.NormalRelativeAngle(moveAngle);
                var maxTurning = Math.PI / 720d * (40d - 3d * Math.Abs(predictedVelocity));
                predictedHeading = Utils.NormalRelativeAngle(predictedHeading + RoboUtils.Limit(-maxTurning, moveAngle, maxTurning));
                predictedVelocity += (predictedVelocity * moveDir < 0 ? 2 * moveDir : moveDir);
                predictedVelocity = RoboUtils.Limit(-8, predictedVelocity, 8);
                predictedPosition = RoboUtils.Project(predictedPosition, predictedHeading, predictedVelocity);

                counter++;

                double distance = predictedPosition.Distance(surfWave.FirePosition);
                if (distance < surfWave.DistanceTraveled + (counter + 1) * surfWave.BulletSpeed)
                {
                    intercepted = true;
                }
            } while (!intercepted && counter < 500);

            return predictedPosition;
        }

        private double CheckDanger(Wave surfWave, int direction)
        {
            int index = GetFactorIndex(surfWave, PredictPosition(surfWave, direction));
            return SurfStats[index];
        }

        private void Surf()
        {
            var surfWave = ClosestSurfableWave();
            if (surfWave.IsEmpty)
            {
                return;
            }

            double dangerLeft = CheckDanger(surfWave, -1);
            double dangerRight = CheckDanger(surfWave, 1);

            double goAngle = RoboUtils.AbsoluteBearing(surfWave.FirePosition, position_);
            if (dangerLeft < dangerRight)
            {
                goAngle = RoboUtils.WallSmoothing(field_, position_, goAngle - (Math.PI / 2d), -1, WallStickLength);
            }
            else
            {
                goAngle = RoboUtils.WallSmoothing(field_, position_, goAngle + Math.PI / 2d, 1, WallStickLength);
            }

            RoboUtils.SetBackAsFront(bot_, goAngle);
        }
    }
}