package TaxiRecommendation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class SegmentProcessTree {
	SegmentProcess segmentProcess;
	int depth;
	int costIncurr;
	ArrayList<SegmentProcessTree> segmentChildList;
	double segmentLength;
	int noOfTimesVisited;
	/**
	 * timeSlot is the number of slot in a day 
	 */
	public static int noOfTimeSlot = recomendation.Main.noOfTimeSlot;
	
	

	public SegmentProcessTree(SegmentProcess segmentProcess) {
		// TODO Auto-generated constructor stub
		this.segmentProcess = segmentProcess;
		segmentChildList = new ArrayList<SegmentProcessTree>();
		depth = 1;
		this.segmentLength=this.segmentProcess.segmentLength;
		
	}
	
	public SegmentProcessTree() {
		// TODO Auto-generated constructor stub
	}
	
	SegmentProcessTree recursionTree(SegmentProcess startSegmentProcess,int M, Map<Long, ArrayList<SegmentProcess>> segmentProcessIdMap)
	{
		int depth = 1;
		//double startSegmentProcessLength=startSegmentProcess.segmentLength;
		double length=0.0;
		SegmentProcessTree segmentProcessTree = null;
		SegmentProcessTree rootTree = new SegmentProcessTree(startSegmentProcess);
		Queue<SegmentProcessTree> queue = new LinkedList<SegmentProcessTree>();
		queue.add(rootTree);
		while(!queue.isEmpty())
		{
			segmentProcessTree = queue.remove();
			depth = segmentProcessTree.depth;
			length=segmentProcessTree.segmentLength;
//			System.out.println(segmentProcessTree.segmentProcess);
			ArrayList<SegmentProcess> segmentProcessList = segmentProcessIdMap.get(segmentProcessTree.segmentProcess.endLocation.id);
			// remove the road segment which has been used to generate the next list i.e turn on the same road segment is not allowed
			ArrayList<SegmentProcess> segmentProcessTempList = new ArrayList<SegmentProcess>();
			SegmentProcess segmentProcessTemp = null;
			int size = 0;
			if(segmentProcessList == null)
			{
				size = 0;
				depth++;
				continue;
			}
			else
				size = segmentProcessList.size();
			for (int i = 0; i < size; i++) {
				segmentProcessTemp = segmentProcessList.get(i);
				if(segmentProcessTemp.endLocation.id == segmentProcessTree.segmentProcess.startLocation.id  && segmentProcessTemp.startLocation.id == segmentProcessTree.segmentProcess.endLocation.id)
					segmentProcessTempList.add(segmentProcessList.get(i));
//					break;
			}
			segmentProcessList.removeAll(segmentProcessTempList);
			if(segmentProcessList != null)
			{
				for(SegmentProcess segmentProcess2: segmentProcessList)
				{
					SegmentProcessTree segmentProcessTree2 = new SegmentProcessTree(segmentProcess2);
					segmentProcessTree2.depth = depth + 1;
					segmentProcessTree2.segmentLength=segmentProcess2.segmentLength+length;
					segmentProcessTree.segmentChildList.add(segmentProcessTree2);
					//if(segmentProcessTree2.segmentLength-startSegmentProcessLength< distanceThreshold)
					if(segmentProcessTree2.depth<M)
						queue.add(segmentProcessTree2);
				}
			}
			
			depth++;
		}
		
		
		return rootTree;
	}
	
	static void printFun(SegmentProcessTree rootNode)
	{
		Queue<SegmentProcessTree> queue = new LinkedList<SegmentProcessTree>();
		LinkedList<Integer> queuechild = new LinkedList<Integer>();
		queue.add(rootNode);
		int depth =1 ;
		System.out.printf("Depth %d ==========================\n",depth++);
		queuechild.add(1);
		while(!queue.isEmpty())
		{
			SegmentProcessTree segmentProcessTree = queue.remove();
			segmentProcessTree.segmentProcess.segmentProcessPrint(recomendation.Main.timeSlot);
			int child = 0;
			for(SegmentProcessTree segmentProcessTree2: segmentProcessTree.segmentChildList)
			{
				queue.add(segmentProcessTree2);
				child++;
			}
			if(queuechild.isEmpty() || queuechild.size() == 1)
				queuechild.add(child);
			else {
				child +=queuechild.removeLast();
				queuechild.addLast(child);
			}
			
			if(queuechild.peekFirst() == 1)
			{
				System.out.printf("Depth %d ==========================\n",depth++);
				queuechild.removeFirst();
			}
			else {

				int val = queuechild.removeFirst();
				--val;
				queuechild.addFirst(val);
			}
			
		}
	}
	
	public static void path(ArrayList<ArrayList<SegmentProcess>> rootToLeaves, ArrayList<SegmentProcess> arr)
	{
		ArrayList<SegmentProcess> temp = new ArrayList<SegmentProcess>();
		temp.addAll(arr);
		rootToLeaves.add(temp);
	}
	
	static void topK(ArrayList<ArrayList<SegmentProcess>> rootToLeaves, SegmentProcessTree rootNode, ArrayList<SegmentProcess> arr, int len)
	{
		
		if(rootNode.segmentChildList.isEmpty())
			path(rootToLeaves, arr);
		else {
			ArrayList<SegmentProcessTree> child = rootNode.segmentChildList;
			for (SegmentProcessTree temp : child) {
				int size = arr.size();
				for (int i = size -1; i >= len; i--) {
					arr.remove(i);
				}
				arr.add(temp.segmentProcess);
				
				topK(rootToLeaves, temp, arr, len+1);
			}
		}
			
	}	
	
	

}
