import java.lang.*;
public class Main {

    static int hitCount = 0;
    static int missCount = 0;
    static int evictionCount = 0;

    // şimdilik test amaçlı koydum burayı, sonradan kaldırırız
    static String[][] L1I = new String[2][4];
    static String[][] L1D = new String[2][4];
    static String[][] L2 = new String[4][4];

    static int L1s = 0;
    static int L1E = 0;
    static int L1b = 0;
    static int L2s = 0;
    static int L2E = 0;
    static int L2b = 0;


    public static void main(String[] args) {

        System.out.println("Hello world!");
        System.out.println(args[1]);
    }

    public static void data_load(String address, String size){
        // first check if is it hit for L1
        int L1S = calculate_set_index(L1s, L1b, address);
        String L1tag = calculate_tag(L1s, L1b, address);
        if(!isHit(L1S, L1E, L1I, L1tag)){
            missCount++;
            //TODO: fill the miss part L1
        }
        // check if is it hit for L2
        int L2S = calculate_set_index(L2s, L2b, address);
        String L2tag = calculate_tag(L2s, L2b, address);
        if(!isHit(L2S, L2E, L2, L2tag)){
         missCount++;
         // TODO: fill the miss part for L2
        }

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