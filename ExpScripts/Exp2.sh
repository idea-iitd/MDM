#%list of parameters
#1. %dataCategory - {"Normal" "Top"}{String}
#2. %testDataType - {"topxneighbours"}{String}
#3. %dataType - {"1,10,15,25,50"}{String..has to be converted to list in code}
#4. %distance - {1000,2000,3000,5000}{String..has to be converted to list in code}
#5. %noOfTimesRecommendation - {1,5,10,15,20,25,30} {String}
#6. %phase - {"Training","Testing"}
#7. %costPerMetre - {0.125,0.25,0.5,1.0,2.0,5.0,10.0}{String}
#8. %testingCost - {0.125,0.25,0.5,1.0,2.0,5.0,10.0}{String}
#9. %experimentsType - {"UCT_TESTING","UCT_TRAINING","BASELINE"}{String}
#10. %timeslot - {0.1,2,3}{String}
#11. %testDataPercentage - {80}

#1
javac driver/Centralized.java
#trajectories="50 100 200 300 400 500"
java driver/Centralized "Top" "AllSegments" "10" 3000 1 0.25 5.0 "UCT_TRAINING" 3 80 300 1
noOfTimesRecommendation="1 5 10 15 20 25 30"
for i in $noOfTimesRecommendation; do
	echo $i	
	java driver/Centralized "Top" "AllSegments" "10" 3000 $i 0.25 5.0 "UCT_TESTING" 3 80 300 1
done

# java driver/Centralized "Normal" "AllSegments" "10" 3000 10 0.25 5.0 "UCT_TRAINING" 3 80 300
# java driver/Centralized "Top" "AllSegments" "10" 3000 10 0.25 5.0 "UCT_TRAINING" 3 80 300
# java driver/Centralized "Normal" "AllSegments" "10" 3000 10 0.25 5.0 "UCT_TESTING" 3 80 300
# java driver/Centralized "Top" "AllSegments" "10" 3000 10 0.25 5.0 "UCT_TESTING" 3 80 300