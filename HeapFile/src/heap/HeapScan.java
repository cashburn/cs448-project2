package heap;
import java.util.*;
import global.* ;
import chainexception.ChainException;
public class HeapScan{
	private HeapFile heap;

	protected HeapScan(HeapFile hf){
		heap = hf;
		
	}

	protected void finalize() throws Throwable{}

	public void close()throws ChainException{}

	public boolean hasNext(){return false;}

	public Tuple getNext(RID rid){return null;}
}