A Simple Captcha using Java Servlet.
===================

Tools
-------------------
* Programming Language - Java
* IDE - Netbeans 7.2
* Build Tool - Maven

How It Works
--------------------
Keep CaptchaResult class instance on httpsession everytime it generates new Captcha. To compare your answer with Captcha's generated string, just use CaptchaResult's getAnswer() method.