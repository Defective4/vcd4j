<img width="128" height="128" src="logo.png" />  

# VCD4j
A VCD (Value Change Dump) files parser, player, recorder and writer library for Java

# About
The Value Change Dump (VCD) file format is a widely used data format for storing digital logic traces. It's used to analyze signal states over time using waveform viewing tools. It's common application is in hardware simulation and debugging.

**VCD4j** is a Java library that aims to be an all-in-one tool for interacting with VCD files.  
Its key features include the ability to **parse**, **write**, **replay**, and **record** VCD files in real time.  
The library is designed to assist with reading, manipulating, and even creating your own Value Change Dump files, as well as to simulate and capture digital signals in real time for further processing.  
Additionally, VCD4j is designed to be compatible with libraries such as [pi4j](http://www.pi4j.com/), making it a perfect library for working with digital signals on devices such as the Raspberry Pi

# Installation
*Waiting for Maven Central*

# Examples
Some basic examples on how to use VCD4j can be found in [examples](src/examples) directory.  
To build and run the examples use `mvn package -DbuildExamples=true`.  
You will be able to find the built *executable* jar in the `target/` directory.  
To run it, use `java -jar vcd4j-x.x-examples.jar <argument>`, where `<argument>` is the name of example to run.  
Possible example names are listed after running the jar with no arguments.

# Roadmap
- [ ] Support for multiple scopes
- [ ] Better VCD simulation accuracy
- [ ] Better handling of malformed VCD files

# Contributing
All contributions are welcome!  
Before opening a pull request, please [open an issue](https://github.com/Defective4/vcd4j/issues) first.  
Please make sure that no similar issues/pull requests are open.  

## Reporting bugs and feature requests
For bugs and features requests please [open an issue](https://github.com/Defective4/vcd4j/issues/new)