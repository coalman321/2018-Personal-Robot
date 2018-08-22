#####################################
# plotrobot.plt
# Created By Eric Francis 3/11/2018
#####################################

# Data Order:
#   1 FGPA Time
#   2 Auto State
#   3 Left Wheel Encoder
#   4 Right Wheel Encoder
#   5 Gyro Angle
#   6 Gyro Target
#   7 Lift Encoder
#   8 Lift Encoder Target
#   9 In Auto
#  10 Left Motor Voltage
#  11 Right Motor Voltage
#  12 Left Talon Voltage
#  13 Right Talon Voltage
#  14 Right wheel Velocity
#  15 Left Wheel Velocity

robotfile = "robotdata.tsv"

set datafile separator "\t"
set grid
set title 'Team 4145 Robot Data'
set xlabel "FPGA Time"
set ylabel "Wheel Encoder"
set y2label "Gyro Angle"

set ytics nomirror
set y2tics
set tics out
set mxtics 5
set mytics 5
set autoscale  y
set autoscale y2

plot robotfile using 1:($9*1000) axes x1y1 with line title "In Auto" lw 3 lc rgb "red", \
	 robotfile using 1:($2*1000) axes x1y1 with line title "Auto State" lw 3 lc rgb "brown", \
	 robotfile using 1:($3*1) axes x1y1 with points title "Left Wheel Encoder" ps 1.0 lc rgb "blue", \
     	 robotfile using 1:($4*1) axes x1y1 with points title "Right Wheel Encoder" ps 1.0 lc rgb "purple", \
	 robotfile using 1:($13*100) axes x1y1 with points title "Right Wheel Velocity" lw 3 lc rgb "green", \
	 robotfile using 1:($14*100) axes x1y1 with points title "Left Wheel Velocity" lw 3 lc rgb "red"

