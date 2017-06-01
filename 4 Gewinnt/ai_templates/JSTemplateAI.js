var random = Java.type("java.util.Random");
function turn(board, symbol){
	var x = Math.random() * 8|0;
	while(board.getCell(x,board.getHeight()-1)!="u"){
		x = Math.random() * 8|0;
	}
	return x;
}