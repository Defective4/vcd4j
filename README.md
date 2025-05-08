<img width="128" height="128" src="logo.png" />  

# VCD4j
A VCD (Value Change Dump) files parser, player, recorder and writer library for Java

# About
> The VCD file format structure is a common data format that can be used to store digital logic traces, so that you can then later view it with a waveform viewing tool.  
[[Source](https://zipcpu.com/blog/2017/07/31/vcd.html)]  

**VCD4j** is a Java library that allows you to not only read and write your own VCD files, but also record and replay them in real time!  
Here are its key components:
- **Parser**  
  VCD parser supports both four-state and multi-bit values.
- **Writer**  
  You can prepare your own Value Change Dumps and write them to a `.vcd` file  
- **Player**  
  Built-in VCD player lets you replay VCD files in real time with *up to* 1ns precision.  
  The player can easily be integrated with other libraries such as [pi4j](http://www.pi4j.com/)
- **Recorder**  
  Digital signals can be recorderd from practically *any* source, including hardware devices.  
  Just like the player, VCD recorder can be integrated with pi4j.  

# Examples
Some basic examples on how to use VCD4j can be found in [examples](src/examples) directory.  
To build and run the examples use `mvn package -DbuildExamples=true`.  
You will be able to find the built *executable* jar in the `target/` directory.  
To run it, use `java -jar vcd4j-x.x-examples.jar <argument>`, where `<argument>` is the name of example to run.  
Possible example names are listed after running the jar with no arguments.