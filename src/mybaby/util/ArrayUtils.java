package mybaby.util;

import java.util.ArrayList;

public class ArrayUtils {
    
    // ArrayList<String> 转 String[]
    public static String[] arrayList2StringArray(ArrayList<String> als){
	      String[] sa=new String[als.size()];
	      als.toArray(sa);
	      return sa;
    }
    // String[] 转 ArrayList<String>
    public static ArrayList<String> stringArray2ArrayList(String[] sa){
	      ArrayList<String> als=new ArrayList<String>(0);
	      for(int i=0;i<sa.length;i++){
	        als.add(sa[i]);
	      }
	      return als;
    }
    //合并两个ArrayList
    public static <T> ArrayList<T> mergeArrayList(ArrayList<T> al1,ArrayList<T> al2){
        ArrayList<T> result=new ArrayList<T>();
        result.addAll(al1);
        result.addAll(al2);
        return result;
    }
    public static Object[] mergeArray(Object[] arr1,Object[] arr2){
    	 Object[] mergeArray=new Object[arr1.length+arr2.length];
		 System.arraycopy(arr1, 0, mergeArray, 0, arr1.length);
		 System.arraycopy(arr2, 0, mergeArray, arr1.length, arr2.length);
		 return mergeArray;
    }
}
