package recomendation;

import java.util.ArrayList;


public class PriorityQueue
	{
	private ArrayList<PriorityQueueElement> queue;
	
	public PriorityQueue()
		{
		this.queue = new ArrayList<PriorityQueueElement>();
		}
	
	public boolean isEmpty()
		{
		return this.queue.isEmpty();
		}
	
	public double getValue(int index)
		{
		return this.queue.get(index).distanceFromSource;
		}
		
	public void insert(PriorityQueueElement priorityQueueElement)
		{
		//check if the element already exists, if it does, remove
		int index = this.indexOf(priorityQueueElement.segment);
		if(index != -1)
			this.remove(index);
//		System.out.println("Adding " + priorityQueueElement.name + " " + priorityQueueElement.distanceFromSource + ":");
		this.queue.add(priorityQueueElement);
		for(int i = this.queue.size() - 2 ; i >= 0 ; --i)
			{
			PriorityQueueElement element = this.queue.get(i);
			if(element.distanceFromSource <= priorityQueueElement.distanceFromSource)
				break;
			//exchange
			this.queue.set(i + 1, element);
			this.queue.set(i, priorityQueueElement);
			}
		}
	
	public PriorityQueueElement remove(int index)
		{
//		System.out.println("Removing " + this.queue.get(index).name + " " + this.queue.get(index).distanceFromSource + ":");
		return this.queue.remove(index);
		}
	
	public int indexOf(SegmentProcess segment)
		{
		for(int i = this.queue.size() - 1 ; i >= 0 ; --i)
			if(this.queue.get(i).segment == segment)
				return i;
		
		return -1;
		}
	
	public void print()
		{
		if(this.queue.isEmpty())
			System.out.println("queue empty");
		else
			{
			for(int i = 0 ; i < this.queue.size() ; ++i)
				System.out.print("\t" + this.queue.get(i).distanceFromSource);
			System.out.println();
			}
		}
	}