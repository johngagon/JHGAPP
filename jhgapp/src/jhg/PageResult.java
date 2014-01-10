package jhg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Paged Result collection class.
 * 
 * @author John H. Gagon
 * @version 1.0.0
 *
 * @param <T> Any type that needs to be paged.
 */
public final class PageResult<T> implements java.io.Serializable {

	/* NOTE
	 * Interface, Abstraction, Binding, Structure, Access, Ordering 
	 * 
	 * Interface
	 * Main
	 *  Abstract
	 *  Concrete
	 *  Final 
	 * 	  Static
	 *    Instance
	 * 		Class
	 *  	Field
	 *  	Constructor
	 *  	Method
	 *  		Public
	 *  		Protected
	 *  		Package
	 *  		Private
	 *            Alpha
	 * Spacing: one between sections and non-field members
	 */
	
	private static final long serialVersionUID = 10000L;//NOTE 01.00.00
	
	private List<T> keys;
	private List<T> page;
	private int totalCount;
	private int pageSize;
	private int noPages;
	private int currPage;
	private int from;
	private int to;
	
	/**
	 * Construct paged results from a list.
	 * 
	 * @param _keys the objects to hold in the paged list.
	 * @param _pageSize the number of objects per page to hold
	 */
	public PageResult(List<T> _keys, int _pageSize){
		if(_pageSize < 2)throw new IllegalArgumentException("page size must be greater than 1");
		pageSize = _pageSize;
		init(_keys);
	}

	/**
	 * Sets the current page to the first page.
	 */
	public void first(){
		setCurrPage(1);
	}
	
	/**
	 * Set the current page to the page number one more than it's current number.
	 */
	public void next(){
		if(currPage < noPages){
			setCurrPage( currPage + 1);
		}
	}
	
	/**
	 * Set the current page immediately to the given number.  If the page number is 
	 * not in range, it does nothing.
	 * 
	 * @param pageNo  the page number to set the current page to.
	 */
	public void jump(int pageNo){
		if(pageNo >=1 && pageNo <=noPages){
			setCurrPage( pageNo );
		}
	}
	
	/**
	 * Set the current page to the page number one less than it's current number.
	 */
	public void prev(){
		if(currPage > 1){
			setCurrPage(currPage -1);
		}
	}
	
	/**
	 * Set the current page to the last page.
	 */
	public void last(){
		setCurrPage(noPages);
	}
	
	/**
	 * Get's the current page  number this page result is set to.
	 * 
	 * @return the page number as an int.
	 */
	public int currPage(){
		return currPage;
	}
	
	/**
	 * Get the total count of the object collection or size.
	 * 
	 * @return the count of entries as an int.
	 */
	public int totalCount(){
		return totalCount;
	}
	
	/**
	 * Gets the page size or number of objects set for a single page.
	 * 
	 * @return the quantity of entries on a page as an int.
	 */
	public int pageSize(){
		return pageSize;
	}
	
	/**
	 * Gets the count of pages determined by the total number and pagesize.
	 * 
	 * @return the count of the pages.
	 */
	public int pageCount(){
		return noPages;
	}
	
	/**
	 * Gets the current start index of the current page.
	 * 
	 * @return the start index of the current page as an int.
	 */
	public int from(){
		return from;
	}
	
	/**
	 * Gets the current end index of the current page.
	 * 
	 * @return the end index of current page as an int.
	 */
	public int to(){
		return to;
	}
	
	/**
	 * Get the subset of objects for the page number that is currently set.
	 * 
	 * @return a collection of objects as a List.
	 */
	public List<T> getPage(){
		return page;
	}
	
	/**
	 * Refreshes the page result with a new set of objects.  Normally, the objects 
	 * are not modifiable. So this works around that ensuring proper re-initialization. 
	 * The size of the page is not changed.
	 *  
	 * @param _keys sets the list.
	 */
	public void refresh(List<T> _keys){
		init(_keys);
	}
	
