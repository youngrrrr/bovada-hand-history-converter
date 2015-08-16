package youngrk.bc;


public class Test {
	
	public static void main(String[] args) {
		
//		String s1 = "Seat 6: Big Blind [ME] ($5 in chips)";
//		String s2 = "Big Blind  [ME] : Big blind/Bring in $0.05 ";
		
//		String[] s1arr = s1.split("(?<=: )[^\\[]+\\[ME\\]|^[^\\[]+\\[ME\\](?= :)");
//		String[] s2arr = s2.split("(?<=: )[^\\[]+\\[ME\\]|^[^\\[]+\\[ME\\](?= :)");
		
//		String[] heroArr = s1.split(": [^\\[ME\\]]*\\[ME\\] ");
//		if(heroArr[0].equals("")) { //posting blinds
//			s1 = "Hero: ";
//			if(heroArr[1].contains("Big")) {
//				s1 += "posts big blind $0.05";
//			} else {
//				s1 += "posts small blind $0.02";
//			}
//		} else { //seat position
//			s1 = heroArr[0] + "Hero" + heroArr[1];
//		}
		
//		System.out.println(heroArr[0] + ": Hero " + heroArr[1]);
		
//		String s = "Dealer : All-in(raise) $4.98 to $4.98";
//		String[] sA = s.split(" : ");
//		String lastRaiseStr = sA[1].substring(sA[1].indexOf("$", sA[1].indexOf("to"))+1,sA[1].length());
//		System.out.println(lastRaiseStr);
		
		String a = "Seat stand";
		String[] aA = a.split(" : ");
		for (String b : aA) {
			System.out.println(b);
		}
	}
}
