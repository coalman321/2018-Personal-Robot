#####################################
# testPlot.plt
# Created By Cole Tucker8/21/2018
#####################################

# Data Order:
#   FORWARD###############
#   1 Time
#   2 Linear Velocity (m/s)
#   3 Acceleration (m/s^2)
#   4 Angular Velocity (rad/s)
#   5 X Position (in)
#   6 Y Position(in)
#   7 Rotation (deg)
#   8 Curvature
#   9 DCurvature_DS
#   REVERSE##############
#  10 Velocity (in/s)
#  11 Acceleration (in/s^2)
#  12 Linear Velocity (m/s)
#  13 Acceleration (m/s^2)
#  14 Angular Velocity (rad/s)
#  15 X Position (in)
#  16 Y Position(in)
#  17 Rotation (deg)
#  18 Curvature
#  19 DCurvature_DS
#  20 Time
#  21 Velocity (in/s)
#  22 Acceleration (in/s^2)

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
	 datafile using 1:($3*100) axes x1y1 with points title "Acceleration (m/s^2)" lw 3 lc rgb "brown", \
	 datafile using 1:($4*100) axes x1y1 with points title "Angular Velocity (rad/s)" lw 3 lc rgb "red", \
	 datafile using 1:($5*1) axes x1y1 with points title "X Position (in)" ps 1.0 lc rgb "blue", \
     datafile using 1:($6*1) axes x1y1 with points title "Y Position(in)" ps 1.0 lc rgb "purple", \
	 datafile using 1:($7*1) axes x1y1 with points title "Rotation (deg)" lw 3 lc rgb "green"
