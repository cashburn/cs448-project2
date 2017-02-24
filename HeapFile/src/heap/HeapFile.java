package heap; 

import java.util.*;
import global.* ;
import chainexception.ChainException;
// import chainexception.ChainException;

public class HeapFile{
	public HeapFile(String name){
	
	}

	public RID insertRecord(byte[] record)throws ChainException{return null;}

	public Tuple getRecord(RID rid){return null;}

	public boolean updateRecord(RID rid, Tuple newRecord) throws ChainException{return false;}

	public boolean deleteRecord(RID rid){return false;}

	public int getRecCnt(){return 0;}

	public HeapScan openScan(){return null;}
}