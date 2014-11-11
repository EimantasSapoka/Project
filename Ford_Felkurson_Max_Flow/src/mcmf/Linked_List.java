package mcmf;

/**
 * This class implements a linked list data structure.
 */
public class Linked_List
{
   /*******************
        LINKED LIST
   *******************/
   
   Link first;    // first link in list
   Link current;  // current link in list
   Link last;     // last link in list
   int num;       // number of links in list

   /**
    * constructor - initialises instance variables
    */
   public Linked_List()
   {
      first = null;
      current = null;
      last = null;
      num = 0;
   }


   /**
    * adds an element to the end of the list
    * @param   o  object to add to list
    */
   public void add(Object o)
   {
      if(first == null)// if list is empty
      {
         first = new Link(o,null); // make new link first
         last = first; // make last first, as only link in list
      }
      else // if not empty
      {
         Link l = new Link(o,last);// makes new link with last link as previous
         last.next = l; // set last links next to be this link
         last = l; // make this link the new last link
      }
      num++;
   }
   
   /**
    * deletes current item from list
    */
   public void delete()
   {
      if(current != null) // if list not empty
      {
         if(current == first) // if first in list
         {
           first = current.next; // make next link first link
           current = first; // set current to first link
           if (first == null)
	      last = null;
           else
	      first.previous = null;
         }
         else if(current == last) // if last link in list
         {
            last = current.previous; // set new last to be previous link
            if (last != null)
               last.next = null; // set new lasts next link to be null
            current = last; // set current to be last link
         }
         else // somewhere in middle of list
         {
            // set previous links next link to be current next link
            current.previous.next = current.next; 
            // set next links previous link to be the previous link
            current.next.previous = current.previous;
            // set current to be next link
            current = current.next;
         }
         num--;
      }
      
   }
   
   /**
    * deletes the specified link from the list
    * @param   l  link to be deleted
    */
   public void delete(Link l)
   {
      // set current to be the link specified then delete
      current = l;
      delete();
   }
   
   
   /**
    * returns and deletes the first item from the list
    * @return  first object in list
    */
   public Object head()
   {
      setToStart();
      Object o = current.item;      
      delete();
      return o;
   }
   
   /**
    * sets current to be first item in list
    */
   public void setToStart() {  current = first; }
   
   /**
    * increments current to next item in list
    */
   public void increment() { current = current.next; }
   
   
   /**
    * returns true if list has another item
    * @return  true if list has another item, false otherwise
    */
   public boolean hasNext() { return current != null;}
   
   /**
    * returns the current object in the list
    * @return  current object in list
    */
   public Object getCurrentItem(){ return current.item;}
   
   
   /**
    * returns the current link in the list
    */
   public Link getLink() { return current;}
   
   
   /**
    * returns the number of objects in the list
    * @return number of objects in list
    */
   public int getNum() { return num; }
   
   
   
   
   /**
    * This class stores information for a link in the linked list.
    *
    * @author  Alison Smart
    */
   public class Link
   {
      /*****************
         LINK
       ****************/
      
      Object item;      // item stored in link
      Link next;        // next item in list
      Link previous;    // previous item in list
      
      
      /**
       * constructor - initialises instance variables
       * @param   o     object stored in link
       * @param   l     previous link in list
       */
      public Link(Object o, Link l)
      {
         item = o;
         next = null;
         previous = l;
      }
      
      Object getItem(){ return item;}
         
   }
}

