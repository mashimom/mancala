#!/bin/bash

# requires httpie and jq installed

# creating users
P1=$(http --verify=no -bj POST  'https://localhost/mancala/api/user/?screenName=kirk' | jq -r '.pid')
P2=$(http --verify=no -bj POST 'https://localhost/mancala/api/user/?screenName=bones' | jq -r '.pid')

# creating a game
http --verify=no -b POST "https://localhost/mancala/api/player/$P1/wait-room"
http --verify=no -b POST "https://localhost/mancala/api/player/$P2/wait-room"

MY_ID=$(http -bj --verify=no 'https://localhost/mancala/api/game/' | jq -r '.[0].pid')
echo $MY_ID


#1
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=0"
#2
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=3"
#3
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=4"
#4
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=5"
#5
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=0"
#6
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=1"
#7
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=4"
#8
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=5"
#9
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=3"
#10
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=0"
#11
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=1"
#12
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=1"
#13
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=5"
#14
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=4"
#15
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=4"
#16
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=5"
#17
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=4"
#18
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=2"
#19
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=5"
#20
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=TWO&position=4"
#21
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=3"
#22
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=4"
#23
http --verify=no -b POST "https://localhost/mancala/api/game/$MY_ID/move?playerRole=ONE&position=5"

#ENDED
http -j --verify=no "https://localhost/mancala/api/game/$MY_ID/"

echo "In the final call the game must have ended with a victory of Bones on 42x22"

http --verify=no -b POST "https://localhost/mancala/api/player/$P1/wait-room"
http --verify=no -b POST "https://localhost/mancala/api/player/$P2/wait-room"

echo "new game should exist"

NEW_ID=$(http -bj --verify=no 'https://localhost/mancala/api/game/' | jq -r '.[0].pid')
echo "$NEW_ID"

REMATCH=$(http -bj --verify=no "https://localhost/mancala/api/game/$MY_ID/" | jq -r '.pid')
echo "$REMATCH"