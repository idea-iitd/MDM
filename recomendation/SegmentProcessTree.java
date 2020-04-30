package recomendation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class SegmentProcessTree {
	SegmentProcess segmentProcess;
	int depth;
	int costIncurr;
	ArrayList<SegmentProcessTree> segmentChildList;
	// keep the object of road segment
	RoadNetwork roadNetwork;
	

	/**
	 * Constructor to initialize the data structure
	 * @param segmentProcess root road segment
	 * @param roadNetwork object of current road segment
	 */
	public SegmentProcessTree(SegmentProcess segmentProcess, RoadNetwork roadNetwork) 
	{
		// TODO Auto-generated constructor stub
		this.segmentProcess = segmentProcess;
		segmentChildList = new ArrayList<SegmentProcessTree>();
		depth = 1;
		this.roadNetwork = roadNetwork;
	}
	
	/**
	 * Constructor to initialize the data structure
	 * @param segmentProcess root road segment
	 * @param roadNetwork object of current road segment
	 */
	public SegmentProcessTree(SegmentProcess segmentProcess)
	{
		// TODO Auto-generated constructor stub
		this.segmentProcess = segmentProcess;
		segmentChildList = new ArrayList<SegmentProcessTree>();
		depth = 1;
	}
	
	public SegmentProcessTree() {
		// TODO Auto-generated constructor stub
	}
	
//	SegmentProcessTree recursionTree(SegmentProcess startSegmentProcess , int M)
//	{
//		int depth = 1;
//		SegmentProcessTree segmentProcessTree = null;
//		SegmentProcessTree rootTree = new SegmentProcessTree(startSegmentProcess);
//		Queue<SegmentProcessTree> queue = new LinkedList<SegmentProcessTree>();
//		queue.add(rootTree);
//		while(!queue.isEmpty())
//		{
//			segmentProcessTree = queue.remove();
//			depth = segmentProcessTree.depth;
////			System.out.println(segmentProcessTree.segmentProcess);
//			
//			ArrayList<SegmentProcess> segmentProcessList = null;
//			boolean bool = true;
//			ArrayList<SegmentProcess> tempProcesses = Main.segmentProcessIdMap.get(segmentProcessTree.segmentProcess.endLocation.id);
//			if(tempProcesses != null)
//			{
//				for (SegmentProcess segment : tempProcesses)
//				{
//					if(bool)
//					{
//						segmentProcessList = new ArrayList<SegmentProcess>();
//						segmentProcessList.add(segment);
//						bool = false;
//					}
//					else
//						segmentProcessList.add(segment);
//
//				}
//			}
//			else
//				segmentProcessList = null;
//			// remove the road segment which has been used to generate the next list i.e turn on the same road segment is not allowed
//			ArrayList<SegmentProcess> segmentProcessTempList = new ArrayList<SegmentProcess>();
//			SegmentProcess segmentProcessTemp = null;
//			int size = 0;
//			if(segmentProcessList == null)
//			{
//				size = 0;
//				depth++;
//				continue;
//			}
//			else
//				size = segmentProcessList.size();
//			for (int i = 0; i < size; i++) {
//				segmentProcessTemp = segmentProcessList.get(i);
//				if(segmentProcessTemp.endLocation.id == segmentProcessTree.segmentProcess.startLocation.id  && segmentProcessTemp.startLocation.id == segmentProcessTree.segmentProcess.endLocation.id)
//					segmentProcessTempList.add(segmentProcessList.get(i));
//			}
//			segmentProcessList.removeAll(segmentProcessTempList);
//			if(segmentProcessList != null)
//			{
//				for(SegmentProcess segmentProcess2: segmentProcessList)
//				{
//					SegmentProcessTree segmentProcessTree2 = new SegmentProcessTree(segmentProcess2);
//					segmentProcessTree2.depth = depth + 1;
//					segmentProcessTree.segmentChildList.add(segmentProcessTree2);
//					if(segmentProcessTree2.depth < M)
//						queue.add(segmentProcessTree2);
//				}
//			}
//			
//			depth++;
//		}
//		
//		
//		return rootTree;
//	}
	
	
	SegmentProcessTree recursionTree(SegmentProcess startSegmentProcess , int M)
	{
		SegmentProcessTree rootTree = new SegmentProcessTree(startSegmentProcess);
		Queue<SegmentProcessTree> queue = new LinkedList<SegmentProcessTree>();
		queue.add(rootTree);
		while(!queue.isEmpty())
		{
			SegmentProcessTree segmentProcessTree = null;
			segmentProcessTree = queue.remove();
			int depth = segmentProcessTree.depth;
//			System.out.println(segmentProcessTree.segmentProcess);
			
			ArrayList<SegmentProcess> segmentProcessList = new ArrayList<SegmentProcess>();
			ArrayList<SegmentProcess> tempProcesses = roadNetwork.getSegmentProcessFromStartId(segmentProcessTree.segmentProcess.endLocation.id);
			if(tempProcesses == null)
				continue;
			else
			{
				for (SegmentProcess segment : tempProcesses)
				{
					if(segment.endLocation.id != segmentProcessTree.segmentProcess.startLocation.id  || segment.startLocation.id != segmentProcessTree.segmentProcess.endLocation.id)
						segmentProcessList.add(segment);
				}
			}
			// remove the road segment which has been used to generate the next list i.e turn on the same road segment is not allowed
			if(segmentProcessList != null)
			{
				for(SegmentProcess segmentProcess: segmentProcessList)
				{
					SegmentProcessTree segmentProcessTree2 = new SegmentProcessTree(segmentProcess);
					segmentProcessTree2.depth = depth + 1;
					segmentProcessTree.segmentChildList.add(segmentProcessTree2);
					if(segmentProcessTree2.depth < M)
						queue.add(segmentProcessTree2);
				}
			}
			
		}
		return rootTree;
	}
	
	
	static void printFun(SegmentProcessTree rootNode, int timeSlot)
	{
		if(rootNode == null)
			return;
		Queue<SegmentProcessTree> queue = new LinkedList<SegmentProcessTree>();
		LinkedList<Integer> queuechild = new LinkedList<Integer>();
		queue.add(rootNode);
		int depth =1 ;
		System.out.printf("Depth %d ==========================\n",depth++);
		queuechild.add(1);
		while(!queue.isEmpty())
		{
			SegmentProcessTree segmentProcessTree = queue.remove();
			segmentProcessTree.segmentProcess.segmentProcessPrint(timeSlot);
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
	
	public void path(ArrayList<ArrayList<SegmentProcess>> rootToLeaves, ArrayList<SegmentProcess> arr)
	{
		ArrayList<SegmentProcess> temp = new ArrayList<SegmentProcess>();
		for (SegmentProcess segmentProcess : arr) {
			temp.add(segmentProcess);
		}
		rootToLeaves.add(temp);
	}
	
	public void rootToLeavesFunction(ArrayList<ArrayList<SegmentProcess>> rootToLeaves, SegmentProcessTree rootNode, ArrayList<SegmentProcess> arr, int len)
	{
		
		if(rootNode.segmentChildList.isEmpty())
			path(rootToLeaves, arr);
		else {
			ArrayList<SegmentProcessTree> child = rootNode.segmentChildList;
			for (SegmentProcessTree temp : child) {
				int size = arr.size();
				for (int i = size - 1; i >= len; i--) {
					arr.remove(i);
				}
				arr.add(temp.segmentProcess);
				
				rootToLeavesFunction(rootToLeaves, temp, arr, len+1);
			}
		}
			
	}	
	
	

}