	private void init(List<T> _keys){
		keys = (List<T>)Collections.unmodifiableCollection(_keys);          
		//NOTE  These don't have to be sorted but are indexed.  Sorting, if desired
		//should take place before objects are passed in the constructor.
		totalCount = keys.size();
		noPages = pageCount(pageSize,totalCount);
		page = new ArrayList<T>(pageSize);
		setCurrPage(1);		
	}
	
	private void setCurrPage(int _currPage){
		currPage = _currPage;
		from = from(pageSize,currPage);
		to = to(pageSize,totalCount,currPage);
		page.clear();
		for(int i=(from-1); i<to; i++){
			page.add(keys.get(i));
		}
	}	
	
	private static final int from(int pagesize, int pageNo){
		int fromRecord = ((pageNo-1) * pagesize) +1;//EXAMPLE e.g.: pagesize 20, pageno 1, =1 
		return fromRecord; 						   	//EXAMPLE pagesize 20, pageno 2, =21
	}
	
	private static final int   to(int pagesize, int total, int pageNo){
		int noPages = pageCount(pagesize,total);
		int toRecord = 0;
		if(pageNo==noPages && total<(pagesize*noPages)){
			toRecord = total;					    //EXAMPLE if the page is 6/6 pages, total is 101, size is 20. (6*20=120). 101<120
		}else{
           toRecord = pagesize * pageNo;            //EXAMPLE 100,20,5-> pageNo  //pageno 1 = 20, pageno 2 = 40.			
		}
		return toRecord;
	}
	
	private static final int pageCount(int pagesize,int total){
		int result = 0;
		if(pagesize==0){
			result = 0;
		}else{
			result = total/pagesize;
			int modulus = total%pagesize;
			if(modulus>0){
				result = result+1;
			}			
		}
		return result;
	}
	
	/*
	 * MAIN  Main entry point for testing in IDE.
	 */
	public static void main(String[] args){
		testFromTo();
	}
	
	private static void testFromTo(){
		int S = 30; //NOTE page size
		int T = 102;//NOTE total count
		int P = 3;  //NOTE page number
		p(P+": 61-90 : "+from(S,P)+"-"+to(S,T,P));
		/*
		OFF
		p("pageCount(20,1)=1:"+pageCount(20,1));
		p("pageCount(20,19)=1:"+pageCount(20,19));
		p("pageCount(20,20)=1:"+pageCount(20,20));
		p("pageCount(20,21)=2:"+pageCount(20,21));
		p("pageCount(20,80)=4:"+pageCount(20,80));
		p("pageCount(20,99)=5:"+pageCount(20,99));
		p("pageCount(20,100)=5:"+pageCount(20,100));
		p("pageCount(20,101)=6:"+pageCount(20,101));	
		*/	
	}
	
	@SuppressWarnings("unused")
	private static void test(){
		//OFF
		//                                   Expected
		//1 page size is 20, total is 0            0
		//2 page size is 20, total is 1            1
		//3 page size is 20, total is 19           1
		//4 page size is 20, total is 20           1
		//5 page size is 20, total is 21           2
		//6 page size is 20, total is 80           4  even multiple
		//7 page size is 20, total is 99           5 
		//8 page size is 20, total is 100          5  even multiple
		//9 page size is 20, total is 101          6
		p("pageCount(20,0)=0:"+pageCount(20,0));
		p("pageCount(20,1)=1:"+pageCount(20,1));
		p("pageCount(20,19)=1:"+pageCount(20,19));
		p("pageCount(20,20)=1:"+pageCount(20,20));
		p("pageCount(20,21)=2:"+pageCount(20,21));
		p("pageCount(20,80)=4:"+pageCount(20,80));
		p("pageCount(20,99)=5:"+pageCount(20,99));
		p("pageCount(20,100)=5:"+pageCount(20,100));
		p("pageCount(20,101)=6:"+pageCount(20,101));
	}
	
	private static void p(String x){
		System.out.println(x);
	}
	
}
