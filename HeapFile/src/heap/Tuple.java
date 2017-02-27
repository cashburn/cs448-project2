package heap;

import global.* ;
import chainexception.ChainException;
public class Tuple{
	
	/*FIXME this shouldn't be an arbitrary number*/
	public static final int max = 1024;

	private byte[] tupleArray;
	private int tupleLength;

	//initialize a blank tuple
	public Tuple(){
		tupleArray = new byte[max];
		tupleLength = max;
	}

	//init a tuple with fields
	public Tuple(byte[] data, int offset, int length){
		tupleArray = data;
		tupleLength = length;
	}

	public int getLength(){return tupleLength;}

	public byte[] getTupleByteArray(){return tupleArray;}
}