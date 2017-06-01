import random

def turn(board,symbol):
    x = random.choice(range(board.getWidth()))
    while board.getCell(x,board.getHeight()-1) != "u":
        x = random.choice(range(board.getWidth()))
    return x
