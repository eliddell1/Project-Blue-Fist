# author Eliddell1
# script for ble communication between fistbump device and android app
# Nov-2018 copywrite

from bluetooth import *
import os
import fnmatch

bashStartBTCommand = "echo 'power on\ndiscoverable on\npairable on\nagent NoInputNoOutput\ndefault-agent\nquit' | bluetoothctl"

os.system(bashStartBTCommand)

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "00001101-0000-1000-8000-00805F9B34FB"

advertise_service( server_sock, "FistBump",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ], 
#                   protocols = [ OBEX_UUID ] 
                    )


def getHashes():
	totalPMKID = 0
	totalHandshakes = 0
	totalTargetedHandshakes = 0
	totalTargetedPMKID = 0
	if os.path.isdir('/media/usb0/PMKID'):
		totalPMKID = len(fnmatch.filter(os.listdir("/media/usb0/PMKID"),'*.16800'))
		totalTargetedPMKID = len(fnmatch.filter(os.listdir("/media/usb0/PMKID"),'targeted*'))
	if os.path.isdir('/media/usb0/Handshakes'):
		totalHandshakes = len(fnmatch.filter(os.listdir("/media/usb0/Handshakes"),'*.2500'))
                totalTargetedHandshakes = len(fnmatch.filter(os.listdir("/media/usb0/Handshakes"),'targeted*'))

	return "HASHES,"+str(totalTargetedPMKID)+":"+str(totalPMKID)+","+str(totalTargetedHandshakes)+":"+str(totalHandshakes)+","

def listen():
	print "Waiting for connection on RFCOMM channel %d" % port
	print getHashes()
	client_sock, client_info = server_sock.accept()
	print "Accepted connection from ", client_info

	try:
		while True:
		#try:
			print "listening"
			data = client_sock.recv(1024)
			if len(data) == 0: break
			print "received [%s]" % data
			if data.startswith('TARGET:'):
				if os.path.exists('/dev/sda'):
					type,bssid,essid = data.split(":")
					print "targeting - essid:"+essid+" bssid:"+ bssid
                                        setTargetCMD = "sudo echo "+bssid+" > /media/usb0/targets.txt"
					os.system(setTargetCMD)
					launchTargetedAttackCMD = "sudo bash /home/pi/FistBump/wpa_hashgrab.sh "+essid
					os.system(launchTargetedAttackCMD)
					os.system("sudo rm /media/usb0/targets.txt")
					data = 'ATTACK_COMPLETE'
				else:
					data = 'ERROR_NO_DRIVE'
			elif data == 'attack':
				if os.path.exists('/dev/sda'):
					os.system("sudo bash /home/pi/FistBump/wpa_hashgrab.sh")
					data = 'ATTACK_COMPLETE'
				else:
					data = 'ERROR_NO_DRIVE'
			elif data == 'refresh':
				data = getHashes()
			else:
				data = 'no comprende!'
			client_sock.send(data)
			print "sent[%s]" % data

	except IOError as e:
		print (e.errno)
		print (e)
		pass

	print "disconnected"
	listen()


listen()
