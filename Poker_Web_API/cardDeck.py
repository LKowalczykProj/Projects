from enum import Enum
import random


class Color(Enum):
    spade = 0
    club = 1
    diamond = 2
    heart = 3


class Figure(Enum):
    DWOJKA = 0
    TROJKA = 1
    CZWORKA = 2
    PIATKA = 3
    SZOSTKA = 4
    SIODEMKA = 5
    OSEMKA = 6
    DZIEWIATKA = 7
    DZIESIATKA = 8
    J = 9
    Q = 10
    K = 11
    A = 12


class Card:

    def __init__(self, col, fig):
        self.color = col
        self.fig = fig  # 1 - 13 as - krol


class Card_Deck:

    def __init__(self):
        self.cards = []
        for i in range(0, 52):
            card = Card(Color(int(i/13)), Figure(i % 13))
            self.cards.append(card)
        self.shuffle(3)

    def print_deck(self):
        for card in self.cards:
            print("{} {} ".format(card.fig.name, card.color.name))

    def shuffle(self, n):
        for i in range(0, n):
            for j in range(0, 1000):
                a = random.randint(0, 51)
                b = random.randint(0, 51)
                while a == b:
                    b = random.randint(0, 51)
                temp = self.cards[a]
                self.cards[a] = self.cards[b]
                self.cards[b] = temp

    def deal_hands(self, amm):
        hands = []
        for i in range(0, amm):
            hand = [self.cards.pop()]
            hands.append(hand)
        for i in range(0, amm):
            hands[i].append(self.cards.pop())
        return hands

    def deal_table(self):
        table = []
        for i in range(0, 7):
            if i == 3 or i == 5:
                self.cards.insert(0, self.cards.pop())
            else:
                table.append(self.cards.pop())
        return table

    def shuffle_in(self, hands, table):
        for hand in hands:
            for card in hand:
                self.cards.append(card)
        for card in table:
            self.cards.append(card)
        self.shuffle(3)


def sort_by_color(val):
    return val.color.value


def sort_by_figure(val):
    return val.fig.value


def sort_by_power(val):
    return int(val[0])


def check_poker(cards):
    cards.sort(key=sort_by_figure, reverse=True)
    for i in range(0, 3):
        cardbatch = cards[i:i+5]
        poker = True
        for j in range(0, 4):
            if cardbatch[j].color.value != cardbatch[j+1].color.value:
                poker = False
                break
            elif cardbatch[j].fig.value != cardbatch[j+1].fig.value+1:
                poker = False
                break
        if poker:
            return "80000000000"
    return "0"


def check_four(cards):
    cards.sort(key=sort_by_figure, reverse=True)
    four_value = -1
    s1 = ""
    for i in range(0, 4):
        cardbatch = cards[i:i+4]
        if cardbatch[0].fig.value == cardbatch[1].fig.value == cardbatch[2].fig.value == cardbatch[3].fig.value:
            four_value = cardbatch[0].fig.value
            if cardbatch[0].fig.value < 10:
                s1 = "0"+str(cardbatch[0].fig.value)
            else:
                s1 = str(cardbatch[0].fig.value)

    if four_value >= 0:
        for i in range(0, 7):
            if cards[i].fig.value != four_value:
                if cards[i].fig.value < 10:
                    s2 = "0" + str(cards[i].fig.value)
                else:
                    s2 = str(cards[i].fig.value)
                return "7"+s1+s2+"000000"
    return "0"


def check_full(cards):
    cards.sort(key=sort_by_figure, reverse=True)
    triple_found = False
    triple_value = 0
    s1 = ""
    for i in range(1, 6):
        if cards[i-1].fig.value == cards[i].fig.value == cards[i+1].fig.value:
            triple_found = True
            triple_value = cards[i].fig.value
            if triple_value < 10:
                s1 = "0" + str(triple_value)
            else:
                s1 = str(triple_value)
            break

    if not triple_found:
        return "0"
    else:
        for i in range(0, 6):
            if cards[i].fig.value == cards[i+1].fig.value and cards[i].fig.value != triple_value:
                if cards[i].fig.value < 10:
                    s2 = "0" + str(cards[i].fig.value)
                else:
                    s2 = str(cards[i].fig.value)
                return "6"+s1+s2+"000000"
    return "0"


def check_flush(cards):
    hand = cards[0:2]
    hand.sort(key=sort_by_figure, reverse=True)
    cards.sort(key=sort_by_color)
    color = 5
    for i in range(0, 3):
        cardbatch = cards[i:i+5]
        if cardbatch[0].color.value == cardbatch[1].color.value == cardbatch[2].color.value == cardbatch[3].color.value == cardbatch[4].color.value:
            color = cardbatch[0].color.value
    if color != 5:
        if hand[0].color.value == color:
            if hand[0].fig.value < 10:
                s1 = "0" + str(hand[0].fig.value)
            else:
                s1 = str(hand[0].fig.value)
        else:
            s1 = "00"
        if hand[1].color.value == color:
            if hand[1].fig.value < 10:
                s2 = "0" + str(hand[1].fig.value)
            else:
                s2 = str(hand[1].fig.value)
        else:
            s2 = "00"
        if int(s1) < int(s2):
            temp = s1
            s1 = s2
            s2 = temp
        return "5"+s1+s2+"000000"
    return "0"


def check_straight(cards):
    cards.sort(key=sort_by_figure, reverse=True)
    for i in range(0, 3):
        cardbatch = cards[i:i+5]
        is_straight = True
        for j in range(0, 4):
            if cardbatch[j].fig.value != cardbatch[j+1].fig.value+1:
                is_straight = False
                break
        if is_straight:
            if cardbatch[0].fig.value < 10:
                s = "0" + str(cardbatch[0].fig.value)
            else:
                s = str(cardbatch[0].fig.value)
            return "4"+s+"00000000"

    return "0"


