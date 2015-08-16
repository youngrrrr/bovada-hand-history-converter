package youngrk.bc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class NLHoldemCash {

	private String type, sb, bb, game, structure, tblNum;
	
	private BufferedReader br;
	private StringBuilder hhsb;
	private HashMap<String,String> playerHolecardsHM;
	private String heroPosition;
	private String board;
	private Double lastRaise;
	private Double potBeforeRake, potAfterRake;
	
	public NLHoldemCash(String filePath, String type, String sb, String bb, String game, String structure, String tblNum) throws FileNotFoundException {
		this.type = type;
		this.sb = sb;
		this.bb = bb;
		this.game = game;
		this.structure = structure;
		this.tblNum = tblNum;
		
		this.br = new BufferedReader(new FileReader(filePath));
		this.hhsb = new StringBuilder();
		this.playerHolecardsHM = new HashMap<String,String>(9);
		this.lastRaise = Double.parseDouble(bb);
	}
	
	public void convert() {
		int response = 0;
		
		while(response >= 0 || response != 200) {
			switch (response) {
				case 0: response = table();
						break;
				case 1: response = gameAction("*** FLOP ***");
						break;
				case 2: response = gameAction("*** TURN ***");
						break;
				case 3: response = gameAction("*** RIVER ***");
						break;
				case 4: response = gameAction("Showdown");
						break;
				case 5: response = showdown();
						break;
				case 6: response = summary();
						break;
				default: //log some error
			}
		}
		
		if(response == 200) {
			//log success
		} else {
			//log error
		}
	}
	
	private int table() {
		try {
			String input = this.br.readLine();
			int titleIndex = 0;
			int seatNum = -1;
			
			while (!input.contains("*** HOLE CARDS ***")) {
				if (input.contains("Bovada Hand")) {
					String[] inputArr = input.split(" ");
					inputArr[0] = "PokerStars";
					inputArr[2] = inputArr[2] + ":";
					inputArr[3] = "Hold'em";
					inputArr[4] = "No";
					inputArr[5] = "Limit";
					inputArr[6] = "(" + this.sb + "/" + this.bb + ")";
					inputArr[8] = inputArr[8].replace("-", "/");
					
					input = "";
					for(int i = 0; i < inputArr.length; i++) {
						input += inputArr[i] + " ";
					}
					
					titleIndex = input.length() + 1;
				} else if (input.contains("+")) {
					input = input.replace("+", "plus");
				} else if (input.contains("/")) {
					if (input.contains("Set dealer/")) {
						int seatNumIndex = input.indexOf("[") + 1;
						seatNum = Integer.parseInt(input.substring(seatNumIndex, seatNumIndex + 1));
						input = this.br.readLine();
						continue;
					}
					
					if (input.contains("[ME]")) {
						if (input.contains("Ante/")) {
							input = "Hero: posts small blind " + this.sb;
						} else if (input.contains("blind/Bring")) {
							input = "Hero: posts big blind " + this.bb;
						}
					} else {
						if (input.contains("Ante/")) {
							input = "Small Blind: posts small blind " + this.sb;
						} else if (input.contains("blind/Bring")) {
							input = "Big Blind: posts big blind " + this.bb;
						}
					}
				} else if (input.contains("[ME]")) {
					String[] heroArr = input.split(": [^\\[ME\\]]*\\[ME\\] ");
					input = heroArr[0] + ": Hero " + heroArr[1];
				} else if (input.contains("Seat stand") || input.contains("Seat sit out") || 
							input.contains("Table enter user") || input.contains("Table leave user")) {
					input = this.br.readLine();
					continue;
				}
				
				this.hhsb.append(input + "\n");
				
				input = this.br.readLine();
			}
			
			this.hhsb.append(input + "\n");
			this.hhsb.insert(titleIndex, "Table '#" + this.tblNum + "' Seat #" + seatNum + " is the button\n");
		} catch (IOException e) {
			
			return -1;
		} catch (Exception e) {
			
			return -1;
		}
		
		return 1;
	}
	
	private int gameAction(String street) {
		try {
			String input = this.br.readLine();
			
			while(!(input.contains(street) || input.contains("*** SUMMARY ***"))) {
				if (input.contains("Card dealt")) {
					String[] tempPlayerHolecardsArr = input.split(" : Card dealt to a spot "); 
					
					if (input.contains("[ME]")) {
						input = "Dealt to Hero " + tempPlayerHolecardsArr[1];
						this.playerHolecardsHM.put("Hero", tempPlayerHolecardsArr[1]);
						this.heroPosition = tempPlayerHolecardsArr[0].replace("+", "plus");
					} else {
						input = this.br.readLine();
						this.playerHolecardsHM.put(tempPlayerHolecardsArr[0], tempPlayerHolecardsArr[1]);
						continue;						 
					}
					
				}  else if (input.contains("Seat stand") || input.contains("Seat sit out") || 
						input.contains("Table enter user") || input.contains("Table leave user")) {
					input = this.br.readLine();
					continue;
				} else {
					String[] split = input.split(" : ");
					
					if (split[0].contains("[ME]")) {
						split[0] = "Hero";
					} else if (split[0].contains("+")) {
						split[0] = split[0].replace("+", "plus");
					}
					
					input = split[0] + ": " + split[1].toLowerCase().replace(" (timeout)", "");
					
					if (split[1].contains("Raises")) {
						String lastRaiseStr = split[1].substring(split[1].indexOf("$", split[1].indexOf("to"))+1,split[1].length());
						lastRaise = Double.parseDouble(lastRaiseStr);
					} else if (split[1].contains("uncalled")) {
						String potBeforeRakeStr = split[1].substring(split[1].indexOf("$")+1,split[1].length()-1);
						potBeforeRake = Double.parseDouble(potBeforeRakeStr);
						input = "Uncalled bet ($" + potBeforeRakeStr + ") returned to " + split[0];
					} else if (split[1].contains("Does not show")) {
						input = split[0] + ": doesn't show hand";
					} else if (split[1].contains("result $")) {
						String potAfterRakeStr = split[1].substring(split[1].indexOf("$")+1,split[1].length()-1);
						potAfterRake = Double.parseDouble(potAfterRakeStr);
						input = split[0] + " collected $" + potAfterRakeStr + " from pot";
					} else if (split[1].contains("All-in")) {
						if(split[1].contains("(raise)")) {
							Double allInBet = Double.parseDouble(split[1].substring(split[1].indexOf("$")+1,split[1].length()-1));
							input = split[0] + ": raises $" + (allInBet - lastRaise) + " to $" + allInBet + " and is all-in";
						} else {
							String allInCall = split[1].substring(split[1].indexOf("$")+1,split[1].length()-1);
							input = split[0] + ": calls $" + allInCall + " and is all-in"; 
						}
					}
					
				}
				
				this.hhsb.append(input + "\n");
				
				input = this.br.readLine();
			}
			
			if(input.contains("Showdown")) {
				input = "*** SHOW DOWN ***";
			}
			this.hhsb.append(input + "\n");
			
			if(input.contains("*** FLOP ***")) {
				return 2;
			} else if (input.contains("*** TURN ***")) {
				return 3;
			} else if (input.contains("*** RIVER ***")) {
				return 4;
			} else if (input.contains("*** SHOW DOWN ***")) {
				return 5;
			} else { //SUMMARY
				return 6;
			}
			
		} catch (IOException e) {
			
			return -1;
		} catch (Exception e) {
			
			return -1;
		}
	}
	
	private int showdown() {
		try {
			String input = this.br.readLine();
			
			while(!input.contains("*** SUMMARY ***")) {
				String[] split = input.split(" : ");
				if (split[0].contains("+")) {
					split[0].replace("+", "plus");
				} else if (split[0].contains("[ME]")) {
					split[0] = "Hero";
				}
				
				if (split[1].contains("Showdown")) {
					input = split[0] + ": shows " + this.playerHolecardsHM.get(split[0]) + " " + split[1].substring(split[1].indexOf("(")); 
				} else if (split[1].contains("Hand result")) {
					String potAfterRakeStr = split[1].substring(split[1].indexOf("$")).replace(" ", "");
					this.potAfterRake = Double.parseDouble(potAfterRakeStr);
					input = split[0] + " collected " + potAfterRakeStr + " from pot";
				} else if (input.contains("Seat stand") || input.contains("Seat sit out") || 
						input.contains("Table enter user") || input.contains("Table leave user")) {
					input = this.br.readLine();
					continue;
				}
				
				this.hhsb.append(input + "\n");
				
				input = this.br.readLine();
			}
		} catch (IOException e) {
			
		} catch (Exception e) {
			
			return -1;
		}
		
		return 6;
	}
	
	private int summary() {
		try {
			String input = this.br.readLine();
			
			while(!input.contains("\n")) {
				
				
				this.hhsb.append(input + "\n");
				
				input = this.br.readLine();
			}
		} catch (IOException e) {
			
		} catch (Exception e) {
			
			return -1;
		}
		
		return 7;
	}
	
}
