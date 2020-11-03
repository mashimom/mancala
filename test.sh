#!/bin/bash

# requires httpie and jq installed

# creating users
P1=$(http -bj POST ':8080/api/user/?screenName=kirk' | jq -r '.pid')
P2=$(http -bj POST ':8080/api/user/?screenName=bones' | jq -r '.pid')

# creating a game
http -b POST ":8080/api/player/$P1/wait-room"
http -b POST ":8080/api/player/$P2/wait-room"

MY_ID=$(http -bj ':8080/api/game/' | jq -r '.[0].pid')
echo $MY_ID


#1
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=0"
#2
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=3"
#3
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=4"
#4
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=5"
#5
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=0"
#6
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=1"
#7
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=4"
#8
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=5"
#9
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=3"
#10
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=0"
#11
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=1"
#12
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=1"
#13
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=5"
#14
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=4"
#15
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=4"
#16
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=5"
#17
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=4"
#18
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=2"
#19
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=5"
#20
http POST ":8080/api/game/$MY_ID/move?playerRole=TWO&position=4"
#21
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=3"
#22
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=4"
#23
http POST ":8080/api/game/$MY_ID/move?playerRole=ONE&position=5"

#ENDED
http -j ":8080/api/game/$MY_ID/"

echo "In the final call the game must have ended with a victory of Bones on 42x22"
