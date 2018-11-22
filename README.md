# Project-Blue-Fist

Project Blue Fist is the next iteration of my P.O.C. project, [FistBump](https://github.com/eliddell1/FistBump), a handheld pentesting tool that can be used to grab WPA handshakes and PMKID hashes on Red Team engagements. It was designed to allow one to get close to a given target allowing the attack to be more effective, without raising suspision.  The original proof of concept was a stand alone device that launched a very effective attack via the push of a button, but was not exactly stealthy with its' use of blinking lights to indicate various stages of the attack as well as the outcome. It was also a bit bulky and difficult to conceal. 

Project Blue-Fist aims to remedy these short comings by removing the array of led lights as well as the trigger button, leaving only a single button to power on or off the device.  This greatly lowers its' physical footprint allowing for the device to gracefuly fit in your pocket.  This iteration also now makes use of bluetooth and an android app so that the device can be completely controlled from your android phone. It still saves the hashes to removable storage, but now allows for on the fly targeting and detailed results which include new naming conventions for targeted vs broad attack results, and catalog file generation on broad attacks that details what essids can be found in the hashfile of the same file name.

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/white_device.jpg" width="300" height="400">   <img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/white_phone_device.jpg" width="300" height="400">   

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/Screenshot_20181109-181002.png" width="200" height="398">     <img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/Screenshot_20181109-181027.png" width="200" height="398">      

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/device_fistbumpBLE.jpg" width="400" height="300">

<img src="https://github.com/eliddell1/Project-Blue-Fist/blob/master/Images/device%2Bapp-targeted_attack_mode.jpg" width="400" height="300">





