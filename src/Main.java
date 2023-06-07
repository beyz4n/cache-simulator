import java.io.File;
import java.lang.*;
import java.util.*;

public class Main {

    static int hitCount = 0;
    static int missCount = 0;
    static int evictionCount = 0;

    final static int col = 4;
    // şimdilik test amaçlı koydum burayı, sonradan kaldırırız
    static String[][] L1I ; // 0 tag, 1 time, 2 valid, 3 data
    static String[][] L1D ;
    static String[][] L2 ;

    static int L1s = 0;
    static int L1E = 0;
    static int L1b = 0;
    static int L2s = 0;
    static int L2E = 0;
    static int L2b = 0;

    static String ram[];

    public static void main(String[] args) throws Exception {

        L1s = Integer.parseInt(args[1]);
        L1E = Integer.parseInt(args[3]);
        L1b = Integer.parseInt(args[5]);
        L2s = Integer.parseInt(args[7]);
        L2E = Integer.parseInt(args[9]);
        L2b = Integer.parseInt(args[11]);

        String inputTrace = args[13];

        //File traceFile = new File(inputTrace);
        File ramFile = new File("RAM.dat"); // file input stream ile yap

        //if (!traceFile.exists())
        //    throw new Exception("trace file does not exist: " + traceFile.getName());
        if (!ramFile.exists())
            throw new Exception("ram file does not exist: " + ramFile.getName());

        L1I = new String[L1s*L1E][col];
        L1D = new String[L1s*L1E][col];
        L2 = new String[L2s*L2E][col];

        //Scanner traceScanner = new Scanner(traceFile);
        Scanner ramScanner = new Scanner(ramFile);

        for(int i = 0 ; ramScanner.hasNext() ; i++){
                ram[i] = ""+ramScanner.next().charAt(7);

         }




    }

    public static void data_load(String address, String size, String L1I[][], String L2[][]){
        // first check for L1
        int L1S = calculate_set_index(L1s, L1b, address);
        String L1tag = calculate_tag(L1s, L1b, address);
        int lineIndex;
        // if it is miss for L1
        if(!isHit(L1S, L1E, L1I, L1tag)){
            missCount++;
            if(isContainEmptyLine(L1S, L1E, L1I)){
                lineIndex = findEmptyLineIndex(L1S, L1E, L1I);
            }
            else{
                evictionCount++;
                lineIndex = findEvictionLine(L1S, L1E, L1I);
            }
            L1I[L1S*L1E + lineIndex][0] = L1tag;
            L1I[L1S*L1E + lineIndex][1] = findTime(L1S, L1E, L1I);
            L1I[L1S*L1E + lineIndex][2] = "1";
            // TODO: add data part
        }
        // check for L2
        int L2S = calculate_set_index(L2s, L2b, address);
        String L2tag = calculate_tag(L2s, L2b, address);
        // if it is miss for L2
        if(!isHit(L2S, L2E, L2, L2tag)){
            missCount++;
            if(isContainEmptyLine(L2S, L2E, L2)){
                lineIndex = findEmptyLineIndex(L2S, L2E, L2);
            }
            else {
                evictionCount++;
                lineIndex = findEvictionLine(L2S, L2E, L2);
            }
            L2[L2S*L2E + lineIndex][0] = L2tag;
            L2[L2S*L2E + lineIndex][1] = findTime(L2S, L2E, L2);
            L1I[L1S*L1E + lineIndex][2] = "1";
            // TODO: add data part
        }
    }


    // Method to check if the set contains empty line
    public static boolean isContainEmptyLine(int S, int E, String cache[][]){
        boolean isContain = false;
        for(int i = 0; i < E; i++){
            if(cache[S*E + i][0].equalsIgnoreCase(""))
                isContain = true;
        }
        return isContain;
    }

    // Method to find empty line inside the set
    public static int findEmptyLineIndex(int S, int E, String cache[][]){
        int index = 0;
        for(int i = 0; i < E; i++){
            if(cache[S*E + i][0].equalsIgnoreCase(""))
                index = i;
        }
        return index;
    }

