# When found in the nature, AIs are peaceful pets,
# however some people (often called hackers) try to turn them into evil animals,
# so be aware of not letting bad AIs in.
# This module filters bad AIs, by forcing them to play in a sandbox,
# in fact, only tame ones will play in this sandbox,
# so the bad guys will be detected!

import imp
from sys import stdin
import timeout
global board
global symbol

class Board():
    def __init__(self,board):
        self.board = board
    def getCell(x,y):
        return self.board[x][y]
    def getWidth():
        return len(self.board)
    def getHeight():
        return len(self.board[0])

def load_ai(filename):
    # the sandbox is currently as clever as ... well ... it's not clever
    code = open(filename).read() # quick and dirty, yet production ready code ;)
    module = imp.new_module('ai')
    mdict = module.__dict__

    # Pop the obvious exploits
    mdict.pop('exec',0)
    mdict.pop('eval',0)

    # Lexical analyzer
    # TODO: Fix the stupid
    forbidden = ['import subprocess', 'import sys', 'import os']
    for word in forbidden:
        if word in code:
            raise Exception('Found security issue in {} while looking for {}'.format(filename, word))

    exec(code, mdict) # 'exec' is the root of all eval, here we gonna sandbox
    return module

file = stdin.readline().rstrip()

module = load_ai(file)
w, h = int(stdin.readline()), int(stdin.readline());

turn = int(stdin.readline())
while turn > 0:
    b = [[0 for x in range(w)] for y in range(h)]
    x, y = 0,0
    while x < w:
        while y < h:
            b[x][y] = stdin.readline()
            y = y + 1
        x = x + 1
    board = Board(b)
    symbol = stdin.readline()
    pos_x = timeout.timelimit(3, eval("module." + "turn"), args=(board, symbol))
    print("pos_x_result_handler=" + str(pos_x))
    turn = int(stdin.readline())