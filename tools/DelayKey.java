package tools;

public class DelayKey{

	private Integer action;
	
	private Integer timeStep;
	
	public DelayKey(Integer action, Integer timeStep) {
		// TODO Auto-generated constructor stub
		this.action = action;
		this.timeStep = timeStep;
	}
	
	public Integer getAction()
	{
		return action;
	}
	
	public Integer getTimeStep()
	{
		return timeStep;
	}
	
	
	
	public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        // object must be Test at this point
        DelayKey test = (DelayKey)obj;
        return this.action.intValue() == test.action.intValue() &&  this.timeStep.intValue() == test.timeStep.intValue();
    }

    public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + action.hashCode();
        hash = 31 * hash + (null == timeStep ? 0 : timeStep.hashCode());
        return hash;
    }

	
}
