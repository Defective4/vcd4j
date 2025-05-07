# VCD4J
A VCD (Value Change Dump) files parser, player, recorder and writer library for Java

# About
> The VCD file format structure is a common data format that can be used to store digital logic traces, so that you can then later view it with a waveform viewing tool.  
[[Source](https://zipcpu.com/blog/2017/07/31/vcd.html)]  

**VCD4J** is a Java library that allows you to not only read and write your own VCD files, but also record and replay them in real time!
Here are some of its key features:
- **Decode**  
  VCD parser supports both four-state and multi-bit values.
- **Write**  
  You can prepare your own Value Change Dumps and write them to a `.vcd` file  
- **Play**  
  Built-in VCD player lets you replay VCD files in real time with *up to* 1ns precision.  
  The player can easily be integrated with other libraries such as [pi4j](http://www.pi4j.com/)
- **Record**  
  Digital signals can be recorderd from practically *any* source, including hardware devices.  
  Just like the player, VCD recorder can be integrated with pi4j.