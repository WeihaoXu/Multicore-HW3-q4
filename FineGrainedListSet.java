package q4;
import java.util.concurrent.locks.*;
public class FineGrainedListSet implements ListSet {
// you are free to add members
  public Node head, tail;
  int count;
	
  public FineGrainedListSet() {
    // implement your constructor here
    head = new Node(Integer.MIN_VALUE); // the value of head is never used. Head is initialized so that it can lock
    tail = new Node(Integer.MAX_VALUE);
    head.next = tail;
  }


	  
  public boolean add(int value) {
    // implement your add method here	
    Node prev, curr;
    Node node = new Node(value);
    head.lock();
    prev = head;
    try{
      if(head.next == tail){ //first node inserted
        node.next = tail;
        head.next = node;
        return true;
      } 
      else {
        prev = head;
        curr = head.next;
        curr.lock();
        try{
          while((curr!= tail) && (curr.value <= node.value)){
            prev.unlock();
            prev = curr;
            curr = curr.next;
            curr.lock();
          }
          if(prev.value == node.value){
            return false;
          }
          else{
            node.next = curr;
            prev.next = node;
            return true;
          }
        } finally {curr.unlock();}
      }
    } finally {prev.unlock();}

  }
	  
  public boolean remove(int value) {
    // implement your remove method here	
    return false;
  }







	  
  public boolean contains(int value) {
    // implement your contains method here	
    Node temp = head;
    while(temp.next!=tail){
      if(temp.next.value == value) return true;
      temp = temp.next;
    }
    return false;
  }
	  
  protected class Node implements Lock{
    public Integer value;
    public Node next;
    private ReentrantLock lock;
			    
  	public Node(Integer x) {
  		value = x;
  		next = null;
      lock = new ReentrantLock();
  	}

    public void lock(){
      lock.lock();
    }
    public void unlock(){
      lock.unlock();
    }
  }

  


  public static void main(String[] args){
    FineGrainedListSet f = new FineGrainedListSet();
    Thread[] threads = new Thread[5];
    for(int i =0; i < 5; i++)
    {
      Test test = new Test(f, i);
      threads[i] = new Thread(test);
      threads[i].start();
    }
    try{
      for(int i = 0; i < 5; i++){
        threads[i].join();
      }
    } catch (Exception e){}

    Node temp = f.head.next;
    while(temp!=f.tail)
    {
      System.out.println(temp.value);
      temp = temp.next;
    }
  }




}

class Test implements Runnable{
    FineGrainedListSet f;
    int seed;
    public Test(FineGrainedListSet f, int seed){
      this.f = f;
      this.seed = seed;
    }
    public void run(){
      for(int i = 0; i < 5; i++){
        f.add(seed+=5);
      }

    }
  }















