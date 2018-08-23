#####################################
# testPlot.plt
# Created By Cole Tucker8/21/2018
#####################################

# Data Order:
#   1 Time
#   2 Linear Velocity (m/s)
#   3 Acceleration (m/s^2)
#   4 Angular Velocity (rad/s)
#   5 X Position (in)
#   6 Y Position(in)
#   7 Rotation (deg)
#   8 Curvature
#   9 DCurvature_DS
#  10 Time
#  11 Velocity (in/s)
#  12 Acceleration (in/s^2)
#  13 Kinematic pose X (in)
#  14 Kinematic pose Y (in)
#  15 Kinematic pose Rot (in)

datafile = "test.txt"

set datafile separator ","
set grid
set title 'Test results'
set xlabel "Time"
set ylabel ""
set y2label ""

set ytics nomirror
set y2tics
set tics out
set mxtics 5
set mytics 5
set autoscale  y
set autoscale y2

plot datafile using 1:($2*10) axes x1y1 with points title "Linear Velocity (m/s)" lw 3 lc rgb "red", \
	 datafile using 1:($3*100) axes x1y1 with points title "Acceleration (m/s^2)" lw 3 lc rgb "green", \
	 datafile using 1:($4*100) axes x1y1 with points title "Angular Velocity (rad/s)" lw 3 lc rgb "blue", \
	 datafile using 1:($5*1) axes x1y1 with points title "Trajectory X Pos (in)" lw 3 lc rgb "brown", \
     datafile using 1:($6*1) axes x1y1 with points title "Trajectory Y Pos (in)" lw 3 lc rgb "purple", \
	 datafile using 1:($7*1) axes x1y1 with points title "Trajectory Rotation (deg)" lw 3 lc rgb "gold", \
     datafile using 1:($13*1) axes x1y1 with points title "Kinematic X Pos (in)" lw 3 lc rgb "pink", \
     datafile using 1:($14*1) axes x1y1 with points title "Kinematic Y Pos (in)" lw 3 lc rgb "violet", \
     datafile using 1:($15*1) axes x1y1 with points title "Kinematic Rotation (deg)" lw 3 lc rgb "cyan"
