import socket
import threading

from time import sleep
from cardDeck import *

separator = "/SEPP/"

class ConfirmationError(Exception):
    pass

def recieve(client, sep):
    data = ""
    while not sep in data:
        data+=client.recv(1).decode()
    return data.split(sep)[0]

class Player:

    def __init__(self, id, client, name, cash, state, bet):
        self.id = id
        self.client = client
        self.name = name
        self.cash = cash
        self.state = state
        self.bet = bet
        self.hand=[]


def threads_running(threads):

    for thread in threads:
        if thread.isAlive():
            return True
    return False


def client_thread_initial(player,table_id):
    try:
        player.client.sendall(("100"+separator).encode())
        confirmation = recieve(player.client, separator)
        if confirmation != "200":
            raise ConfirmationError(400)
        player.client.sendall(("111"+separator).encode())
        confirmation = recieve(player.client,separator)
        if confirmation != "211":
            raise ConfirmationError(411)
        player.client.sendall((str(table_id)+separator).encode())
        player.client.sendall((str(player.id)+separator).encode())
        player.client.sendall((str(player_limit)+separator).encode())
        player.client.sendall(("110"+separator).encode())
        confirmation = recieve(player.client,separator)
        if confirmation != "210":
            raise ConfirmationError(410)
        player.name=recieve(player.client,separator)
    except ConfirmationError as error:
        raise ConfirmationError("{} from client {} on table {}".format(error,player.id,table_id)) from error


def client_send_meta(client,players,blinds,pool):
    # 1. Send Meta data (pool, blinds)
    # Pool
    client.sendall(("109" + separator).encode())
    confirmation = recieve(client, separator)
    if confirmation != "209":
        raise ConfirmationError(409)
    client.sendall((str(pool) + separator).encode())
    # Blinds
    client.sendall(("103" + separator).encode())
    confirmation = recieve(client, separator)
    if confirmation != "203":
        raise ConfirmationError(403)
    client.sendall((str(players[blinds[0]].id) + separator).encode())
    client.sendall(("104" + separator).encode())
    confirmation = recieve(client, separator)
    if confirmation != "204":
        raise ConfirmationError(404)
    client.sendall((str(players[blinds[1]].id) + separator).encode())

    # 2. Send player data
    try:
        client.sendall(("112" + separator).encode())
        confirmation = recieve(client, separator)
        if confirmation != "212":
            raise ConfirmationError(412)
        client.sendall((str(len(players))+separator).encode())
        for player in players:
            client.sendall((str(player.id) + separator).encode())  # id
            client.sendall((player.name + separator).encode())  # name
            client.sendall((str(player.cash) + separator).encode())  # cash
            client.sendall((player.state + separator).encode())  # state
            client.sendall((str(player.bet) + separator).encode())  # bet
    except ConfirmationError as error:
        raise ConfirmationError("{} << from send_meta".format(error)) from error


def send_meta(players,blinds,pool):
    threads=[]
    for player in players:
        t = threading.Thread(target=client_send_meta, args=(player.client,players,blinds,pool))
        threads.append(t)
        t.start()

    while threads_running(threads):
        pass


def reset_state(players, all):
    if all:
        for player in players:
            player.state = "none"
    else:
        for player in players:
            if player.state == "call":
                player.state = "none"


def reset_bets(players):
    for player in players:
        player.bet = 0


def take_blinds(players,small,big,amm,pool):
    for player in players:
        if player.id==small:
            player.cash-=amm
            player.bet=amm
        if player.id == big:
            player.cash-=amm*2
            player.bet=amm*2
    pool+=amm*3


def send_hands(players):
    for player in players:
        player.client.sendall(("106"+separator).encode())
        confirmation = recieve(player.client,separator)
        if confirmation != "206":
            raise ConfirmationError(406)
        for card in player.hand:
            player.client.sendall((str(card.fig.value)+separator).encode())
            player.client.sendall((str(card.color.value)+separator).encode())

