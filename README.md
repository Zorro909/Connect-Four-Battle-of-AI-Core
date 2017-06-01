# Connect Four - AI vs AI

## Game Rules

Each player turn consists of dropping a "disc" into one slot and gaining 4 in a row. [More https://en.wikipedia.org/wiki/Connect_Four]

The game "field" is 8*8.

## Download

### Download Compiled

Click on the Release Tab [https://github.com/Zorro909/Connect-Four-Battle-of-AI-Core/releases] and select the newest Version.
Extract all Files into one Folder and you're done.

### Download via Git (Discouraged if you only want to develop an AI)

```
% git clone https://github.com/Zorro909/Connect-Four-Battle-of-AI-Core
```

## Usage

```
java -jar VierGewinnt.jar Path/To/First/AI.py Path/To/Second/AI.js
```

## AI Tutorial

### General

This Battle of AI Core supports three programming languages.
Java, JavaScript and Python.

The Symbols are X,O and u.
X is Player 1, O is Player 2 and u is unplaced, ie. there is nothing.

### Java

https://github.com/Zorro909/Connect-Four-Battle-of-AI-Core/blob/v0.1/4%20Gewinnt/ai_templates/JavaTemplateAI.java

You need to implement the Interface de.Zorro.VierGewinnt.API.AI

Board has the Functions:  
	getCell(x,y): Returns the Value of the Cell (X,O,u)  
	getWidth(): Returns the Width of the Board  
	getHeight(): Returns the Height of the Board  
	copyBoard(): Returns a copied board where you can set your discs yourself  
	setNew(player,x): (Only available for copied Boards) You can set a stone yourself. Player is either 0 or 1.  
	                   Must be the opposite who just played.  
			   Returns e for Error, g for good, X for Player 1 won, O for Player 2 won, l for a Tie  
			   (Nobody won and Board is full)
		
#### Building

You have three Options of Building your Java class.

##### Source Files

If you want to develop like in Python or JavaScript, you can let this Program compile your File. If you use any Library, you must put it in a libs folder, where the VierGewinnt.jar is.

##### Class Files

You pretty much just need to place the Class somewhere and execute the Program.

##### Jar Files

The Jar File Name must be schemed after this: MainClass.jar (The Class currently can't be in a Package)

### JavaScript

https://github.com/Zorro909/Connect-Four-Battle-of-AI-Core/blob/v0.1/4%20Gewinnt/ai_templates/JSTemplateAI.js

You're able to use the same Options like Java.  
Additionally you're able to use Java Classes like java.util.Random
```
var random = Java.type("java.util.Random");
```

You need to implement a function named turn with the arguments board and symbol that returns the x coordinate of your dropped disc.
```
function turn(board,symbol){
return x;
}
```

### Python

https://github.com/Zorro909/Connect-Four-Battle-of-AI-Core/blob/v0.1/4%20Gewinnt/ai_templates/pythonTemplateAI.py

You need to implement a function named turn with the arguments board and symbol that returns the x coordinate of your dropped disc.

```
def turn(board,symbol)

    return x
```

The Board variable has the following functions:  
	getCell(x,y): Returns the Value of the Cell (X,O,u)  
	getWidth(): Returns the Width of the Board  
	getHeight(): Returns the Height of the Board  
