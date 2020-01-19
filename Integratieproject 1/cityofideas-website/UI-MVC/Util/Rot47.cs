namespace COI.UI_MVC.Util
{
    /// <summary>
    /// Alternative for ROT13
    /// <see cref="https://en.wikipedia.org/wiki/ROT13"/>
    /// </summary>
    internal static class Rot47
    {
        private static readonly int MIN_ASCII = 33;
        private static readonly int MAX_ASCII = 126;

        internal static string Rotate(string str)
        {
            char[] charArray = str.ToCharArray();

            for (var i = 0; i < charArray.Length; i++)
            {
                int asciiVal = charArray[i];

                if (charArray[i] >= MIN_ASCII && charArray[i] <= MAX_ASCII)
                {
                    if (asciiVal > (MAX_ASCII - 47))
                    {
                        asciiVal -= 47;
                    }
                    else
                    {
                        asciiVal += 47;
                    }
                }

                charArray[i] = (char)asciiVal;
            }

            return new string(charArray);
        }
    }
}
