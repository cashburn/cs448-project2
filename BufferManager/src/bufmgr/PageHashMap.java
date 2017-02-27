package bufmgr;

import global.*;
import java.util.ArrayList;
import java.util.LinkedList;
//import java.util.List;
import java.util.ListIterator;

class HashEntry {
    PageId page;
    int frame;

    public HashEntry(PageId page, int frame) {
        this.page = page;
        this.frame = frame;
    }
}

public class PageHashMap {
    LinkedList<HashEntry>[] array;
    int size;

	public PageHashMap(int entries) {
		size = entries;
        array = new LinkedList[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = new LinkedList<HashEntry>();
        }
	}

    private int hash(PageId key) {
        int hash = key.pid % size;
        if (hash < 0) {
            hash += size;
        }

        return hash;
    }

    public void put(PageId key, int value) {
        int hash = hash(key);
        HashEntry entry = new HashEntry(key, value);
        LinkedList<HashEntry> list = array[hash];
        list.addFirst(entry);
    }

    public Integer get(PageId key) {
        int hash = hash(key);
        LinkedList<HashEntry> list = array[hash];
        ListIterator<HashEntry> iter = list.listIterator(0);
        HashEntry entry;
        while (iter.hasNext()) {
            entry = iter.next();
            if (entry.page.pid == key.pid) {
                return entry.frame;
            }
        }
        return null;
    }

    public void remove(PageId key) {
        int hash = hash(key);
        LinkedList<HashEntry> list = array[hash];
        ListIterator<HashEntry> iter = list.listIterator(0);
        HashEntry entry;
        while (iter.hasNext()) {
            entry = iter.next();
            if (entry.page.pid == key.pid) {
                iter.remove();
            }
        }
    }



}
