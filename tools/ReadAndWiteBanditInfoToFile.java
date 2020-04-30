package tools;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import recomendation.SegmentProcess;
 
public class ReadAndWiteBanditInfoToFile  {
	
	private ArrayList<ArrayList<SegmentProcess>> routes;
	
	//private ArrayList<Double>   binfo = new ArrayList<Double>();
	
	private static String fileName = null;
	
	public ReadAndWiteBanditInfoToFile ( ArrayList<ArrayList<SegmentProcess>> routes,  String fileName )
	{
		this.routes = routes;
		this.fileName = fileName;
		
	}
	
	
	public void banditInfoWriter(ArrayList<ArrayList<SegmentProcess>> routes, Double[] banditInfo)
    {
 	   
 	   try 
 	   {
 		   File file = new File(fileName);
 		   File fileTemp = new File("FileTemp.txt");
 		   BufferedWriter out = new BufferedWriter(new FileWriter(fileTemp));
 		   BufferedReader in = null;
 		   boolean fileFirstTimeWritten = false;
 		   if(file.exists())
		   {
 		       in = new BufferedReader(new FileReader(file));
 		       String string;
// 		       boolean write = false;
 		       while ((string = in.readLine()) != null) 
 		       {
 		    	   if(!string.contains(";"))
 		    	   {
 		    		   String[] idString = string.split(",");
 		    		   SegmentProcess startSegment = routes.get(0).get(0);
 		    		   if(Long.parseLong(idString[0]) == startSegment.startLocation.id && Long.parseLong(idString[1]) == startSegment.endLocation.id)
 		    		   {
 		    			   out.write(string+"\n");
 		    			   for(int i=0; i<routes.size(); i++)
 		    			   {
 		    				   in.readLine();  // to remove the lines from existing file to be replaced by new one
 		    				   ArrayList<SegmentProcess> segmentProcessList = routes.get(i);
 		    				   StringBuffer strbuffer = new StringBuffer();
 		    				   for (SegmentProcess segmentProcess : segmentProcessList) 
 		    				   {
 		    					   strbuffer.append(segmentProcess.startLocation.id);
 		    					   strbuffer.append(",");
 		    					   strbuffer.append(segmentProcess.endLocation.id);
 		    					   strbuffer.append(",");

 		    				   }
 		    				   strbuffer.deleteCharAt(strbuffer.lastIndexOf(","));
 		    				   strbuffer.append(";");
 		    				   strbuffer.append(Double.valueOf(banditInfo[i]));
 		    				   strbuffer.append("\n");
 		    				   out.write(strbuffer.toString());
 		    			   }
 		    			   fileFirstTimeWritten = true;
 		    		   }
 		    		   else
 		    			   out.write(string+"\n");
 		    	   }
 		    	   else
 		    		   out.write(string+"\n");


 		       }
 		       in.close();
 		   }
 		   
 		   if(fileFirstTimeWritten == false) 
 		   {
 			   //if(write == false)
 			   {
 				   SegmentProcess startSegment = routes.get(0).get(0);
 				   out.write(startSegment.startLocation.id+","+startSegment.endLocation.id+"\n");
 				   for(int i=0; i<routes.size(); i++)
 				   {
 					   ArrayList<SegmentProcess> segmentProcessList = routes.get(i);
 					   StringBuffer strbuffer = new StringBuffer();
 					   for (SegmentProcess segmentProcess : segmentProcessList) 
 					   {
 						   strbuffer.append(segmentProcess.startLocation.id);
 						   strbuffer.append(",");
 						   strbuffer.append(segmentProcess.endLocation.id);
 						   strbuffer.append(",");

 					   }
 					   strbuffer.deleteCharAt(strbuffer.lastIndexOf(","));
 					   strbuffer.append(";");
 					   strbuffer.append(Double.valueOf(banditInfo[i]));
 					   strbuffer.append("\n");
 					   out.write(strbuffer.toString());
 				   }
 			   }

 		   }
 		  
 		   out.close();
 		   file.delete();
 		   fileTemp.renameTo(file);
		
 	   } catch (IOException e) 
 	   {
			// TODO Auto-generated catch block
			e.printStackTrace();
 	   }
    }
    
