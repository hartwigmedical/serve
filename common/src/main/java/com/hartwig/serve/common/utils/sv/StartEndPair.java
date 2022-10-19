package com.hartwig.serve.common.utils.sv;

import java.util.Objects;

public class StartEndPair<A>
{
    public final A Start;
    public final A End;

    public StartEndPair(A start, A end)
    {
        Start = start;
        End = end;
    }

    public A get(int seIndex) { return seIndex == StartEndIterator.SE_START ? Start : End; }
    public A get(boolean isStart) { return isStart ? Start : End; }
    public A start() { return Start; }
    public A end() { return End; }

    public String toString()
    {
        return "Pair[" + Start + "," + End + "]";
    }

    public boolean equals(Object other)
    {
        return other instanceof StartEndPair<?>
                && Objects.equals(Start, ((StartEndPair)other).Start)
                && Objects.equals(End, ((StartEndPair)other).End);
    }

    public static <A,B> StartEndPair<A> of(A a, A b)
    {
        return new StartEndPair<>(a,b);
    }
}