def check_three(cards):
    cards.sort(key=sort_by_figure, reverse=True)
    three_value = -1
    s1 = ""
    s2 = ""
    s3 = ""
    for i in range(1, 6):
        if cards[i-1].fig.value == cards[i].fig.value == cards[i+1].fig.value:
            three_value = cards[i].fig.value
            if cards[i].fig.value < 10:
                s1 = "0" + str(cards[i].fig.value)
            else:
                s1 = str(cards[i].fig.value)
            break
    if three_value >= 0:
        for i in range(0, 7):
            if cards[i].fig.value != three_value:
                if s2 == "":
                    if cards[i].fig.value < 10:
                        s2 = "0" + str(cards[i].fig.value)
                    else:
                        s2 = str(cards[i].fig.value)
                elif s3 == "":
                    if cards[i].fig.value < 10:
                        s3 = "0" + str(cards[i].fig.value)
                    else:
                        s3 = str(cards[i].fig.value)
                else:
                    break
        return "3"+s1+s2+s3+"0000"
    return "0"


def check_2pair(cards):
    cards.sort(key=sort_by_figure, reverse=True)
    s1 = ""
    s2 = ""
    s3 = ""
    pairs = 0
    pair1 = -1
    pair2 = -1
    for i in range(0, 6):
        if cards[i].fig.value == cards[i+1].fig.value:
            if pairs == 0:
                pair1 = cards[i].fig.value
                if cards[i].fig.value < 10:
                    s1 = "0" + str(cards[i].fig.value)
                else:
                    s1 = str(cards[i].fig.value)
            if pairs == 1:
                pair2 = cards[i].fig.value
                if cards[i].fig.value < 10:
                    s2 = "0" + str(cards[i].fig.value)
                else:
                    s2 = str(cards[i].fig.value)
            pairs += 1

    if pairs >= 2:
        for i in range(0, 7):
            if cards[i].fig.value != pair1 and cards[i].fig.value != pair2:
                if cards[i].fig.value < 10:
                    s3 = "0" + str(cards[i].fig.value)
                else:
                    s3 = str(cards[i].fig.value)
        return "2"+s1+s2+s3+"0000"
    return "0"


def check_1pair(cards):
    cards.sort(key=sort_by_figure, reverse=True)
    pairs = 0
    s1 = ""
    s2 = ""
    s3 = ""
    s4 = ""
    pair_value = -1
    for i in range(0, 6):
        if cards[i].fig.value == cards[i + 1].fig.value:
            if pairs == 0:
                pair_value = cards[i].fig.value
                if cards[i].fig.value < 10:
                    s1 = "0" + str(cards[i].fig.value)
                else:
                    s1 = str(cards[i].fig.value)
            pairs += 1

    if pairs >= 1:
        for i in range(0, 7):
            if cards[i].fig.value != pair_value:
                if s2 == "":
                    if cards[i].fig.value < 10:
                        s2 = "0" + str(cards[i].fig.value)
                    else:
                        s2 = str(cards[i].fig.value)
                elif s3 == "":
                    if cards[i].fig.value < 10:
                        s3 = "0" + str(cards[i].fig.value)
                    else:
                        s3 = str(cards[i].fig.value)
                elif s4 == "":
                    if cards[i].fig.value < 10:
                        s4 = "0" + str(cards[i].fig.value)
                    else:
                        s4 = str(cards[i].fig.value)
                else:
                    break

        return "1"+s1+s2+s3+s4+"00"
    return "0"


def check_high_card(cards):
    cards.sort(key=sort_by_figure, reverse=True)
    if cards[0].fig.value < 10:
        s1 = "0" + str(cards[0].fig.value)
    else:
        s1 = str(cards[0].fig.value)
    if cards[1].fig.value < 10:
        s2 = "0" + str(cards[1].fig.value)
    else:
        s2 = str(cards[1].fig.value)
    if cards[2].fig.value < 10:
        s3 = "0" + str(cards[2].fig.value)
    else:
        s3 = str(cards[2].fig.value)
    if cards[3].fig.value < 10:
        s4 = "0" + str(cards[3].fig.value)
    else:
        s4 = str(cards[3].fig.value)
    if cards[4].fig.value < 10:
        s5 = "0" + str(cards[4].fig.value)
    else:
        s5 = str(cards[4].fig.value)

    return s1+s2+s3+s4+s5


def evaluate_hands(hands, table): # [ ([card, card], id)
    hand_level = []
    card_combinations = []
    for hand in hands:
        card_combo = []
        for card in hand[0]:
            card_combo.append(card)
        for card in table:
            card_combo.append(card)
        card_combinations.append(card_combo)
    i = 0
    for combo in card_combinations:
        hand_power = check_poker(combo)
        if hand_power == "0":
            hand_power = check_four(combo)
        if hand_power == "0":
            hand_power = check_full(combo)
        if hand_power == "0":
            hand_power = check_flush(combo)
        if hand_power == "0":
            hand_power = check_straight(combo)
        if hand_power == "0":
            hand_power = check_three(combo)
        if hand_power == "0":
            hand_power = check_2pair(combo)
        if hand_power == "0":
            hand_power = check_1pair(combo)
        if hand_power == "0":
            hand_power = check_high_card(combo)

        hand_level.append((hand_power, hands[i][1]))
        i += 1

    hand_level.sort(key=sort_by_power, reverse=True)
    i = 1
    max = int(hand_level[0][0])
    while i < len(hand_level):
        if int(hand_level[i][0]) != max:
            hand_level.pop(i)
        else:
            i += 1

    return hand_level
