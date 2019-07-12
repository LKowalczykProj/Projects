import sys
from PyQt5.QtCore import Qt, pyqtSignal, QThread, QRect
from PyQt5 import QtWidgets
from PyQt5.QtGui import QFont, QPainter, QBrush, QPen, QColor, QPixmap
from cardDeck import Color, Card, Figure
from time import sleep
import threading
import socket

import signal

signal.signal(signal.SIGINT, signal.SIG_DFL)

separator = "/SEPP/"

def recieve(client,sep):
    data=""
    while not sep in data:
        data+=client.recv(1).decode()
    return data.split(sep)[0]

card_path = "deck_images/"
misc_path = "misc_images/"

class Player():

    def __init__(self,id,name,cash,state,bet):
        self.id=id
        self.name=name
        self.cash=cash
        self.state=state
        self.bet=bet
        self.hand=[]
        self.loser=False


class TableWindow(QtWidgets.QMainWindow):

    def __init__(self, nickname,client):
        super().__init__()
        self.card_width = 60
        self.card_height = 80
        self.client_id=0
        self.table_id=0
        self.width=1000
        self.height=600
        self.posX=100
        self.posY=100
        self.table=[]
        self.nicknames=[]
        self.cash_labels=[]
        self.bet_labels=[]
        self.state_labels=[]
        self.small_blind=0
        self.big_blind=1
        self.gameloop = GameThread(client,nickname)
        self.gameloop.send_meta.connect(self.update_meta)
        self.gameloop.send_table.connect(self.update_table)
        self.gameloop.send_hand.connect(self.update_hand)
        self.gameloop.send_client_info.connect(self.update_client)
        self.gameloop.discard_request.connect(self.discard)
        self.gameloop.game_over.connect(self.game_end)
        self.gameloop.player_move_info.connect(self.update_move)
        self.gameloop.move_request.connect(self.initiate_move)
        self.gameloop.showdown_request.connect(self.showdown)
        self.gameloop.start()


        self.initUI()

    def initUI(self):
        self.setWindowTitle("Poker table #{}".format(self.table_id))
        self.setGeometry(self.posX,self.posY,self.width,self.height)
        self.setObjectName("MAINTABLE")
        self.setStyleSheet("QMainWindow#MAINTABLE {background-color: black}")
        font = QFont()
        font.setBold(True)
        font.setPixelSize(25)
        self.tableLabel = QtWidgets.QLabel("Table #{}".format(self.table_id),self)
        self.tableLabel.setStyleSheet("background-color: white; color: black")
        self.tableLabel.setMargin(4)
        self.tableLabel.setFont(font)
        self.tableLabel.setFixedWidth(self.tableLabel.width()+8)
        self.tableLabel.move(0,0)
        self.statusLabel = QtWidgets.QLabel("Waiting for players...",self)
        self.statusLabel.setStyleSheet("background-color: white; color: black")
        self.statusLabel.setMargin(4)
        self.statusLabel.setFont(font)
        self.statusLabel.setFixedWidth(400)
        self.statusLabel.move(600, 0)
        self.poolLabel = QtWidgets.QLabel("Pool: 0",self)
        self.poolLabel.setFont(font)
        self.poolLabel.setMargin(4)
        self.poolLabel.setFixedWidth(200)
        self.poolLabel.move(330,170)
        self.poolLabel.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
        self.poolLabel.setAlignment(Qt.AlignCenter)
        for position in range(0,4):
            if position == 0:
                label = QtWidgets.QLabel("You")
                label.setGeometry(425,545,150,40)
                label.setFont(font)
                label.setAlignment(Qt.AlignCenter)
                label.setStyleSheet("color: white")
                self.nicknames.append(label)
                label = QtWidgets.QLabel("Cash: 0", self)
                label.setGeometry(575, 420, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.cash_labels.append(label)
                label = QtWidgets.QLabel("Bet: 0", self)
                label.setGeometry(575, 460, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.bet_labels.append(label)
                label = QtWidgets.QLabel("None", self)
                label.setGeometry(575, 500, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.state_labels.append(label)
            elif position == 1:
                label = QtWidgets.QLabel("", self)
                label.setGeometry(40, 215, 150, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignCenter)
                label.setStyleSheet("color: white")
                self.nicknames.append(label)
                label = QtWidgets.QLabel("Cash: 0", self)
                label.setGeometry(50, 350, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.cash_labels.append(label)
                label = QtWidgets.QLabel("Bet: 0", self)
                label.setGeometry(50, 390, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.bet_labels.append(label)
                label = QtWidgets.QLabel("None", self)
                label.setGeometry(50, 430, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.state_labels.append(label)
            elif position == 2:
                label = QtWidgets.QLabel("", self)
                label.setGeometry(425, 15, 150, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignCenter)
                label.setStyleSheet("color: white")
                self.nicknames.append(label)
                label = QtWidgets.QLabel("Cash: 0", self)
                label.setGeometry(575, 60, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.cash_labels.append(label)
                label = QtWidgets.QLabel("Bet: 0", self)
                label.setGeometry(575, 100, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.bet_labels.append(label)
                label = QtWidgets.QLabel("None", self)
                label.setGeometry(575, 140, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.state_labels.append(label)
            else:
                label = QtWidgets.QLabel("", self)
                label.setGeometry(800, 215, 150, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignCenter)
                label.setStyleSheet("color: white")
                self.nicknames.append(label)
                label = QtWidgets.QLabel("Cash: 0", self)
                label.setGeometry(810, 350, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.cash_labels.append(label)
                label = QtWidgets.QLabel("Bet: 0", self)
                label.setGeometry(810, 390, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.bet_labels.append(label)
                label = QtWidgets.QLabel("None", self)
                label.setGeometry(810, 430, 140, 40)
                label.setFont(font)
                label.setAlignment(Qt.AlignLeft)
                label.setMargin(4)
                label.setStyleSheet("background-color: rgba(100,100,100,150); color: white")
                self.state_labels.append(label)
        self.show()

    def paintEvent(self, event):
        painter = QPainter(self)
        painter.setPen(QPen(QColor(110,66,0,255), 5, Qt.SolidLine))
        painter.setBrush(QBrush(QColor(0,150,0,255), Qt.SolidPattern))
        painter.drawEllipse(5, 5, self.width-10, self.height-10)
        painter.setBrush(QBrush(QColor(0, 100, 0, 255), Qt.Dense5Pattern))
        painter.drawEllipse(5, 5, self.width - 10, self.height - 10)
        painter.setPen(QPen(Qt.black, 1, Qt.SolidLine))
        painter.setRenderHint(QPainter.SmoothPixmapTransform)
        # Drawing cards
        for player in self.gameloop.players:
            position = (player.id-self.client_id) % 4
            if position == 0:
                posX=435
                posY=460
            elif position == 1:
                posX=50
                posY=260
            elif position == 2:
                posX=435
                posY=60
            else:
                posX=810
                posY=260

            if not player.loser:
                if len(player.hand) == 0:
                    im = QPixmap(card_path + "card_back.png")
                    painter.drawPixmap(QRect(posX,posY,self.card_width,self.card_height), im)
                    painter.drawPixmap(QRect(posX+70,posY,self.card_width,self.card_height), im)
                else:
                    paths = []
                    for card in player.hand:
                        if card.fig.value > 8:
                            paths.append("{}_{}.png".format(card.fig.name, card.color.name))
                        else:
                            paths.append("{}_{}.png".format(card.fig.value + 2, card.color.name))
                    im = QPixmap(card_path+paths[0])
                    painter.drawPixmap(QRect(posX, posY,self.card_width,self.card_height), im)
                    im = QPixmap(card_path + paths[1])
                    painter.drawPixmap(QRect(posX+70, posY,self.card_width,self.card_height), im)

        paths = []
        for card in self.table:
            if card.fig.value > 8:
                paths.append("{}_{}".format(card.fig.name, card.color.name))
            else:
                paths.append("{}_{}".format(card.fig.value + 2, card.color.name))

        table_y=220
        if len(self.table) == 0:
            im = QPixmap(card_path + "card_back.png")
            painter.drawPixmap(QRect(330, table_y, self.card_width, self.card_height), im)
            painter.drawPixmap(QRect(400, table_y, self.card_width, self.card_height), im)
            painter.drawPixmap(QRect(470, table_y, self.card_width, self.card_height), im)
            painter.drawPixmap(QRect(540, table_y, self.card_width, self.card_height), im)
            painter.drawPixmap(QRect(610, table_y, self.card_width, self.card_height), im)
        elif len(self.table) == 3:
            im = QPixmap(card_path+paths[0]+".png")
            painter.drawPixmap(QRect(330, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[1] + ".png")
            painter.drawPixmap(QRect(400, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[2] + ".png")
            painter.drawPixmap(QRect(470, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + "card_back.png")
            painter.drawPixmap(QRect(540, table_y, self.card_width, self.card_height), im)
            painter.drawPixmap(QRect(610, table_y, self.card_width, self.card_height), im)
        elif len(self.table) == 4:
            im = QPixmap(card_path+paths[0]+".png")
            painter.drawPixmap(QRect(330, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[1] + ".png")
            painter.drawPixmap(QRect(400, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[2] + ".png")
            painter.drawPixmap(QRect(470, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[3] + ".png")
            painter.drawPixmap(QRect(540, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + "card_back.png")
            painter.drawPixmap(QRect(610, table_y, self.card_width, self.card_height), im)
        else:
            im = QPixmap(card_path+paths[0]+".png")
            painter.drawPixmap(QRect(330, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[1] + ".png")
            painter.drawPixmap(QRect(400, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[2] + ".png")
            painter.drawPixmap(QRect(470, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[3] + ".png")
            painter.drawPixmap(QRect(540, table_y, self.card_width, self.card_height), im)
            im = QPixmap(card_path + paths[4] + ".png")
            painter.drawPixmap(QRect(610, table_y, self.card_width, self.card_height), im)

        # Drawing Chips
        for player in self.gameloop.players:
            total_green = 0
            total_blue = 0
            total_red = 0
            total_white = 0
            cash_stack = player.cash
            while cash_stack > 0:
                if cash_stack>2000:
                    cash_batch=2000
                else:
                    cash_batch=cash_stack
                cash_stack-=cash_batch

                if cash_batch > 1000:
                    green = 4
                    cash_batch -= 1000
                else:
                    green = int(cash_batch/250)
                    cash_batch -= green*250
                total_green += green
                if cash_batch > 500:
                    blue = 5
                    cash_batch -= 500
                else:
                    blue = int(cash_batch/100)
                    cash_batch -= blue*100
                total_blue += blue
                if cash_batch > 400:
                    red = 8
                    cash_batch -= 400
                else:
                    red = int(cash_batch/50)
                    cash_batch -= red*50
                total_red += red
                white= int(cash_batch/10)
                total_white += white

            position = (player.id - self.client_id) % 4
            if position == 0:
                posX = 319
                posY = 500
            elif position == 1:
                posX = 209
                posY = 350
            elif position == 2:
                posX = 319
                posY = 90
            else:
                posX = 694
                posY = 350

            offsetX = 0
            offsetY = 0
            iteration = 0
            im = QPixmap(misc_path + "chip_250.png")
            while total_green> 0:
                if total_green > 10:
                    batch_green = 10
                    total_green -= 10
                else:
                    batch_green = total_green
                    total_green=0
                for chip in range(0, batch_green):
                    painter.drawPixmap(QRect(posX + offsetX+22, posY + offsetY-30- chip * 6, 30, 20), im)
                if iteration == 0:
                    offsetX = -10
                    offsetY = 5
                if iteration == 1:
                    offsetX = 10
                    offsetY = 5
                if iteration == 2:
                    offsetX = 0
                    offsetY = 10
                iteration += 1

            offsetX = 0
            offsetY = 0
            iteration = 0
            im = QPixmap(misc_path + "chip_100.png")
            while total_blue > 0:
                if total_blue > 10:
                    batch_blue = 10
                    total_blue -= 10
                else:
                    batch_blue = total_blue
                    total_blue=0
                for chip in range(0, batch_blue):
                    painter.drawPixmap(QRect(posX + offsetX, posY + offsetY - chip * 6, 30, 20), im)
                if iteration == 0:
                    offsetX = -10
                    offsetY = 5
                if iteration == 1:
                    offsetX = 10
                    offsetY = 5
                if iteration == 2:
                    offsetX = 0
                    offsetY = 10
                iteration += 1

            offsetX = 0
            offsetY = 0
            iteration = 0
            im = QPixmap(misc_path + "chip_50.png")
            while total_red > 0:
                if total_red > 10:
                    batch_red = 10
                    total_red -= 10
                else:
                    batch_red = total_red
                    total_red=0
                for chip in range(0, batch_red):
                    painter.drawPixmap(QRect(posX + 40 + offsetX, posY + offsetY - chip * 6, 30, 20), im)
                if iteration == 0:
                    offsetX = -10
                    offsetY = 5
                if iteration == 1:
                    offsetX = 10
                    offsetY = 5
                if iteration == 2:
                    offsetX = 0
                    offsetY = 10
                iteration += 1

            offsetX = 0
            offsetY = 0
            iteration = 0
            im = QPixmap(misc_path + "chip_10.png")
            while total_white > 0:
                if total_white > 10:
                    batch_white = 10
                    total_white -= 10
                else:
                    batch_white = total_white
                    total_white=0
                for chip in range(0, batch_white):
                    painter.drawPixmap(QRect(posX+18 + offsetX, posY+20 + offsetY - chip * 6, 30, 20), im)
                if iteration == 0:
                    offsetX = -10
                    offsetY = 5
                if iteration == 1:
                    offsetX = 10
                    offsetY = 5
                if iteration == 2:
                    offsetX = 0
                    offsetY = 10
                iteration += 1

            total_green = 0
            total_blue = 0
            total_red = 0
            total_white = 0
            cash_stack = self.gameloop.pool
            while cash_stack > 0:
                if cash_stack > 2000:
                    cash_batch = 2000
                else:
                    cash_batch = cash_stack
                cash_stack -= cash_batch

                if cash_batch > 1000:
                    green = 4
                    cash_batch -= 1000
                else:
                    green = int(cash_batch / 250)
                    cash_batch -= green * 250
                total_green += green
                if cash_batch > 500:
                    blue = 5
                    cash_batch -= 500
                else:
                    blue = int(cash_batch / 100)
                    cash_batch -= blue * 100
                total_blue += blue
                if cash_batch > 400:
                    red = 8
                    cash_batch -= 400
                else:
                    red = int(cash_batch / 50)
                    cash_batch -= red * 50
                total_red += red
                white = int(cash_batch / 10)
                total_white += white

            posX = 400
            posY = 360
            offsetX=0
            offsetY=0
            iteration = 0
            while total_green+total_white+total_red+total_blue>0:
                if total_blue>10:
                    batch_blue=10
                    total_blue-=10
                else:
                    batch_blue=total_blue
                    total_blue=0

                if total_green>10:
                    batch_green=10
                    total_green-=10
                else:
                    batch_green=total_green
                    total_green=0

                if total_red>10:
                    batch_red=10
                    total_red-=10
                else:
                    batch_red=total_red
                    total_red=0

                if total_white>10:
                    batch_white=10
                    total_white-=10
                else:
                    batch_white=total_white
                    total_white=0

                im = QPixmap(misc_path + "chip_250.png")
                for chip in range(0, batch_green):
                    painter.drawPixmap(QRect(posX+offsetX, posY+offsetY - chip * 6, 30, 20), im)
                im = QPixmap(misc_path + "chip_100.png")
                for chip in range(0, batch_blue):
                    painter.drawPixmap(QRect(posX + 40+offsetX, posY+offsetY - chip * 6, 30, 20), im)
                im = QPixmap(misc_path + "chip_50.png")
                for chip in range(0, batch_red):
                    painter.drawPixmap(QRect(posX + 80+offsetX, posY+offsetY - chip * 6, 30, 20), im)
                im = QPixmap(misc_path + "chip_10.png")
                for chip in range(0, batch_white):
                    painter.drawPixmap(QRect(posX + 120+offsetX, posY+offsetY - chip * 6, 30, 20), im)
                if iteration==0:
                    offsetX=-10
                    offsetY=5
                if iteration==1:
                    offsetX=10
                    offsetY=5
                if iteration==2:
                    offsetX=0
                    offsetY=10
                iteration+=1

        # Drawing markers
        position = (self.small_blind - self.client_id) % 4
        if position == 0:
            posX = 425
            posY = 400
        elif position == 1:
            posX = 195
            posY = 180
        elif position == 2:
            posX = 285
            posY = 120
        else:
            posX = 700
            posY = 200

        im = QPixmap(misc_path + "small_blind.png")
        painter.drawPixmap(QRect(posX, posY, 30, 30), im)

        position = (self.big_blind - self.client_id) % 4
        if position == 0:
            posX = 425
            posY = 400
        elif position == 1:
            posX = 195
            posY = 180
        elif position == 2:
            posX = 285
            posY = 120
        else:
            posX = 700
            posY = 200

        im = QPixmap(misc_path + "big_blind.png")
        painter.drawPixmap(QRect(posX, posY, 30, 30), im)

        for player in self.gameloop.players:
            position = (player.id - self.client_id) % 4
            if position == 0:
                posX = 365
                posY = 400
            elif position == 1:
                posX = 235
                posY = 260
            elif position == 2:
                posX = 365
                posY = 120
            else:
                posX = 740
                posY = 260

            if(player.state == "all-in"):
                im = QPixmap(misc_path + "allin.png")
                painter.drawPixmap(QRect(posX, posY, 30, 30), im)

    def update_meta(self):
        print("Update meta called")
        self.poolLabel.setText("Pool: {}".format(self.gameloop.pool))
        print("Players in gameloop {}".format(len(self.gameloop.players)))
        for player in self.gameloop.players:
            position = (player.id-self.client_id)%4
            self.nicknames[position].setText(player.name)
            self.cash_labels[position].setText("Cash: {}".format(player.cash))
            self.bet_labels[position].setText("Bet: {}".format(player.bet))
            self.state_labels[position].setText(player.state.capitalize())

        self.small_blind = self.gameloop.small_blind
        self.big_blind = self.gameloop.big_blind
        self.update()

    def update_table(self):
        self.table=self.gameloop.table
        self.update()

    def update_hand(self):
        self.gameloop.players[self.client_id].hand=self.gameloop.hand
        self.update()

    def update_client(self):
        self.table_id = self.gameloop.table_id
        self.client_id = self.gameloop.client_id
        self.setWindowTitle("Poker Table #{} - {}".format(self.table_id,self.gameloop.nickname))
        self.tableLabel.setText("Table #{}".format(self.table_id))

    def discard(self):
        self.table=[]
        self.update()

    def game_end(self,result):
        if result == "lose":
            self.statusLabel.setText("You're broke! You lose!")
        else:
            self.statusLabel.setText("Congratulations! You win!")
        self.update()
        self.close()

    def update_move(self):
        if self.client_id == self.gameloop.move:
            self.statusLabel.setText("Your turn")
        else:
            self.statusLabel.setText("{}'s turn".format(self.gameloop.players[self.gameloop.move].name))
        self.update()

    def initiate_move(self):
        self.CW = ChoiseWindow(self.gameloop.players[self.client_id].name)
        self.CW.answer.connect(self.gameloop.resume)
        self.CW.show()

    def showdown(self):
        if len(self.gameloop.winners) == 1:
            winners =self.gameloop.players[self.gameloop.winners[0]].name
        else:
            winners=""
            for winner in self.gameloop.winners:
                winners+="{} ".format(self.gameloop.players[winner].name)
        self.statusLabel.setText("Winners: {}".format(winners))
        self.update()
# **************************************************

class ConnectionThread(QThread):

    connected_to_server = pyqtSignal()
    connected_to_table = pyqtSignal()
    connection_error = pyqtSignal()

    def __init__(self,client):
        QThread.__init__(self)
        self.client=client

    def __del__(self):
        self.wait()

    def run(self):
        connected=False
        while not connected:
            connected=True
            try:
                self.client.connect(('localhost',5000))
            except socket.error:
                connected = False

        self.connected_to_server.emit()
        command = recieve(self.client,separator)
        if command == "100":
            self.client.sendall(("200"+separator).encode())
            self.connected_to_table.emit()
        else:
            self.connection_error.emit()



class GameThread(QThread):

    send_client_info = pyqtSignal()
    send_meta = pyqtSignal()
    discard_request = pyqtSignal()
    send_hand = pyqtSignal()
    send_table = pyqtSignal()
    reveal_hands = pyqtSignal()
    game_over = pyqtSignal(str)
    player_move_info = pyqtSignal()
    move_request = pyqtSignal()
    showdown_request = pyqtSignal()

    def __init__(self,client,nickname):
        QThread.__init__(self)
        self.client = client
        self.nickname=nickname
        self.players=[]
        self.hand=[]
        self.table=[]
        self.player_total = 4
        self.big_blind = 1
        self.small_blind = 0
        self.pool = 0
        self.move=0
        self.pause=False
        self.choise="call"
        self.bid=0
        self.winners = []

    def __del__(self):
        self.wait()

    def resume(self,action,bid):
        self.pause = False
        self.choise=action
        self.bid = bid

    def run(self):
        while True:
            print("Waiting for command:")
            command = recieve(self.client, separator)
            print(command)
            try:
                code = int(command)
            except ValueError:
                self.client.close()
                break

            if code == 100:
                self.client.sendall(("200" + separator).encode())
            elif code == 101:
                self.client.sendall(("201" + separator).encode("utf-8"))
                self.pause = True
                self.move_request.emit()
                while self.pause:
                    pass

                if self.choise == "pass":
                    self.client.sendall(("251" + separator).encode("utf-8"))
                elif self.choise == "call":
                    self.client.sendall(("252" + separator).encode("utf-8"))
                elif self.choise == "raise":
                    self.client.sendall(("253" + separator).encode("utf-8"))
                    self.client.sendall((str(self.bid) + separator).encode())
                elif self.choise == "all-in":
                    self.client.sendall(("254" + separator).encode("utf-8"))
                else:
                    self.client.sendall(("255" + separator).encode("utf-8"))
            elif code == 102:
                self.client.sendall(("202"+separator).encode())
                times = int(recieve(self.client,separator))
                for i in range(0,times):
                    pid = int(recieve(self.client,separator))
                    for i in range(0, 2):
                        fig = int(recieve(self.client,separator))
                        col = int(recieve(self.client,separator))
                        c = Card(Color(col),Figure(fig))
                        self.players[pid].hand.append(c)
                self.showdown_request.emit()
                sleep(10)
                self.winners = []
            elif code == 103:
                self.client.sendall(("203" + separator).encode())
                self.big_blind = int(recieve(self.client, separator))
            elif code == 104:
                self.client.sendall(("204" + separator).encode())
                self.small_blind = int(recieve(self.client, separator))
            elif code == 105:
                self.client.sendall(("205"+separator).encode())
                self.move = int(recieve(self.client,separator))
                self.player_move_info.emit()
            elif code == 106:
                self.client.sendall(("206" + separator).encode())
                for i in range(0,2):
                    fig = int(recieve(self.client, separator))
                    col = int(recieve(self.client, separator))
                    card = Card(Color(col), Figure(fig))
                    self.hand.append(card)
                self.send_hand.emit()
            elif code == 107:
                self.client.sendall(("207"+separator).encode())
                self.client.close()
                self.game_over.emit("lose")
                break
            elif code == 108:
                self.client.sendall(("208"+separator).encode())
                self.client.close()
                self.game_over.emit("win")
                break
            elif code == 109:
                self.client.sendall(("209" + separator).encode())
                self.pool = int(recieve(self.client, separator))
            elif code == 110:
                self.client.sendall(("210" + separator).encode())
                self.client.sendall((self.nickname + separator).encode())
            elif code == 111:
                self.client.sendall(("211"+separator).encode())
                self.table_id = int(recieve(self.client, separator))
                self.client_id = int(recieve(self.client, separator))
                self.player_total = int(recieve(self.client,separator))
                self.send_client_info.emit()
            elif code == 112:
                self.client.sendall(("212" + separator).encode())
                ptimes = int(recieve(self.client,separator))
                for i in range(0,ptimes):
                    pid = int(recieve(self.client, separator))
                    pname = recieve(self.client, separator)
                    pcash = int(recieve(self.client, separator))
                    pstate = recieve(self.client, separator)
                    pbet = int(recieve(self.client, separator))
                    if len(self.players) < self.player_total:
                        p = Player(pid, pname, pcash, pstate, pbet)
                        self.players.append(p)
                    else:
                        for player in self.players:
                            if player.id == pid:
                                player.state = pstate
                                player.cash = pcash
                                player.bet = pbet
                self.send_meta.emit()
            elif code == 113:
                self.client.sendall(("213" + separator).encode())
                times = int(recieve(self.client, separator))
                for i in range(0,times):
                    self.winners.append(int(recieve(self.client,separator)))
            elif code == 114:
                self.client.sendall(("214" + separator).encode())
                loser = int(recieve(self.client, separator))
                for player in self.players:
                    if player.id == loser:
                        print(player.name + " lost the game!!!")
                        player.loser = True
                        player.state="none"
            elif code == 115:
                self.client.sendall(("215" + separator).encode())
                n = int(recieve(self.client,separator))
                for i in range(0,n):
                    fig = int(recieve(self.client, separator))
                    col = int(recieve(self.client, separator))
                    card = Card(Color(col), Figure(fig))
                    self.table.append(card)
                self.send_table.emit()
            elif code == 116:
                self.client.sendall(("216" + separator).encode())
                for player in self.players:
                    player.hand=[]
                self.hand = []
                self.table = []
                self.discard_request.emit()
            else:
                self.client.sendall(("440" + separator).encode('utf-8'))
                break



class ChoiseWindow(QtWidgets.QWidget):

    answer = pyqtSignal(str,int)

    def __init__(self,name):
        QtWidgets.QWidget.__init__(self)
        self.setWindowTitle("Choose your move {}".format(name))

        layout = QtWidgets.QHBoxLayout()
        self.bPass = QtWidgets.QPushButton("Pass")
        self.bPass.clicked.connect(self.Pass)
        layout.addWidget(self.bPass)

        self.bCall = QtWidgets.QPushButton("Call")
        self.bCall.clicked.connect(self.Call)
        layout.addWidget(self.bCall)

        self.bAllin = QtWidgets.QPushButton("All-in")
        self.bAllin.clicked.connect(self.Allin)
        layout.addWidget(self.bAllin)

        self.bRaise = QtWidgets.QPushButton("Raise")
        self.bRaise.clicked.connect(self.Raise)
        layout.addWidget(self.bRaise)

        self.Textfield = QtWidgets.QLineEdit()
        self.Textfield.setFixedWidth(40)
        layout.addWidget(self.Textfield)
        self.setLayout(layout)

    def Pass(self):
        self.answer.emit("pass",0)
        self.close()

    def Call(self):
        self.answer.emit("call",0)
        self.close()

    def Raise(self):
        try:
            x = int(self.Textfield.text())
            if x > 0 and x%10 == 0:
                self.answer.emit("raise", x)
                self.close()
        except ValueError:
            pass

    def Allin(self):
        self.answer.emit("all-in",0)
        self.close()






class ConnectionWindow(QtWidgets.QWidget):

    def __init__(self):
        QtWidgets.QWidget.__init__(self)
        self.width = 200
        self.height = 100
        self.setGeometry(600, 300, self.width, self.height)
        self.setGUI()
        self.client = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        self.conn_thread = ConnectionThread(self.client)
        self.conn_thread.connected_to_server.connect(self.display_connection)
        self.conn_thread.connected_to_table.connect(self.switch)
        self.conn_thread.connection_error.connect(self.close)
        self.conn_thread.start()
        self.show()


    def setGUI(self):
        layout = QtWidgets.QGridLayout()
        self.label1 = QtWidgets.QLabel("Waiting to connect...")
        layout.addWidget(self.label1)

        self.label2 = QtWidgets.QLabel("")
        layout.addWidget(self.label2)
        self.setLayout(layout)

    def display_connection(self):
        self.label1.setText("Connected to server")
        self.label2.setText("Waiting for other players...")

    def switch(self):
        self.cam = LoginWindow(self.client)
        self.cam.show()
        self.close()


class LoginWindow(QtWidgets.QWidget):

    def __init__(self,client):
        QtWidgets.QWidget.__init__(self)
        self.width = 200
        self.height = 100
        self.setGeometry(600,300,self.width,self.height)
        self.setWindowTitle("Enter the table")
        self.client = client

        layout = QtWidgets.QGridLayout()

        self.label = QtWidgets.QLabel("Enter your nickname")
        layout.addWidget(self.label)

        self.textfield = QtWidgets.QLineEdit()
        layout.addWidget(self.textfield)

        self.button = QtWidgets.QPushButton("Confirm")
        self.button.clicked.connect(self.switch)
        layout.addWidget(self.button)

        self.setLayout(layout)

    def switch(self):
        self.cam = TableWindow(self.textfield.text(),self.client)
        self.cam.show()
        self.close()

    def keyPressEvent(self, event):

        if event.key() == Qt.Key_Enter-1:
            self.switch()

app = QtWidgets.QApplication(sys.argv)
window=ConnectionWindow()
sys.exit(app.exec_())