    public  Double[] banditInfoReader(ArrayList<ArrayList<SegmentProcess>> routes)
    {
 	   
 	   try 
 	   {
 		   
 		   File file = new File(fileName);
 		   if(file.exists())
 		   {
 			   BufferedReader in = new BufferedReader(new FileReader(file));
 			   String string; 
 			   ArrayList<ArrayList<Long>> routeStartId = new ArrayList<ArrayList<Long>>();
 			   ArrayList<ArrayList<Long>> routeEndId = new ArrayList<ArrayList<Long>>();
 			   ArrayList<Double> banditInfoList = new ArrayList<Double>();
 			   while((string = in.readLine()) != null)
 			   {
 				   if(!string.contains(";"))
 				   {
 					   String[] idString = string.split(",");
 					   SegmentProcess startSegment = routes.get(0).get(0);
 					   if(Long.parseLong(idString[0]) == startSegment.startLocation.id && Long.parseLong(idString[1]) == startSegment.endLocation.id)
 					   {
 						   int size = 0;
 						   while(size < routes.size())
 						   {
 							   string = in.readLine();
 							   ArrayList<Long> startIdList = new ArrayList<Long>();
 		    				   ArrayList<Long> endIdList = new ArrayList<Long>();
 		    				   String[] str = string.split(";");
 		    				   String[] subString = str[0].split(",");
 		    				   for (int i = 0; i < subString.length; i=i+2) 
 		    				   {
 		    					   startIdList.add(Long.parseLong(subString[i]));
 		    					   endIdList.add(Long.parseLong(subString[i+1]));
 		    				   }
 		    				   banditInfoList.add(Double.parseDouble(str[1]));
 		    				   routeStartId.add(startIdList);
 		    				   routeEndId.add(endIdList);
 		    				   size++;
 		    			   }
 		    			   Double[] banditInfoTemp = new Double[banditInfoList.size()];
 		    			   if(routeStartId.size() == routes.size())
 		    			   {
 		    				   int  counter = 0;
 		    				   for(int i=0; i<routes.size(); i++)
 		        			   {
 		    					   boolean boolInner = true;
 		        				   ArrayList<SegmentProcess> segmentProcessList = routes.get(i);
 		        				   for (int j = 0; j < routeStartId.size(); j++) 
 		        				   {
 		        					   boolInner = true;
 		        					   ArrayList<Long> startIdList = routeStartId.get(j);
 		            				   ArrayList<Long> endIdList = routeEndId.get(j);
 		            				   if(segmentProcessList.size() == startIdList.size())
 		            				   {
 		            					   
 		            					   for (int k = 0; k < startIdList.size(); k++)
 		            					   {
 		            						   SegmentProcess segmentProcess = segmentProcessList.get(k);
 		            						   if(segmentProcess.startLocation.id != startIdList.get(k) || segmentProcess.endLocation.id != endIdList.get(k))
 		            						   {
 		            							   boolInner = false;
 		            							   break;
 		            						   }
 		            					   }
 		            					   if(boolInner == true)
 		            					   {
 		            						   banditInfoTemp[i] = banditInfoList.get(j);
 		            						   counter++;
 		            						   break;
 		            					   }
 		            				   }
 		            				   
 		        				   }
 		        				   if(boolInner == false)  // if we have not got match for ith route
 		        				   {
 		        					   
 		        					   in.close();
 		        					   return null;
 		        				   }
 		        				  
 		        			   }
 		    				   if(counter == routes.size())
 		    				   {
 		    					   in.close();
 		    					   return banditInfoTemp;
 		    				   }

 		    			   }
 		    			       		    			   
 						   }
 					  
 					   }
  				   }
 			  
 					   
 				   
 			   
 			   in.close();
 			   return null;
 		   }
 		   
		
 	   } catch (IOException e) 
 	   {
			// TODO Auto-generated catch block
			e.printStackTrace();
 	   }
 	   return null;
    }
    
    public static ArrayList<Pair> read1000RandomSegments(String fileName)
    {
    	ArrayList<Pair> pair = new ArrayList<Pair>();
    	File file = new File(fileName);
    	try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String string;
			while((string = in.readLine()) != null)
			{
				String[] subString = string.split(",");
				long start = Long.parseLong(subString[0]);
				long end = Long.parseLong(subString[1]);
				pair.add(new Pair(start, end));
				
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return pair;
    }
    
    public static void writeMeanToFile(double[][] accumilatedMeans, String fileName)
    {
    	File file = new File(fileName);
    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < accumilatedMeans.length; i++) 
			{
				StringBuffer strBuffer = new StringBuffer();

				for (int j = 0; j < accumilatedMeans[i].length; j++) 
				{
					strBuffer.append(accumilatedMeans[i][j]);
					strBuffer.append(",");
				}
				strBuffer.deleteCharAt(strBuffer.length()-1);
				strBuffer.append("\n");
				
				out.write(strBuffer.toString());
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public static ArrayList<ArrayList<SegmentProcess>> readClusteredFile(String fileName, long startId, long endId)
    {
    	ArrayList<ArrayList<SegmentProcess>> bestRoutes = null;
    	
    	File file = new File(fileName);
    	try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String string;
			while ((string = in.readLine()) != null) {
				if(string.contains(":"))
				{
					String[] subString = string.split(":");
					if(startId == Long.parseLong(subString[0]) && endId == Long.parseLong(subString[1]))
					{
						bestRoutes = new ArrayList<ArrayList<SegmentProcess>>();
						while((string = in.readLine()) != null && !string.contains(":"))
						{
							subString = string.split(";");
							ArrayList<SegmentProcess> route = new ArrayList<SegmentProcess>();
							for (int i = 0; i < subString.length; i++) {
								String[] subsubString = subString[i].split(",");
								long start = Long.parseLong(subsubString[0]);
								long end = Long.parseLong(subsubString[1]);
								ArrayList<SegmentProcess> arrList = recomendation.RoadNetwork.segmentProcessIdMap.get(start);
								for (SegmentProcess segmentProcess : arrList) {
									if(end == segmentProcess.endLocation.id)
									{
										route.add(segmentProcess);
										break;
									}
								}
							}
							bestRoutes.add(route);
						}
					}
				}
			}
			
			in.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return bestRoutes;
    	
    }
    
    public static void writeClusteredFile(String fileName, ArrayList<ArrayList<SegmentProcess>> bestRoutes, boolean appendTofile)
    {
    	File file = new File(fileName);
    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, appendTofile));
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append(bestRoutes.get(0).get(0).startLocation.id);
			strBuffer.append(":");
			strBuffer.append(bestRoutes.get(0).get(0).endLocation.id);
			strBuffer.append("\n");
			out.write(strBuffer.toString());
			for (int i = 0; i < bestRoutes.size(); i++) {
				ArrayList<SegmentProcess> route = bestRoutes.get(i);
				strBuffer = new StringBuffer();
				for (SegmentProcess segmentProcess : route) {
					strBuffer.append(segmentProcess.startLocation.id);
					strBuffer.append(",");
					strBuffer.append(segmentProcess.endLocation.id);
					strBuffer.append(";");
				}
				strBuffer.deleteCharAt(strBuffer.length() - 1);
				strBuffer.append("\n");
				out.write(strBuffer.toString());
			}
			out.close();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
 

}