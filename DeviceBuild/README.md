# Physical Build Instructions

The most difficult part of the phsyical assembly is the power on/off circut which is applied to the prototype board seen below.  In my setup the small black button is used for powering the device.

To fit in the 3d printable encloser included in this repo it is important to place the buttons in the exact positions seen on the images below.

__Top of proto board__

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/proto_top.jpg" width=50% height=50%>    

__Bottom of proto board__

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/proto_underside.jpg"  width=50% height=50%>

__proto board connected to pi zero__

_note that the yellow, red, and white wires are soldered to the corresponding gpio pins of the pi, seen in second image. they end up ordered: yellow, white, red on the pi_

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/proto_underside_pi.jpg"  width=50% height=50%>
<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/pi_3_wire_gpio.jpg" width=50% height=50%>

For the power cycle circut reference the bread board image below. Note that the wires coming off our proto board will attach to the powerboot and are color coded (light blue is battery, light green is env, black is ground and white will be low battery)

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/lipopi_schematic_powerboost.png">

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/powerbutton_powerboost_1000C.png" width=50% height=50%>

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/powerboost_1.jpg" width=50% height=50%>

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/powerboost_proto.jpg" width=50% height=50%>

Once that proto board is all wired and soldered the rest is quite simple. And can be explained with the pics below.

We attach the pi zero to the zero4u usb hat like and then run the power from the powerboost to the jlc connector on the zero4u like so:

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/zer4u_powerboost_connection.jpg" width=50% height=50%>

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/assembled_1.jpg" width=50% height=50%>

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/assembled_2.jpg" width=50% height=50%>

The lipo batter will plug directly to the power boost and the power out goes tothe zero4u hub which will supply power to the board.. 

Thats really it.  mostly plug n play!  just download the latest device release image and write it to a micro sd card and pop it in.  Also make sure to get the Android App for controlling the device.  Both links provided below. You can then just plug in your wifi adapter and usb flash drive and power her up by holding the power button for about 1 second and the connect your bluetooth enabled android app to the device.

[<img src="https://img.shields.io/badge/Device%20Image%20Latest%20Release-v.3.0.0-green.svg">](https://github.com/eliddell1/Project-Blue-Fist/releases)

[<img src="https://img.shields.io/badge/Android-PlayStore-green.svg">](https://play.google.com/store/apps/details?id=liddell.onus.com.fistbump_ble)

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=MLBAU3J6JVDS8&source=url)


