# Universal Controller Client for Java

Universal Controller is a SDK that allows developers to use their customised 
controller on mobile devices or a tailor made hardware controller with Unity. 
This Java based client side library can be implemented on most of the hardwares 
that support Java, or more specific, single board computers like Arduino boards 
& Raspberry Pi. If you want to develop your own hardware controller with a 
single board computer, this is the library you are looking for.

The server side SDK can be found 
[here](https://github.com/iamWing/UniversalController_Server).

Or if you are looking for the client side library for Android, it's
[here](https://github.com/iamWing/uc-client-android).

---

## Getting Start

To use the this library, I recommend to start with initialising a `UCClient` 
instance by calling the static method 
`UCClient.init(String, int, int, IUCCallback)`. By calling this method, the 
connection between the client and server will be made. And all the server 
responses will be handled by the methods implements from IUCCallback.

Once the connection has been made, the other methods are quite straight 
forward. Please look up the comments within the code for the usage of those 
public methods.

---

## Version history

__v1.0.0__

- Implemented all basic functions needed to communicate with Universal 
Controller server.

---

## License & copyright

Copyright (c) 2018 Wing Chau & AlphaOwl.co.uk
<br />
All rights reserved.

This software may be modified and distributed under the terms
of the MIT license. See the LICENSE file for details.