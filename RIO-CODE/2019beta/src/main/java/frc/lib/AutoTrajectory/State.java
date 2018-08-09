package frc.lib.AutoTrajectory;

import frc.lib.util.*;

public interface State<S> extends Interpolable<S>, CSVWritable {
    double distance(final S other);

    boolean equals(final Object other);

    String toString();
}
