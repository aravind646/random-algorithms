/**
 * Created by Aravind Selvan on 10/4/14.
 */
public class ListNode {
    public String data;
    public ListNode next;

}

class driver {
    public static ListNode Reverse(ListNode list)
    {
        if (list == null) return null; // first question

        if (list.next == null) return list; // second question

        // third question - in Lisp this is easy, but we don't have cons
        // so we grab the second element (which will be the last after we reverse it)

        ListNode secondElem = list.next;

        // bug fix - need to unlink list from the rest or you will get a cycle
        list.next = null;

        // then we reverse everything from the second element on
        ListNode reverseRest = Reverse(secondElem);

        // then we join the two lists
        secondElem.next = list;

        return reverseRest;
    }

    public static void main(String[] args) {
        ListNode[] listNode = new ListNode[5];
        for (int index = 0; index < 5; index++) {
            listNode[index] = new ListNode();
            listNode[index].data = String.valueOf(index);
        }
        for (int index = 0; index < 5 - 1; index++) {
            listNode[index].next = listNode[index + 1];
        }
        Reverse(listNode[0]);
    }
}
