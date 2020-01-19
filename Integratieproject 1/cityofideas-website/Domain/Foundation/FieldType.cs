using System;

namespace COI.BL.Domain.Foundation
{
    public enum FieldType : byte
    {
        OpenText = 0,
        Image,
        Video,
        SingleChoice,
        MultipleChoice,
        Location,
        DropDown,
        Statement
    }
}
