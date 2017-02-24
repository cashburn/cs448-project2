package heap; 

import global.* ;
import chainexception.ChainException;
import java.util.*;

public class HeapFile{
	private ArrayList<PageId> pageList;
	private String heapFileName;
	private ArrayList<Integer> pidList;
	private int count;
	private HFPage curr;


	public HeapFile(String name){
		Page p = new Page();
		pageList = new ArrayList<PageId>();
		pidList = new ArrayList<Integer>();

		if(name != null){
			heapFileName = name;

			//check to see if exists
			PageId page = global.Minibase.DiskManager.get_file_entry(heapFileName);

			if(page == null){
				page = global.Minibase.BufferManager.newPage(p, 1);

				//write to disk
				global.Minibase.DiskManager.add_file_entry(heapFileName, page);

				//need to unpin the page for now
				global.Minibase.BufferManager.unpinPage(page, true);
				pageList.add(page);
				pidList.add(page.pid);
				global.Minibase.BufferManager.pinPage(page, p, false);
				curr = new HFPage(p);
				curr.setCurPage(page);
				global.Minibase.BufferManager.unpinPage(page, true);
				count = 0;

				return;
			}

			//Page already exists
			//pin it
			global.Minibase.BufferManager.pinPage(page, p, false);


			curr = new HFPage(p);
			curr.setData(p.getData());
			pageList.add(page);
			pidList.add(page.pid);
			global.Minibase.BufferManager.unpinPage(page, false);
			RID x = curr.firstRecord();
			while(x != null){
				count++;
				x = curr.nextRecord(x);
			}


			PageId cPage = curr.getNextPage();
			while(cPage.pid > 0){
				HFPage y = new HFPage();
				global.Minibase.BufferManager.pinPage(cPage, y, false);
				pageList.add(cPage);
				pidList.add(cPage.pid);

				x = y.firstRecord();
				while(x != null){
					count++;
					x = y.nextRecord(x);
				}

				global.Minibase.BufferManager.unpinPage(cPage, false);
				cPage = y.getNextPage();
			}
		} else {
			//passed name is null
			PageId page = global.Minibase.DiskManager.get_file_entry(heapFileName);
			curr = new HFPage(p);
			curr.setCurPage(page);
			pageList.add(page);
			pidList.add(page.pid);
			global.Minibase.BufferManager.unpinPage(page, true);
		}
	}

	public RID insertRecord(byte[] record)throws ChainException{return null;}

	public Tuple getRecord(RID rid){return null;}

	public boolean updateRecord(RID rid, Tuple newRecord) throws ChainException{return false;}

	public boolean deleteRecord(RID rid){return false;}

	public int getRecCnt(){return 0;}

	public HeapScan openScan(){return null;}
}