    // Method to find line for eviction in that set
    public static int findEvictionLine(int S, int E, String cache[][]){
        int index = 0;
        int min = Integer.parseInt(cache[S*E][1]);
        int new_min;
        for(int i = 1; i < E; i++){
            new_min = Integer.parseInt(cache[S*E + i][1]);
            if( new_min< min){
                min = new_min;
                index = i;
            }
        }
        return index;
    }

    public static String findTime(int S, int E, String cache[][]){
        int time = 0;
        int newTime;
        for(int i = 0; i < E; i++){
            newTime = (cache[S*E + i][1].equalsIgnoreCase("")) ? 0: Integer.parseInt(cache[S*E + i][1]);
            if(newTime > time)
                time = newTime;
        }
        time++;
        return "" + time;
    }

    public static boolean isHit(int S, int E, String cache[][] ,String tag){
        boolean isHit = false;
        for(int i = 0; i < E; i++){
            if(cache[S*E + i][0].equalsIgnoreCase(tag) && cache[S*E + i][2].equalsIgnoreCase("1")){
                hitCount++;
                isHit = true;
                break;
            }
        }
        return isHit;
    }

    public static int calculate_set_index(int s, int b, String address){
        String binaryAddress = hex2Binary(address);
        String setIndexBits = binaryAddress.substring(binaryAddress.length() - ( s + b), binaryAddress.length() -  b);
        int setIndex = binary2Decimal(setIndexBits);
        return setIndex;
    }

    // Method to calculate tag
    public static String calculate_tag(int s, int b, String address){
       String binaryAddress = hex2Binary(address);
       String tag = binaryAddress.substring(0, binaryAddress.length() - ( s + b));
       tag = binary2Hex(tag);
       return tag;
    }

    public static String binary2Hex(String binary){
        int check = (int)(Math.ceil(binary.length() / 4));
        String part;
        String hex = "";
        for(int i = 0; i < check; i++){
            if(binary.length() >= 4) {
                part = binary.substring(binary.length() - 4);
                binary = binary.substring(0, binary.length() - 4);
            }
            else{
                part = binary;
                switch (part.length()){
                    case 1: part = "000" + part; break;
                    case 2: part = "00" + part; break;
                    case 3: part = "0" + part; break;
                }
            }
            switch (part) {
                case "0000": hex += "0"; break;
                case "0001": hex += "1"; break;
                case "0010": hex += "2"; break;
                case "0011": hex += "3"; break;
                case "0100": hex += "4"; break;
                case "0101": hex += "5"; break;
                case "0110": hex += "6"; break;
                case "0111": hex += "7"; break;
                case "1000": hex += "8"; break;
                case "1001": hex += "9"; break;
                case "1010": hex += "A"; break;
                case "1011": hex += "B"; break;
                case "1100": hex += "C"; break;
                case "1101": hex += "D"; break;
                case "1110": hex += "E"; break;
                case "1111": hex += "F"; break;
            }
        }
        return hex;
    }

    public static String hex2Binary(String hex){
        String binary = "";

        for(int i = 0; i < hex.length(); ){
            switch (hex.charAt(i)){
                case '0': binary += "0000"; break;
                case '1': binary += "0001"; break;
                case '2': binary += "0010"; break;
                case '3': binary += "0011"; break;
                case '4': binary += "0100"; break;
                case '5': binary += "0101"; break;
                case '6': binary += "0110"; break;
                case '7': binary += "0111"; break;
                case '8': binary += "1000"; break;
                case '9': binary += "1001"; break;
                case 'A': binary += "1010"; break;
                case 'B': binary += "1011"; break;
                case 'C': binary += "1100"; break;
                case 'D': binary += "1101"; break;
                case 'E': binary += "1110"; break;
                case 'F': binary += "1111"; break;
                case 'a': binary += "1010"; break;
                case 'b': binary += "1011"; break;
                case 'c': binary += "1100"; break;
                case 'd': binary += "1101"; break;
                case 'e': binary += "1110"; break;
                case 'f': binary += "1111"; break;
            }
        }
        return binary;
    }

    // Method to convert unsigned binary number to decimal number
    public static int binary2Decimal(String binary){

        int decimal = 0;
        int exp = binary.length();

        for(int i = 0; i < binary.length(); i++){
            exp --;
            if(binary.charAt(i) == '1') {
                decimal += Math.pow(2, exp);
            }
        }
        return decimal;
    }

}