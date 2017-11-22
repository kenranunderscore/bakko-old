namespace kenran.Samael.Utilities.Generic
{
    public struct Rectangle
    {
        public Rectangle(double left, double bottom, double width, double height)
        {
            Left = left;
            Bottom = bottom;
            Width = width;
            Height = height;
        }

        public double Left { get; set; }

        public double Bottom { get; set; }

        public double Width { get; set; }

        public double Height { get; set; }

        public double Right => Left + Width;

        public double Top => Bottom + Height;

        public bool Contains(Point p) =>
            Left <= p.X &&
            Bottom <= p.Y &&
            Top >= p.Y &&
            Right >= p.X;
    }
}