def send_table(players,table,option):
    if option == 3:
        for player in players:
            player.client.sendall(("115"+separator).encode())
            confirmation = recieve(player.client, separator)
            if confirmation != "215":
                raise ConfirmationError(415)
            player.client.sendall(("3"+separator).encode())
            for i in range(0, 3):
                player.client.sendall((str(table[i].fig.value)+separator).encode())
                player.client.sendall((str(table[i].color.value)+separator).encode())
    else:
        for player in players:
            player.client.sendall(("115"+separator).encode())
            confirmation = recieve(player.client,separator)
            if confirmation != "215":
                raise ConfirmationError(415)
            player.client.sendall(("1"+separator).encode())
            player.client.sendall((str(table[option-1].fig.value)+separator).encode())
            player.client.sendall((str(table[option-1].color.value)+separator).encode())


def all_call(players):
    for player in players:
        if player.state!="call":
            return False
    return True


def table_thread(players, table_id):
    try:
        # Table Meta Variables
        client_handlers = []
        big_blind = 1
        small_blind = 0
        pool = 0
        blind_size = 20
        player_in_game=player_limit
        try:
            for player in players:
                t = threading.Thread(target=client_thread_initial,args=(player,table_id))
                client_handlers.append(t)
                t.start()

            while threads_running(client_handlers):
                pass
        except ConfirmationError as error:
            raise ConfirmationError("{} << inside initial client thread".format(error)) from error

        try:
            send_meta(players,[big_blind,small_blind],pool)
        except ConfirmationError as error:
            raise ConfirmationError("{} << inside initial send meta".format(error)) from error

        deck = Card_Deck()
        deck.shuffle(3)

        # Game Loop
        try:
            while len(players) > 1:
                big_blind = big_blind % player_in_game
                small_blind = small_blind % player_in_game

                # Meta reset
                reset_bets(players)
                reset_state(players,True)
                take_blinds(players, small_blind, big_blind, blind_size, pool)
                deck.shuffle(3)
                hands = deck.deal_hands(player_in_game)
                table = deck.deal_table()
                for i in range(0,player_in_game):
                    players[i].hand=hands[i]
                send_hands(players)
                highest_bid = blind_size * 2
                pool += blind_size * 3
                send_meta(players,[big_blind,small_blind],pool)

                # Pre-Flop
                # Order -> utg utg+1, small, big
                players_in_round = players.copy()
                all_in_players = []
                cp = big_blind + 1
                raise_flag = -1
                all_in_last=True
                while (len(players_in_round) > 1 and not all_call(players_in_round)) or (all_in_last and len(players_in_round)==1):
                    all_in_last = False
                    cp = cp % len(players_in_round)
                    current_player = players_in_round[cp]
                    for player in players:
                        player.client.sendall(("105"+separator).encode())
                        confirmation = recieve(player.client,separator)
                        if confirmation != "205":
                            raise ConfirmationError(405)
                        player.client.sendall((str(current_player.id)+separator).encode())

                    if current_player.state == "raise" and current_player.id == raise_flag:
                        players_in_round[cp].state = "call"
                    else:
                        current_player.client.sendall(("101" + separator).encode())
                        confirmation = recieve(current_player.client, separator)
                        if confirmation != "201":
                            raise ConfirmationError(401)
                        answer = recieve(current_player.client, separator)
                        if answer == "251":
                            current_player.state = "pass"
                            players_in_round.pop(cp)
                            all_in_last = False
                        elif answer == "252":
                            current_player.state = "call"
                            current_player.cash -= highest_bid - current_player.bet
                            pool += highest_bid - current_player.bet
                            current_player.bet = highest_bid
                            cp += 1
                            if len(players_in_round) == 1:
                                all_in_players.append(players_in_round[0])
                                players_in_round.pop()
                            all_in_last = False
                        elif answer == "253":
                            current_player.state = "raise"
                            raise_bid = int(recieve(current_player.client, separator))
                            highest_bid += raise_bid
                            current_player.cash -= highest_bid - current_player.bet
                            pool += highest_bid - current_player.bet
                            current_player.bet = highest_bid
                            raise_flag = current_player.id
                            cp += 1
                            if len(players_in_round) == 1:
                                all_in_players.append(players_in_round[0])
                                players_in_round.pop()
                            all_in_last = False
                        elif answer == "254":
                            current_player.state = "all-in"
                            if current_player.cash + current_player.bet > highest_bid:
                                highest_bid = current_player.cash + current_player.bet
                                current_player.bet = highest_bid
                                raise_flag = current_player.id
                            else:
                                current_player.bet = current_player.cash + current_player.bet
                            pool += current_player.cash
                            current_player.cash = 0
                            all_in_players.append(players_in_round[cp])
                            players_in_round.pop(cp)
                            all_in_last=True
                        else:
                            raise ConfirmationError(450)
                    if all_in_last:
                        for player in players_in_round:
                            player.state="none"
                    send_meta(players, [big_blind, small_blind], pool)

                # Post-flop 3
                # order small ->big->utg->utg+1
                if len(players_in_round) + len(all_in_players) > 1:
                    send_table(players,table, 3)
                    reset_bets(players)
                    reset_state(players, False)
                    highest_bid = 0
                    if len(players_in_round)==1:
                        sleep(1)
                    send_meta(players,[big_blind,small_blind],pool)
                    cp = small_blind

                raise_flag = -1
                while (len(players_in_round) > 1 and not all_call(players_in_round)) or (all_in_last and len(players_in_round)==1):
                    all_in_last = False
                    cp = cp % len(players_in_round)
                    current_player = players_in_round[cp]
                    for player in players:
                        player.client.sendall(("105" + separator).encode())
                        confirmation = recieve(player.client, separator)
                        if confirmation != "205":
                            raise ConfirmationError(405)
                        player.client.sendall((str(current_player.id) + separator).encode())

                    if current_player.state == "raise" and current_player.id == raise_flag:
                        players_in_round[cp].state = "call"
                    else:
                        current_player.client.sendall(("101" + separator).encode())
                        confirmation = recieve(current_player.client, separator)
                        if confirmation != "201":
                            raise ConfirmationError(401)
                        answer = recieve(current_player.client, separator)
                        if answer == "251":
                            current_player.state = "pass"
                            players_in_round.pop(cp)
                            all_in_last = False
                        elif answer == "252":
                            current_player.state = "call"
                            current_player.cash -= highest_bid - current_player.bet
                            pool += highest_bid - current_player.bet
                            current_player.bet = highest_bid
                            cp += 1
                            if len(players_in_round) == 1:
                                all_in_players.append(players_in_round[0])
                                players_in_round.pop()
                            all_in_last = False
                        elif answer == "253":
                            current_player.state = "raise"
                            raise_bid = int(recieve(current_player.client, separator))
                            highest_bid += raise_bid
                            current_player.cash -= highest_bid - current_player.bet
                            pool += highest_bid - current_player.bet
                            current_player.bet = highest_bid
                            raise_flag = current_player.id
                            cp += 1
                            if len(players_in_round) == 1:
                                all_in_players.append(players_in_round[0])
                                players_in_round.pop()
                            all_in_last = False
                        elif answer == "254":
                            current_player.state = "all-in"
                            if current_player.cash + current_player.bet > highest_bid:
                                highest_bid = current_player.cash + current_player.bet
                                current_player.bet = highest_bid
                                raise_flag = current_player.id
                            else:
                                current_player.bet = current_player.cash + current_player.bet
                            pool += current_player.cash
                            current_player.cash = 0
                            all_in_players.append(players_in_round[cp])
                            players_in_round.pop(cp)
                            all_in_last = True
                        else:
                            raise ConfirmationError(450)
                    if all_in_last:
                        for player in players_in_round:
                            player.state = "none"
                    send_meta(players, [big_blind, small_blind], pool)

                # Post-Flop 4 cards
                if len(players_in_round) + len(all_in_players) > 1:
                    send_table(players,table, 4)
                    reset_bets(players)
                    reset_state(players,False)
                    highest_bid = 0
                    if len(players_in_round)==1:
                        sleep(1)
                    send_meta(players,[big_blind,small_blind],pool)
                    cp = small_blind

                raise_flag = -1
                while (len(players_in_round) > 1 and not all_call(players_in_round)) or (all_in_last and len(players_in_round)==1):
                    all_in_last = False
                    cp = cp % len(players_in_round)
                    current_player = players_in_round[cp]
                    for player in players:
                        player.client.sendall(("105" + separator).encode())
                        confirmation = recieve(player.client, separator)
                        if confirmation != "205":
                            raise ConfirmationError(405)
                        player.client.sendall((str(current_player.id) + separator).encode())

                    if current_player.state == "raise" and current_player.id == raise_flag:
                        players_in_round[cp].state = "call"
                    else:
                        current_player.client.sendall(("101" + separator).encode())
                        confirmation = recieve(current_player.client, separator)
                        if confirmation != "201":
                            raise ConfirmationError(401)
                        answer = recieve(current_player.client, separator)
                        if answer == "251":
                            current_player.state = "pass"
                            players_in_round.pop(cp)
                            all_in_last = False
                        elif answer == "252":
                            current_player.state = "call"
                            current_player.cash -= highest_bid - current_player.bet
                            pool += highest_bid - current_player.bet
                            current_player.bet = highest_bid
                            cp += 1
                            if len(players_in_round) == 1:
                                all_in_players.append(players_in_round[0])
                                players_in_round.pop()
                            all_in_last = False
                        elif answer == "253":
                            current_player.state = "raise"
                            raise_bid = int(recieve(current_player.client, separator))
                            highest_bid += raise_bid
                            current_player.cash -= highest_bid - current_player.bet
                            pool += highest_bid - current_player.bet
                            current_player.bet = highest_bid
                            raise_flag = current_player.id
                            cp += 1
                            if len(players_in_round) == 1:
                                all_in_players.append(players_in_round[0])
                                players_in_round.pop()
                            all_in_last = False
                        elif answer == "254":
                            current_player.state = "all-in"
                            if current_player.cash + current_player.bet > highest_bid:
                                highest_bid = current_player.cash + current_player.bet
                                current_player.bet = highest_bid
                                raise_flag = current_player.id
                            else:
                                current_player.bet = current_player.cash + current_player.bet
                            pool += current_player.cash
                            current_player.cash = 0
                            all_in_players.append(players_in_round[cp])
                            players_in_round.pop(cp)
                            all_in_last = True
                        else:
                            raise ConfirmationError(450)
                    if all_in_last:
                        for player in players_in_round:
                            player.state = "none"
                    send_meta(players, [big_blind, small_blind], pool)

                # Post-Flop 5 cards
                if len(players_in_round) + len(all_in_players) > 1:
                    send_table(players, table, 5)
                    reset_bets(players)
                    reset_state(players, False)
                    highest_bid = 0
                    if len(players_in_round)==1:
                        sleep(1)
                    send_meta(players,[big_blind,small_blind],pool)
                    cp = small_blind

                raise_flag = -1
                while (len(players_in_round) > 1 and not all_call(players_in_round)) or (all_in_last and len(players_in_round)==1):
                    all_in_last = False
                    cp = cp % len(players_in_round)
                    current_player = players_in_round[cp]
                    for player in players:
                        player.client.sendall(("105" + separator).encode())
                        confirmation = recieve(player.client, separator)
                        if confirmation != "205":
                            raise ConfirmationError(405)
                        player.client.sendall((str(current_player.id) + separator).encode())

                    if current_player.state == "raise" and current_player.id == raise_flag:
                        players_in_round[cp].state = "call"
                    else:
                        current_player.client.sendall(("101" + separator).encode())
                        confirmation = recieve(current_player.client, separator)
                        if confirmation != "201":
                            raise ConfirmationError(401)
                        answer = recieve(current_player.client, separator)
                        if answer == "251":
                            current_player.state = "pass"
                            players_in_round.pop(cp)
                            all_in_last = False
                        elif answer == "252":
                            current_player.state = "call"
                            current_player.cash -= highest_bid - current_player.bet
                            pool += highest_bid - current_player.bet
                            current_player.bet = highest_bid
                            cp += 1
                            if len(players_in_round) == 1:
                                all_in_players.append(players_in_round[0])
                                players_in_round.pop()
                            all_in_last = False
                        elif answer == "253":
                            current_player.state = "raise"
                            raise_bid = int(recieve(current_player.client, separator))
                            highest_bid += raise_bid
                            current_player.cash -= highest_bid - current_player.bet
                            pool += highest_bid - current_player.bet
                            current_player.bet = highest_bid
                            raise_flag = current_player.id
                            cp += 1
                            if len(players_in_round) == 1:
                                all_in_players.append(players_in_round[0])
                                players_in_round.pop()
                            all_in_last = False
                        elif answer == "254":
                            current_player.state = "all-in"
                            if current_player.cash + current_player.bet > highest_bid:
                                highest_bid = current_player.cash + current_player.bet
                                current_player.bet = highest_bid
                                raise_flag = current_player.id
                            else:
                                current_player.bet = current_player.cash + current_player.bet
                            pool += current_player.cash
                            current_player.cash = 0
                            all_in_players.append(players_in_round[cp])
                            players_in_round.pop(cp)
                            all_in_last = True
                        else:
                            raise ConfirmationError(450)
                    if all_in_last:
                        for player in players_in_round:
                            player.state = "none"
                    send_meta(players, [big_blind, small_blind], pool)

                # Finish
                players_in_round = players_in_round + all_in_players
                if len(players_in_round) + len(all_in_players) > 1:  # Showdown
                    remaining_hands = []
                    for player in players_in_round:
                        remaining_hands.append((hands[player.id],player.id))
                    winners = evaluate_hands(remaining_hands, table)

                    pool = pool - pool % len(winners)
                    poolpart = int(pool / len(winners))
                    for player in players:
                        player.client.sendall(("113"+separator).encode())
                        confirmation = recieve(player.client,separator)
                        if confirmation != "213":
                            raise ConfirmationError(413)
                        player.client.sendall((str(len(winners))+separator).encode())
                        for winner in winners:
                            player.client.sendall((str(winner[1])+separator).encode())

                    for winner in winners:
                        players[winner[1]].cash += poolpart
                    pool = pool % len(winners)
                    # Show_hands
                    for player in players:
                        player.client.sendall(("102"+separator).encode())
                        confirmation = recieve(player.client,separator)
                        if confirmation != "202":
                            raise ConfirmationError(402)
                        player.client.sendall((str(len(players_in_round))+separator).encode())
                        for player2 in players_in_round:
                            player.client.sendall((str(player2.id)+separator).encode())
                            for card in hands[player2.id]:
                                player.client.sendall((str(card.fig.value) + separator).encode())
                                player.client.sendall((str(card.color.value) + separator).encode())

                else:  # One player left
                    players_in_round[0].cash += pool
                    for player in players:
                        player.client.sendall(("113" + separator).encode())
                        confirmation = recieve(player.client, separator)
                        if confirmation != "213":
                            raise ConfirmationError(413)
                        player.client.sendall(("1" + separator).encode())
                        player.client.sendall((str(players_in_round[0].id) + separator).encode())
                    pool=0

                # Clean table
                deck.shuffle_in(hands, table)
                for player in players:
                    player.client.sendall(("116" + separator).encode())
                    confirmation = recieve(player.client, separator)
                    if confirmation != "216":
                        raise ConfirmationError(416)

                # Check for players who lost and remove them
                for player in players:
                    if player.cash == 0:
                        for player2 in players:
                            player2.client.sendall(("114" + separator).encode())
                            confirmation = recieve(player2.client, separator)
                            if confirmation != "214":
                                raise ConfirmationError(414)
                            player2.client.sendall((str(player.id) + separator).encode())

                n = 0
                while n < len(players):
                    if players[n].cash == 0:
                        players[n].client.sendall(("107"+separator).encode())
                        confirmation = recieve(players[n].client,separator)
                        if confirmation != "207":
                            raise ConfirmationError(407)
                        players.pop(n)
                    else:
                        n += 1

                player_in_game = len(players)

                # Move blinds
                big_blind += 1
                small_blind += 1

            players[0].client.sendall(("108"+separator).encode())
            confirmation = recieve(players[0].client)
            if confirmation != "208":
                raise ConfirmationError(408)
        except ConfirmationError as error:
            raise ConfirmationError("{} << inside game loop".format(error)) from error
    except ConfirmationError as error:
        raise ConfirmationError("{} in table thread".format(error)) from error
    print("Table {} closed".format(table_id))







s = socket.socket(socket.AF_INET,socket. SOCK_STREAM)
s.bind(('localhost', 5000))
s.listen(5)
tables = []
players = []
player_limit = 4

id_counter = 0
while True:
    client, addr = s.accept()
    print("Client conencted")
    p = Player(id_counter,client,"NoName",2000,"none",0)
    players.append(p)
    id_counter+=1
    if len(players) == player_limit:
        tables.append(players)
        tt = threading.Thread(target=table_thread, args=(players, len(tables)))
        try:
            tt.start()
        except ConfirmationError as final_error:
            print(final_error)
        players = []
        id_counter = 0
