var random = Java.type("java.util.Random");
function turn(board, symbol){
	var x = random.nextInt(board.getWidth());
	while(board.getCell(x,board.getHeight()-1)!="u"){
		x = random.nextInt(board.getWidth());
	}
	return x;
}