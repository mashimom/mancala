#!/bin/bash

# requires httpie installed
MY_ID=$(http -bj POST ':8080/api/game/?player1=kirk&player2=spock' | jq -r '.id')
echo $MY_ID

#1
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=0"
#2
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=3"
#3
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=4"
#4
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=5"
#5
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=0"
#6
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=1"
#7
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=4"
#8
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=5"
#9
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=3"
#10
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=0"
#11
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=1"
#12
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=1"
#13
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=5"
#14
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=4"
#15
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=4"
#16
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=5"
#17
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=4"
#18
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=2"
#19
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=5"
#20
http POST ":8080/api/game/$MY_ID/move?player=TWO&position=4"
#21
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=3"
#22
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=4"
#23
http POST ":8080/api/game/$MY_ID/move?player=ONE&position=5"

#ENDED
http ":8080/api/game/$MY_ID/"
