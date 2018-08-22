#####################################
# plotrobot.plt
# Created By Eric Francis 3/19/2018
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
#  12 Talon Voltage
#  13 setpoint 1 (left)
#  14 feed forward 1 (left)
#  15 feed back 1 (left)
#  16 velocity 1 (left)
#  17 setpoint 2 (right)
#  18 feed forward 2 (right)
#  19 feed back 2 (right)
#  20 velocity 2 (right)

robotfile = "robotdata.txt"

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
	 robotfile using 1:($3*1) axes x1y1 with points title "Left Wheel Encoder" ps 2.0 lc rgb "black", \
	 robotfile using 1:($4*1) axes x1y1 with points title "Right Wheel Encoder" ps 2.0 lc rgb "blue", \
	 robotfile using 1:5 axes x1y2 with points title "Gyro Angle" ps 1.0 lc rgb "green", \
	 robotfile using 1:6 axes x1y2 with points title "Gyro Target" ps 1.0 lc rgb "grey", \
	 robotfile using 1:($11*1000) axes x1y1 with points title "Right Motor Voltage" lw 3 lc rgb "brown", \
	 robotfile using 1:($13*1) axes x1y1 with points title "Setpoint1" ps 2.0 lc rgb "black", \
	 robotfile using 1:($17*1) axes x1y1 with points title "Setpoint2" ps 2.0 lc rgb "blue"

#	 robotfile using 1:($18*1) axes x1y1 with points title "Feedforward2" lw 3 lc rgb "brown", \
#	 robotfile using 1:($19*1000) axes x1y1 with points title "Feedback2" lw 3 lc rgb "blue", \
#	 robotfile using 1:($20*1000) axes x1y1 with points title "Velocity2" lw 3 lc rgb "purple"
#	 robotfile using 1:($17*1) axes x1y1 with points title "Setpoint2" lw 3 lc rgb "red", \
#	 robotfile using 1:($11*1000) axes x1y1 with points title "Right Motor Voltage" lw 3 lc rgb "black", \
#    robotfile using 1:4 axes x1y1 with points title "Right Wheel Encoder" ps 1.0 lc rgb "purple"
#	 robotfile using 1:($18*1) axes x1y1 with points title "Feedforward2" lw 3 lc rgb "brown", \
#	 robotfile using 1:($19*1) axes x1y1 with points title "Feedback2" lw 3 lc rgb "blue", \
#	 robotfile using 1:($20*1) axes x1y1 with points title "Velocity2" lw 3 lc rgb "purple"
