import java.lang.*;
public class Main {

    int[][] L1I = new int[2][4];
    int[][] L1D = new int[2][4];
    int[][] L2 = new int[4][4];

    public static void main(String[] args) {

        System.out.println("Hello world!");
        System.out.println(args[1]);
    }

    public static void data_load(String address, String size, int blockOffset, int setIndex, int E ){


    }


    // Method to calculate tag
    public static String calculate_tag(String address, int s, int b){
       String binaryAddress = hex2Binary(address);
       String Tag = binaryAddress.substring(0, binaryAddress.length() - ( s + b));
       Tag = binary2Hex(Tag);
       return Tag;
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

}