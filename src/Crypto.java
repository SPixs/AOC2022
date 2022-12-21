import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Crypto {

	public static void main(String[] args) throws IOException {
		
		Stream<String> lines = Files.lines(Paths.get("./input_crypto.txt"));
		List<String> collect = lines.collect(Collectors.toList());
		
		int sum = 0;
		
		List<MyLong> list = collect.stream().map(s -> new MyLong(Integer.parseInt(s))).collect(Collectors.toList());
		MyLinkedList<MyLong> linkedList = new MyLinkedList<MyLong>(list);

		System.out.println("Initial arrangement:");
		int zeroIndex = 0;

		mix(list, linkedList);
//		System.out.println(linkedList);
		for (int i=0;i<linkedList.size();i++) { if (linkedList.get(i).val == 0) zeroIndex = i; }
		System.out.println(linkedList.get(zeroIndex+1000).val);
		System.out.println(linkedList.get(zeroIndex+2000).val);
		System.out.println(linkedList.get(zeroIndex+3000).val);
		System.out.println("Groove coordinate: " + (linkedList.get(zeroIndex+1000).val+linkedList.get(zeroIndex+2000).val+linkedList.get(zeroIndex+3000).val));

		list = list.stream().map(i -> new MyLong(i.val * 811589153l)).collect(Collectors.toList());
		linkedList = new MyLinkedList<MyLong>(list);
//		System.out.println(linkedList);

		for (int i=0;i<10;i++) {
			mix(list, linkedList);
//			System.out.println(linkedList);
		}
		zeroIndex = 0;
		for (int i=0;i<linkedList.size();i++) { if (linkedList.get(i).val == 0) zeroIndex = i; }
		System.out.println();
		System.out.println(linkedList.get(zeroIndex+1000).val);
		System.out.println(linkedList.get(zeroIndex+2000).val);
		System.out.println(linkedList.get(zeroIndex+3000).val);
		System.out.println("Groove coordinate: " + (linkedList.get(zeroIndex+1000).val+linkedList.get(zeroIndex+2000).val+linkedList.get(zeroIndex+3000).val));

	
	}

	private static void mix(List<MyLong> list, MyLinkedList<MyLong> linkedList) {
		int progress = 0;
		for (MyLong myInteger : list) {
			long delta = myInteger.val;
			shiftMod(linkedList, myInteger, delta);
		}
	}

	private static void shift(MyLinkedList<MyLong> list, MyLong element, long delta) {
		long movesCount = Math.abs(delta)/* % list.size()*/;
		if (element.getVal() > 0) {
//				System.out.println("** " + myInteger +" moves right " + movesCount);
			for (long i=0;i<movesCount;i++) {
				list.moveRight(element);
//					System.out.println("** " + list);
			}
		}
		else if (element.getVal() < 0) {
//				System.out.println("** " + myInteger +" moves left " + movesCount);
			for (long i=0;i<movesCount;i++) {
				list.moveLeft(element);
//					System.out.println("** " + list);
			}
		}
	}
	
	private static void shiftMod(MyLinkedList<MyLong> list, MyLong element, long delta) {
		long movesCount = Math.abs(delta) % (list.size()-1);
		if (element.getVal() > 0) {
			int index = list.indexOf(element);
			if (index + movesCount < list.size() - 1) {
				list.remove(element);
				list.add((int) (index+movesCount), element);
			}
			else {
				list.remove(element);
				list.add((int) (index+movesCount) % (list.size()), element);
			}
		}
		else if (element.getVal() < 0) {
			int index = list.indexOf(element);
			if (index - movesCount > 1) {
				list.remove(element);
				list.add((int) (index-movesCount), element);
			}
			else {
				list.remove(element);
				list.add((int) (index-movesCount+list.size()) % (list.size()), element);
			}
		}
	}
	
	private static class MyLong {
		private long val;

		public MyLong(long val) {
			super();
			this.val = val;
		}
		
		public long getVal() {
			return val;
		}
		
		@Override
		public String toString() {
			return String.valueOf(val);
		}
	}
	
	public static class MyLinkedList<E> extends LinkedList<E> {

		private int staticSize;

		public MyLinkedList(Collection<? extends E> c) {
			super(c);
			staticSize = size();
		}
		
		public void moveLeft(E myInteger) {
			int index = indexOf(myInteger);
			remove(index);
			if (index == 1) {
				add(size(), myInteger);
			}
			else if (index == 0) {
				add(size()-1, myInteger);
			}
			else {
				add(index-1, myInteger);
			}
		}
		
		public int moveLeft(int index) {
			E myInteger = get(index);
			remove(index);
			if (index == 1) {
				int targetPos = size();
				add(targetPos, myInteger);
				return targetPos;
			}
			else if (index == 0) {
				int targetPos = size() - 1;
				add(size()-1, myInteger);
				return targetPos;
			}
			else {
				add(index-1, myInteger);
				return index-1;
			}
		}

		public void moveRight(E myInteger) {
			int index = indexOf(myInteger);
			if (index == size()-1) {
				remove(index);
				add(1, myInteger);
			}
			else {
				remove(index);
				add(index+1, myInteger);
			}
		}

		public int moveRight(int index) {
			E myInteger = get(index);
			if (index == size()-1) {
				remove(index);
				add(1, myInteger);
				return 1;
			}
			else {
				remove(index);
				add(index+1, myInteger);
				return index+1;
			}
		}

		public void insertBefore(MyLong toReplace, E myInteger) {
			int targetIndex = indexOf(toReplace);
			System.out.println(myInteger + " moves between " + get(targetIndex) + " and " + get(targetIndex+1)+":");
			add(targetIndex, myInteger);
		}

		public void insertAfter(MyLong toReplace, E myInteger) {
			int targetIndex = normalize(indexOf(toReplace)+1);
			System.out.println(myInteger + " moves between " + get(targetIndex) + " and " + get(targetIndex+1)+":");
			add(targetIndex, myInteger);
		}

		@Override
		public void add(int index, E element) {
//			index = normalize(index);
			super.add(index, element);
		}
		
		@Override
		public E get(int index) {
			index = normalize(index);
			return super.get(index);
		}

		private int normalize(int index) {
			while (index < 0) index += size();
			index = index % size();
			return index;
		}
		
		@Override
		public E remove(int index) {
//			index = normalize(index);
			return super.remove(index);
		}
	}
}
