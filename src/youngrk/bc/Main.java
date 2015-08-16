package youngrk.bc;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) {
		String hhDirectoryStr = args[0];
		File hhDirectory = new File(hhDirectoryStr);
		String[] hhDirectoryArr = hhDirectory.list();
		String[] filenameArr;
		String type;
		
		for(String file : hhDirectoryArr) {
			filenameArr = file.split("\\s?\\-\\s?");
			type = filenameArr[3];
			
			if(type.equalsIgnoreCase("MTT")) {
				
			} else if(type.equalsIgnoreCase("STT")) {
				
			} else if(type.equalsIgnoreCase("RING")) {
				String sb = filenameArr[4];
				String bb = filenameArr[5];
				String game = filenameArr[6];
				String structure = filenameArr[7];
				String tblNum = filenameArr[8].substring(filenameArr[8].indexOf("No.")+3, filenameArr[8].indexOf(".txt"));
				
				try {
					NLHoldemCash sessionHistory = new NLHoldemCash(hhDirectoryStr + "/" + file, type, sb, bb, game, structure, tblNum);
					sessionHistory.convert();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				HoldemCash sessionHistory = new HoldemCash();
			}
		}
	}
	
}
