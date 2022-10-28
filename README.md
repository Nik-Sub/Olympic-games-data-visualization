# Olympic-games-data-visualization

## Introduction 
Application that visualize data from Olympic Games. Data manipulation is written in C++ and visuals in Java. Two programming languages were combined to create this application, C++ and Java. Communication between them is realized by data serialization in Java and data deserialization in C++ using JNI (Java Native Interface).

## Instruction
First connect .dll file with Java project (in properties find where you can pass path to your Native library), for example:
  1) Open properties of file in Eclipse
  2) Go to Java Build Path
  3) In JRE System Library find Native library location and pass destination of .dll file.
  
When program is started, window will appear with 3 text boxes (file for events, for athletes and for special year).

Format of events.txt file:

  #year season!City!Sport!Discipline!Team or inidividual!Country!if this is team then id of adthletes will apear between [ , ]!Medal
  
  2016 Summer!Rio de Janeiro!Basketball!Basketball Women's Basketball!Team!Serbia!['16977', '19205']!Bronze
  2016 Summer!Rio de Janeiro!Boxing!Boxing Men's Flyweight!Individual!Cuba!125690!
  2016 Summer!Rio de Janeiro!Boxing!Boxing Men's Flyweight!Individual!Uzbekistan!135205!Gold
  
Format of athletes.txt file:

  #id!Name!Gender!Years!Height!Weight
  
  13029!Usain St. Leo Bolt!M!17!196!95

Type of graph can be changed in options: 

PieChart is showing all countries participating in football in 2016.

![image](https://user-images.githubusercontent.com/114858949/198725485-7c6e5fbd-abca-4347-8bf1-9d6bba1eecf8.png)


XY graph showing average height per year

![image](https://user-images.githubusercontent.com/114858949/198728399-90eb3d3c-4da8-4aea-a798-02f53c739726.png)



  
  
  
