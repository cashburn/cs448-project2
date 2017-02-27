package heap; 

import global.* ;
import chainexception.ChainException;
import java.util.*;

public class HeapFile{
	protected ArrayList<PageId> pageList;
	protected String heapFileName;
	protected ArrayList<Integer> pidList;
	protected int count;
	protected HFPage curr;
	PageId headId;
	protected Page p;


	public HeapFile(String name){
		p = new Page();
		pageList = new ArrayList<PageId>();
		pidList = new ArrayList<Integer>();

		if(name != null){
			heapFileName = name;

			//check to see if exists
			PageId page = headId = Minibase.DiskManager.get_file_entry(heapFileName);

			if(page == null){
				page = Minibase.BufferManager.newPage(p, 1);

				//write to disk
				Minibase.DiskManager.add_file_entry(heapFileName, page);

				//need to unpin the page for now
				Minibase.BufferManager.unpinPage(page, true);
				pageList.add(page);
				pidList.add(page.pid);
				Minibase.BufferManager.pinPage(page, p, false);
				curr = new HFPage(p);
				curr.setCurPage(page);
				Minibase.BufferManager.unpinPage(page, true);
				count = 0;

				return;
			}

			//Page already exists
			//pin it
			Minibase.BufferManager.pinPage(page, p, false);


			curr = new HFPage(p);
			curr.setData(p.getData());
			pageList.add(page);
			pidList.add(page.pid);
			Minibase.BufferManager.unpinPage(page, false);
			RID x = curr.firstRecord();
			while(x != null){
				count++;
				x = curr.nextRecord(x);
			}


			PageId cPage = curr.getNextPage();
			while(cPage.pid > 0){
				HFPage y = new HFPage();
				Minibase.BufferManager.pinPage(cPage, y, false);
				pageList.add(cPage);
				pidList.add(cPage.pid);

				x = y.firstRecord();
				while(x != null){
					count++;
					x = y.nextRecord(x);
				}

				Minibase.BufferManager.unpinPage(cPage, false);
				cPage = y.getNextPage();
			}
		} else {
			//passed name is null temp file
			PageId page = global.Minibase.DiskManager.get_file_entry(heapFileName);
			curr = new HFPage(p);
			curr.setCurPage(page);
			pageList.add(page);
			pidList.add(page.pid);
			Minibase.BufferManager.unpinPage(page, true);
		}
	}

	public RID insertRecord(byte[] record)throws ChainException{
		int l = record.length; 

		//search for space in an existing page
		for (int i = 0 ;i < pageList.size(); i++){
			PageId page = pageList.get(i);
			Page p = new Page();

			Minibase.BufferManager.pinPage(page, p, false);
			HFPage hf = new HFPage(p);
			hf.setCurPage(page);
			hf.setData(p.getData());
			if(hf.getFreeSpace() > l){
				RID ret = hf.insertRecord(record);
				Minibase.BufferManager.unpinPage(page, true);
				count++;
				return ret;
			}
			Minibase.BufferManager.unpinPage(page, false);
		}

		//No existing page can house our record so make a new one
		Page p = new Page();
		PageId page = Minibase.BufferManager.newPage(p, 1);

		HFPage hf = new HFPage(p);
		hf.initDefaults();
		hf.setCurPage(page);

		RID ret = hf.insertRecord(record);
		hf.setNextPage(page);
		hf.setPrevPage(curr.getCurPage());
		curr = hf;

		pageList.add(page);
		pidList.add(page.pid);

		Minibase.BufferManager.unpinPage(page, true);

		count++;
		return ret;
	}

	/*Todo - not sure about this one*/
	public Tuple getRecord(RID rid){return null;}

	public boolean updateRecord(RID rid, Tuple newRecord) throws ChainException{
		PageId page = rid.pageno;
		Page p = new Page();

		Minibase.BufferManager.pinPage(page, p, false);
		HFPage hf = new HFPage(p);
		Minibase.BufferManager.unpinPage(page, false);
		return true;
	}

	public boolean deleteRecord(RID rid){return false;}

	public int getRecCnt(){return count;}

	/*Todo - not sure about this one either */
	public HeapScan openScan(){return new HeapScan(this);}

	public Iterator<PageId> iterator(){
		return	pageList.iterator();
	}
}