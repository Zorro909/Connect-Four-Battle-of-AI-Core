import random

def turn(board,symbol):
    x = random.randrange(8)
    while (board.getCell(x,board.getHeight()-1) != "u"):
        x = random.randrange(8)
    return x
