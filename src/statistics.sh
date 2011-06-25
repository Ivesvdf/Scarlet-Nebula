#! /usr/bin/env bash

PREV_TOTAL=0
PREV_IDLE=0
IT_COUNT=0

# CPU calculation is inspired by http://colby.id.au/node/39
while true; do
  CPU=(`cat /proc/stat | grep '^cpu '`) 
  unset CPU[0]                         
  IDLE=${CPU[4]}                      

  # Calculate the total CPU time.
  TOTAL=0
  for VALUE in "${CPU[@]}"; do
    let "TOTAL=$TOTAL+$VALUE"
  done

  # Calculate the CPU usage since we last checked.
  let "DIFF_IDLE=$IDLE-$PREV_IDLE"
  let "DIFF_TOTAL=$TOTAL-$PREV_TOTAL"
  let "DIFF_USAGE=(1000*($DIFF_TOTAL-$DIFF_IDLE)/$DIFF_TOTAL+5)/10"
  #echo -en "\rCPU: $DIFF_USAGE%  \b\b"

  if (( $DIFF_USAGE == 100 )); then
	  PRINT_DIFF_USAGE=1.0
  elif (( $DIFF_USAGE < 10 )); then
	  PRINT_DIFF_USAGE=0.0$DIFF_USAGE
  else
	  PRINT_DIFF_USAGE=0.$DIFF_USAGE
  fi

  echo "{\"datapointType\":\"RELATIVE\",\"datastream\":\"CPU\",\"value\":$PRINT_DIFF_USAGE,\"lowWarnLevel\":0.5,\"mediumWarnLevel\":0.85,\"highWarnLevel\":0.95}"

  # Remember the total and idle CPU times for the next check.
  PREV_TOTAL="$TOTAL"
  PREV_IDLE="$IDLE"

  # Memory usage
  if (( $IT_COUNT % 10 == 0 || $IT_COUNT == 1 )); then
	  echo "{\"datapointType\":\"ABSOLUTE\",\"datastream\":\"MEM\",\"value\":$(free -mto | grep Mem: | awk '{ print $3 }'),\"max\":$(free -mto | grep Mem: | awk '{ print $2 }')}"
  fi

  # Open connections
  echo "{\"datapointType\":\"ABSOLUTE\",\"datastream\":\"Open connections\",\"value\":$(netstat --protocol=inet -n | grep tcp | wc -l)}"

  # Number of processes
  echo "{\"datapointType\":\"ABSOLUTE\",\"datastream\":\"Nr of processes\",\"value\":$(ps ax | wc -l | tr -d " ")}"

  
  # Wait before checking again.
  sleep 5
  let "IT_COUNT=IT_COUNT+1"
done
