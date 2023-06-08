import java.io.*;
import java.lang.*;
import java.util.*;

public class Main {

    static int hitCount_L1I = 0;
    static int missCount_L1I = 0;
    static int evictionCount_L1I = 0;

    static int hitCount_L1D = 0;
    static int missCount_L1D = 0;
    static int evictionCount_L1D = 0;

    static int hitCount_L2 = 0;
    static int missCount_L2 = 0;
    static int evictionCount_L2 = 0;

    final static int col = 4;

    static String[][][] L1I; // 0 tag, 1 time, 2 valid, 3 data
    static String[][][] L1D;
    static String[][][] L2 ;

    static int L1s = 0;
    static int L1E = 0;
    static int L1b = 0;
    static int L2s = 0;
    static int L2E = 0;
    static int L2b = 0;

    static ArrayList<String> ram = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        L1s = Integer.parseInt(args[1]);
        L1E = Integer.parseInt(args[3]);
        L1b = Integer.parseInt(args[5]);
        L2s = Integer.parseInt(args[7]);
        L2E = Integer.parseInt(args[9]);
        L2b = Integer.parseInt(args[11]);

        String inputTrace = args[13];

        FileInputStream ramInput = null;
        try {
            ramInput = new FileInputStream("RAM.dat");
        } catch (Exception e) {
            System.out.println("RAM.dat file could not be found");
            System.exit(1);
        }


        DataInputStream ramFile = new DataInputStream(ramInput);

        File traceFile = new File("traces/" + inputTrace);
        File l1iFile = new File("L1I_output.txt");
        File l1dFile = new File("L1D_output.txt");
        File l2File = new File("L2_output.txt");
        File ramOutputFile = new File("RAM_output.txt");

        PrintWriter l1iOutput = new PrintWriter(l1iFile);
        PrintWriter l1dOutput = new PrintWriter(l1dFile);
        PrintWriter l2Output = new PrintWriter(l2File);
        PrintWriter ramOutput = new PrintWriter(ramOutputFile);



        if (!traceFile.exists())
            throw new Exception("trace file does not exist: " + traceFile.getName());


        L1I = fillCacheWith0( new String[ (int)( Math.pow(2, L1s) ) ][L1E][col], (int)( Math.pow(2, L1s) ), L1E );
        L1D = fillCacheWith0(new String[ (int)( Math.pow(2, L1s) ) ][L1E][col],(int)( Math.pow(2, L1s) ) ,L1E );
        L2 = fillCacheWith0( new String[ (int)( Math.pow(2, L2s) ) ][L2E][col], (int)( Math.pow(2, L2s) ), L2E );


        Scanner traceScanner = new Scanner(traceFile);
        //Scanner ramScanner = new Scanner(ramFile);
        String tempStr = "";
        for (int i = 0; ramFile.available() > 0; i++) {
            for (int j = 0; j < 8; j++) {

                tempStr += "" + byteToHex(ramFile.read() + "");
            }
            ram.add(tempStr);
            tempStr = "";
            //System.out.println(i + ". line is : " + ram.get(i));
        }

        ramFile.close();
        String tempStr2 = "";
        String temparray[] ;
        while(traceScanner.hasNext()){
            tempStr2 = traceScanner.nextLine();
            System.out.println( tempStr2 ); // printing the input trace
            temparray = tempStr2.split(" ");
            if(tempStr2.charAt(0) == 'M'){
                modifyData( temparray[1].substring(0,temparray[1].length()-1), temparray[2].substring(0,temparray[2].length()-1), temparray[3] );
            }
            else if(tempStr2.charAt(0) == 'L'){
                data_load( temparray[1].substring(0,temparray[1].length()-1), temparray[2] );
            }
            else if(tempStr2.charAt(0) == 'S'){
                storeData( temparray[1].substring(0,temparray[1].length()-1), temparray[2].substring(0,temparray[2].length()-1), temparray[3]  );
            }
            else if(tempStr2.charAt(0) == 'I'){
                loadInstruction( temparray[1].substring(0,temparray[1].length()-1), temparray[2] );
            }
            else{
                throw new Exception("unknown input from: " + traceFile.getName());
            }

        }
        System.out.println("L1I-hits:"+ hitCount_L1I +" L1I-misses:"+ missCount_L1I + " L1I-evictions:" + evictionCount_L1I);
        System.out.println("L1D-hits:"+ hitCount_L1D +" L1D-misses:"+ missCount_L1D + " L1D-evictions:" + evictionCount_L1D);
        System.out.println("L2-hits:"+ hitCount_L2 +" L2-misses:"+ missCount_L2 + " L2-evictions:" + evictionCount_L2);

