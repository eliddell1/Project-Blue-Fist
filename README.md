# Project-Blue-Fist (FistBump BLE Edition) 
[<img src="https://img.shields.io/badge/Device%20Image%20Latest%20Release-v.3.0.0-green.svg">](https://github.com/eliddell1/Project-Blue-Fist/releases)

[<img src="https://img.shields.io/badge/Android-PlayStore-green.svg">](https://play.google.com/store/apps/details?id=liddell.onus.com.fistbump_ble)

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=MLBAU3J6JVDS8&source=url)

Project Blue Fist is the next iteration of my P.O.C. project, [FistBump](https://github.com/eliddell1/FistBump), a handheld pentesting tool that can be used to grab WPA handshakes and PMKID hashes on Red Team engagements. It was designed to allow one to get close to a given target allowing the attack to be more effective, without raising suspision.  The original proof of concept was a stand alone device that launched a very effective attack via the push of a button, but was not exactly stealthy with its' use of blinking lights to indicate various stages of the attack as well as the outcome. It was also a bit bulky and difficult to conceal. 

Project Blue-Fist aims to remedy these short comings by removing the array of led lights as well as the trigger button, leaving only a single button to power on or off the device.  This greatly lowers its' physical footprint allowing for the device to gracefuly fit in your pocket.  This iteration also now makes use of bluetooth and an android app so that the device can be completely controlled from your android phone. It still saves the hashes to removable storage, but now allows for on the fly targeting and detailed results which include new naming conventions for targeted vs broad attack results, and catalog file generation on broad attacks that details what essids can be found in the hashfile of the same file name.

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/white_device.jpg" width="300" height="400">      <img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/white_phone_device.jpg" width="300" height="400"> 

## Power on/off Device

To power on the device, hold down the power button.  A red and blue light will turn on, hold the power button until the red light turns off indicating the boot process has begun.  The boot process takes about 30 seconds.

Once the device is on, open the FistBump app on your bluetooth enabled android device. The app will attempt to conect to the FistBump Device

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/connecting_screenshot.png" width="200" height="367">     <img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/Screenshot_retry.png" width="200" height="367">

If it fails, simply press retry.  Sometimes it maybe required to quit the app and try again, should it be unable to connect after the second or thrid attempt.

## Launching an Attack

Once you have connected to the FistBump Device, the app will display a list of wifi networks around you, available to attack.

At this point you can conduct a "Broad Attack" which will target anything in range, by pressing the red attack button or a Targeted Attack simply by selecting one of the displayed networks before tapping the attack button.

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/Screenshot_20181109-181027.png" width="200" height="367">            <img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/Screenshot_20181109-181002.png" width="200" height="367">            <img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/Screenshot_attacking.png" width="200" height="367">

## Collecting Booty

When attacks are successful, the booty/loot is stored to your removable usb storage.  Booty is currently organized into two folders, PMKID and Handshakes. 

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/booty_main_dir.png" width=30% height=30%>

Note that actual hash files have an extension of .2500 or .16800. These correspond to the hashing mode you would use in hashcat to bruteforce those hashes.  2500 being standard WPA handshakes and 16800 being PMKID hashes. i.e. <code>$ hashcat -m 2500 ... </code>  or <code>$ hashcat -m 16800 ... </code>

When you drill into the appropriate directory, you will find broad attack results named with a date/time stamp while targeted attacks will be named with the convention "targeted-[ESSID NAME]"  

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/handshake_dir.png" width=50% height=50%>

Above is the Handshake Directory.  You will notice that each hash file has a corresponding .catalog file.  Because an individual hash file may contain more than one hash, and in the case of broad attacks, even more than one target, this catalog file is there to list the targets found in it's corresponding hash file.

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/broad_booty.png" width=50% height=50%>

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/catalog.png" width=50% height=50%>

## Disclaimer

_This Device was developped as a proof of concept and for White Hat Purposes.  You should only use this device on your own or a consenting network and in a controlled enviroment, as sending the necessary deauth packets used in the contained scripts could be illegal in your given part of the world. I do not endorse or warrent breaking the law or invading the privacy of others. You alone are fully responsible for what you do with this info/device, and how you use it. I am not responsible for your actions. Please do not hack Wifi points that you are not allowed to!!!
Don't be a jerk!_

## What is here?
This repository contains all the Schematics, Reference Photos, Boot images, scripts, Android app source code and even 3d printable encloser parts for creating a FistBump prototype device.

## Parts List

* 1 [pi zero v1.3](https://www.raspberrypi.org/products/raspberry-pi-zero/)
_Do **NOT** use the wifi model as the chip doesn't support monitor/package injection and creates interferience on usb hub_

* 1 [zero4u usb hub adapter](https://www.adafruit.com/product/3298?gclid=Cj0KCQjw6rXeBRD3ARIsAD9ni9CGzOos99HaKls0MxgqrZMt_sKTnR6LVGsSJiN6rdDrbmr9ndM0L3QaAk_SEALw_wcB)

* 1 [Adafruit Perma Proto Bonnet mini](https://www.amazon.com/Adafruit-Perma-Proto-Bonnet-ADA3203/dp/B07115Z42P/ref=sr_1_4?s=electronics&ie=UTF8&qid=1542996935&sr=1-4&keywords=pi+zero+proto+hat)

* 2 [10k resistors](https://www.amazon.com/Projects-25EP51410K0-10K-Resistors-Pack/dp/B01F06T56I/ref=sr_1_1_sspa?ie=UTF8&qid=1540222052&sr=8-1-spons&keywords=10k+resistor&psc=1)

* 1 [100k resistor](https://www.amazon.com/Projects-25EP514100K-100k-Resistors-Pack/dp/B0185FCGEY/ref=sr_1_1_sspa?ie=UTF8&qid=1540222085&sr=8-1-spons&keywords=100k+resistor&psc=1)

* 3 [1N4007 diodes]( https://www.amazon.com/100-Pieces-1N4007-Rectifier-Electronic/dp/B079KBFKK5/ref=sr_1_1_sspa?ie=UTF8&qid=1540222123&sr=8-1-spons&keywords=1n4007+diode&psc=1)

* 1 [small momentary button for power button](https://www.amazon.com/GZFY-6x6x4-5mm-Momentary-Tactile-Button/dp/B01N6GU7TA/ref=sr_1_14?ie=UTF8&qid=1540222185&sr=8-14&keywords=small+momentary+button)

* 1 [2.5 pi nylon screw set](https://www.adafruit.com/product/3299)

* 1 [usb wifi adapter with a small profile and capable of monitor mode]( https://www.amazon.com/gp/product/B019XUDHFC/ref=oh_aui_detailpage_o00_s00?ie=UTF8&psc=1) 

* 1 [usb bluetooth 4.0 adapter with a small profile](https://www.amazon.com/gp/product/B009ZIILLI/ref=oh_aui_detailpage_o08_s00?ie=UTF8&psc=1) 

* 1 [lo profile sandisk flash storage for saving handshakes in a removable manner](https://www.amazon.com/SanDisk-Cruzer-Low-Profile-Drive-SDCZ33-008G-B35/dp/B005FYNSUA/ref=sr_1_7?s=electronics&ie=UTF8&qid=1540222662&sr=1-7&keywords=sandisk+flash+drive+8gb)

* 1 [16-32gb class 10 mirco sd card for pi boot image](https://www.amazon.com/s/ref=nb_sb_ss_i_5_10?url=search-alias%3Delectronics&field-keywords=16gb+micro+sd+card+class+10&sprefix=16gb+micro%2Celectronics%2C132&crid=5DO4BAWIZ2SP)

* 1 [Pwerboost 1000c](https://www.adafruit.com/product/2465)

* 1 [3.7V 1200mAh PKCELL LP503562](https://www.amazon.com/s?k=3.7V+1200mAh+PKCELL+LP503562&ref=nb_sb_noss) _size matters we want a low profile as we will have a tight fit but feel free to alter this as you see fit espcially if you deisgn your own enclosuer etc_

## Physical Assembly
For instructions on the physical assmebly follow the README file, [here](https://github.com/eliddell1/Project-Blue-Fist/tree/master/DeviceBuild).  

I have also supplied freecad/stl files for the 3d printable encloser [here](https://github.com/eliddell1/Project-Blue-Fist/tree/master/Enclosure).

## Donate 

If you enjoyed this project, help me make more by buying me a coffee or something.

### PayPal
[<img src="https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif">](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=MLBAU3J6JVDS8&source=url)

### Bitcoin
1KuntExCV54WJaVxyBMDbAXMye6zWcZfR

## Purchase Inquiries

If you are one of those who would rather have one built for them, send inquiries to liddell.erik@gmail.com subject:FistBumpBLE