        l1iOutput.println("L1I cache: " + "\ntag time valid bit data");
        printCache(L1I, L1s, L1E, l1iOutput);

        l1dOutput.println("L1D cache: " + "\ntag time valid bit data");
        printCache(L1D, L1s, L1E, l1dOutput);

        l2Output.println("L2 cache: " + "\ntag time valid bit data");
        printCache(L2, L2s, L2E, l2Output);

        int sizeOfRam = ram.size();
        for(int i = 0 ; i<sizeOfRam ; i++){
            ramOutput.println(ram.get(i));
        }
        l1iOutput.close();
        l1dOutput.close();
        l2Output.close();
        ramOutput.close();


    }
    public static void printCache(String [][][] cache, int ls, int le, PrintWriter writer){
        for(int i = 0; i< Math.pow(2, ls); i++){
            for(int j = 0; (j < le); j++){
                writer.println(cache[i][j][0] + " " + cache[i][j][1] + " " + cache[i][j][2] + " " + cache[i][j][3]);
            }
        }
    }
    public static int addressToIndex(String str){
        String binaryStr = ""; hex2Binary(str);
        int strLength = str.length();
        for(int i = 0; i<strLength ; i++){
            binaryStr += hex2Binary("" + str.charAt(i) );
        }
        int index = binary2Decimal(binaryStr);
        return index/8;
    }
    public static String[][][] fillCacheWith0 (String[][][] temp ,int ls, int le){
        for(int i = 0 ; i< ls; i++){
            for(int j = 0 ; j < le ; j++){
                for(int k = 0 ; k<4 ; k++){
                    temp[i][j][k] = "";
                }
            }

        }
        return temp;
    }

    public static String byteToHex( String str ){
        int temp = Integer.parseInt(str);
        String tempStr = "";
        String tempStr2 = "";
       for(int i = 0 ; i<8 ; i++){
           tempStr = "" + (temp%2) + tempStr;
           temp = temp/2;
       }
       tempStr = binary2Hex(tempStr);
       tempStr2 = "" + tempStr.charAt(1) + tempStr.charAt(0);


        return tempStr2;
    }

    public static void modifyData(String address, String size, String data){
        loadInstruction(address,size); //firstly call function to load instruction
        data_load(address, size);   //then call function to load data
        storeData(address,size,data); //then call function to store data
    }
    public static void storeData(String address, String size, String data){
        int L1setIndex = calculate_set_index(L1s, L1b, address); //calculates set value of the address for L1
        int L2setIndex = calculate_set_index(L2s, L2b, address); //calculates set value of the address for L2
        String L1tag = calculate_tag(L1s, L1b, address); //calculates tag value of the address for L1
        String L2tag = calculate_tag(L2s, L2b, address); //calculates tag value of the address for L2

        // If there is a hit in L1I
        if(isHit(L1setIndex,L1E, L1I, L1tag, 1)){
            String addressBinary = hex2Binary(address); //convert address from hexadecimal to binary
            String block = addressBinary.substring(addressBinary.length() - L1b); //get the last b bits from the address
            int blocksize = binary2Decimal(block); // change the value to decimal since we want to find the starting index of the data in the block
            modifyRam(data, blocksize, address); //write data to memory
            int L1eIndex = getLine(L1s,L1E, L1I, L1tag); //calculate e index
            modifyCache(L1I, blocksize, data, L1setIndex, L1eIndex); //write data to cache
        }
        else{
            missCount_L1I++;
        }
        //If there is a hit in L1D
        if(isHit(L1setIndex,L1E, L1D, L1tag,2)){
            String addressBinary = hex2Binary(address);  //convert address from hexadecimal to binary
            String block = addressBinary.substring(addressBinary.length() - L1b); //get the last b bits from the address
            int blocksize = binary2Decimal(block);  // change the value to decimal since we want to find the starting index of the data in the block
            modifyRam(data, blocksize, address); //write data to memory
            int L1eIndex = getLine(L1s,L1E, L1D, L1tag); //calculate e index
            modifyCache(L1D, blocksize, data, L1setIndex, L1eIndex); //write data to cache
        }
        else{
            missCount_L1D++;
        }
        //ıf there is a hit in L2
        if(isHit(L1setIndex,L2E, L2, L2tag, 3)){
            String addressBinary = hex2Binary(address);  //convert address from hexadecimal to binary
            String block = addressBinary.substring(addressBinary.length() - L2b); //get the last b bits from the address
            int blocksize = binary2Decimal(block); // change the value to decimal since we want to find the starting index of the data in the block
            modifyRam(data, blocksize, address); //write data to memory
            int L2eIndex = getLine(L2s,L2E, L2, L2tag); //calculate e index
            modifyCache(L2, blocksize, data, L2setIndex, L2eIndex); //write data to cache
        }
        else {
            missCount_L2++;
        }
        if(!isHit(L1setIndex,L1E, L1I, L1tag, 1) && !isHit(L1setIndex,L1E, L1D, L1tag, 2) && !isHit(L1setIndex,L2E, L2, L2tag, 3)){
            modifyRam(data,0, address);
        }

    }

    public static void modifyRam(String data, int blockSize, String address){

        String ramData = ram.get(addressToIndex(address));  //get the data from memory
        String temp = ramData.substring(0,blockSize); //write the first unchanging part of the data to temp
        temp += data; //update temp with data
        temp += ramData.substring(data.length() + ramData.substring(0, blockSize).length() ); //write the last unchanging part of the data to temp
        String modifiedData = temp;
        ram.set(addressToIndex(address), modifiedData); //update Ram
    }
    public static void modifyCache(String[][][] cache, int blockSize, String data, int setIndex, int eIndex){

        String cacheData = cache[setIndex][eIndex][3]; //get cache data
        String temp = cacheData.substring(0,blockSize);  //write the first unchanging part of the data to temp
        temp += data; //update temp with data
        temp += cacheData.substring(data.length() + cacheData.substring(0, blockSize).length() ); //write the last unchanging part of the data to temp
        String modifiedData = temp;
        cache[setIndex][eIndex][3] = modifiedData; //update Cache
    }

    public static void loadInstruction(String address, String size){
        int L1setIndex = calculate_set_index(L1s, L1b, address);
        String L1tag = calculate_tag(L1s, L1b, address);
        int lineIndex;
        // if it is miss for L1I
        if(!isHit(L1setIndex, L1E, L1I, L1tag, 2)){
            missCount_L1I++;
            if(isContainEmptyLine(L1setIndex, L1E, L1I)){
                lineIndex = findEmptyLineIndex(L1setIndex, L1E, L1I);
            }
            else{
                evictionCount_L1I++;
                lineIndex = findEvictionLine(L1setIndex, L1E, L1I);
            }
            L1I[L1setIndex][lineIndex][0] = L1tag;
            L1I[L1setIndex][lineIndex][1] = findTime(L1setIndex, L1E, L1I);
            L1I[L1setIndex][lineIndex][2] = "1";

            String addressBinary = hex2Binary(address);  //convert address from hexadecimal to binary
            String block = addressBinary.substring(addressBinary.length() - L1b); //get the last b bits from the address
            int blocksize = binary2Decimal(block); // change the value to decimal since we want to find the starting index of the data in the block
            L1I[L1setIndex][lineIndex][3] = ram.get(addressToIndex(address)).substring(blocksize);

        }
        // check for L2
        int L2setIndex = calculate_set_index(L2s, L2b, address);
        String L2tag = calculate_tag(L2s, L2b, address);
        // if it is miss for L2
        if(!isHit(L2setIndex, L2E, L2, L2tag, 3)) {
            missCount_L2++;
            if (isContainEmptyLine(L2setIndex, L2E, L2)) {
                lineIndex = findEmptyLineIndex(L2setIndex, L2E, L2);
            } else {
                evictionCount_L2++;
                lineIndex = findEvictionLine(L2setIndex, L2E, L2);
            }
            L2[L2setIndex][lineIndex][0] = L2tag;
            L2[L2setIndex][lineIndex][1] = findTime(L2setIndex, L2E, L2);
            L2[L1setIndex][lineIndex][2] = "1";

            String addressBinary = hex2Binary(address);  //convert address from hexadecimal to binary
            String block = addressBinary.substring(addressBinary.length() - L2b); //get the last b bits from the address
            int blocksize = binary2Decimal(block); // change the value to decimal since we want to find the starting index of the data in the block
            L2[L1setIndex][lineIndex][3] = ram.get(addressToIndex(address)).substring(blocksize);
        }
    }

    public static void data_load(String address, String size){
        // first check for L1
        int L1setIndex = calculate_set_index(L1s, L1b, address);
        String L1tag = calculate_tag(L1s, L1b, address);
        int lineIndex;
        // if it is miss for L1D
        if(!isHit(L1setIndex, L1E, L1D, L1tag, 2)){
            missCount_L1D++;
            if(isContainEmptyLine(L1setIndex, L1E, L1D)){
                lineIndex = findEmptyLineIndex(L1setIndex, L1E, L1D);
            }
            else{
                evictionCount_L1D++;
                lineIndex = findEvictionLine(L1setIndex, L1E, L1D);
            }
            L1D[L1setIndex][lineIndex][0] = L1tag;
            L1D[L1setIndex][lineIndex][1] = findTime(L1setIndex, L1E, L1D);
            L1D[L1setIndex][lineIndex][2] = "1";

            String addressBinary = hex2Binary(address);  //convert address from hexadecimal to binary
            String block = addressBinary.substring(addressBinary.length() - L1b); //get the last b bits from the address
            int blocksize = binary2Decimal(block); // change the value to decimal since we want to find the starting index of the data in the block
            L1D[L1setIndex][lineIndex][3] = ram.get(addressToIndex(address)).substring(blocksize);

        }
        // check for L2
        int L2setIndex = calculate_set_index(L2s, L2b, address);
        String L2tag = calculate_tag(L2s, L2b, address);
        // if it is miss for L2
        if(!isHit(L2setIndex, L2E, L2, L2tag, 3)){
            missCount_L2++;
            if(isContainEmptyLine(L2setIndex, L2E, L2)){
                lineIndex = findEmptyLineIndex(L2setIndex, L2E, L2);
            }
            else {
                evictionCount_L2++;
                lineIndex = findEvictionLine(L2setIndex, L2E, L2);
            }
            L2[L2setIndex][lineIndex][0] = L2tag;
            L2[L2setIndex][lineIndex][1] = findTime(L2setIndex, L2E, L2);
            L2[L1setIndex][lineIndex][2] = "1";

            String addressBinary = hex2Binary(address);  //convert address from hexadecimal to binary
            String block = addressBinary.substring(addressBinary.length() - L2b); //get the last b bits from the address
            int blocksize = binary2Decimal(block); // change the value to decimal since we want to find the starting index of the data in the block
            L2[L1setIndex][lineIndex][3] = ram.get(addressToIndex(address)).substring(blocksize);
        }
    }


    // Method to check if the set contains empty line
    public static boolean isContainEmptyLine(int S, int E, String cache[][][]){
        boolean isContain = false;
        for(int i = 0; i < E; i++){
            if(cache[S][i][0].equalsIgnoreCase(""))
                isContain = true;
        }
        return isContain;
    }

    // Method to find empty line inside the set
    public static int findEmptyLineIndex(int S, int E, String cache[][][]){
        int index = 0;
        for(int i = 0; i < E; i++){
            if(cache[S][i][0].equalsIgnoreCase(""))
                index = i;
        }
        return index;
    }

    // Method to find line for eviction in that set
    public static int findEvictionLine(int S, int E, String cache[][][]){
        int index = 0;
        int min = Integer.parseInt(cache[S][0][1]);
        int new_min;
        for(int i = 1; i < E; i++){
            new_min = Integer.parseInt(cache[S][i][1]);
            if( new_min < min){
                min = new_min;
                index = i;
            }
        }
        return index;
    }

    public static String findTime(int S, int E, String cache[][][]){
        int time = 0;
        int newTime;
        for(int i = 0; i < E; i++){
            newTime = (cache[S][i][1].equalsIgnoreCase("")) ? 0: Integer.parseInt(cache[S][i][1]);
            if(newTime > time)
                time = newTime;
        }
        time++;
        return "" + time;
    }

    public static boolean isHit(int S, int E, String cache[][][] ,String tag, int id){
        boolean isHit = false;
        for(int i = 0; i < E; i++){
            if(cache[S][i][0].equalsIgnoreCase(tag) && cache[S][i][2].equalsIgnoreCase("1")){
                switch (id){
                    case 1: hitCount_L1I++; break;
                    case 2: hitCount_L1D++; break;
                    case 3: hitCount_L2++; break;
                }
                isHit = true;
                break;
            }
        }
        return isHit;
    }

    public static int getLine(int S, int E, String cache[][][] ,String tag){
        for(int i = 0; i < E; i++){
            if(cache[S][i][0].equalsIgnoreCase(tag) && cache[S][i][2].equalsIgnoreCase("1")){
                return i;
            }
        }
        return 0;
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

        for(int i = 0; i < hex.length(); i++ ){
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
        int binaryLength = exp;
        for(int i = 0; i < binaryLength ; i++){
            exp --;
            if(binary.charAt(i) == '1') {
                decimal += Math.pow(2, exp);
            }
        }
        return decimal;
    }

}