package stages.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserDefaultManager {

    public UserDefaultManager(){}

    public static int getProgress(){
        recordData();
        return Integer.parseInt(getData(1));
    }
    public static void setProgress(int stage){
        recordData();
        setData(1, String.valueOf(stage));
    }

    public static double getMusicVolume(){
        recordData();
        return Double.parseDouble(getData(2));
    }

    public static double getAudioEffectsVolume() {
        recordData();
        return Double.parseDouble(getData(3));
    }

    public static int getDifficulty() {
        recordData();
        return Integer.parseInt(getData(4));
    }

    public static void setMusicVolume(double volume){
        recordData();
        setData(2, String.valueOf(volume));
    }

    public static void setAudioEffectsVolume(double volume) {
        recordData();
        setData(3, String.valueOf(volume));
    }

    public static void setDifficulty(int type) {
        recordData();
        setData(4, String.valueOf(type));
    }

    static void recordData(){
        try {
            fileContent = "";
            fileReader = new FileReader("src/src/files/user_default");
            int c;
            while((c = fileReader.read()) != -1){
                fileContent += ((char)c);
            }
        }
        catch(IOException ex){ System.out.println(ex.getMessage()); }
    }

    static void writeData(){
        try {
            fileWriter = new FileWriter("src/src/files/user_default", false);
            fileWriter.write(fileContent);
            fileWriter.flush();
        }
        catch(IOException ex){ System.out.println(ex.getMessage()); }
    }

    static String[] filterData() {
        return fileContent.split("-");
    }

    static String getData(int pos) {
        String[] data = filterData();
        return data[pos - 1];
    }

    static void setData(int pos, String data) {
        String[] dataArray = filterData();
        dataArray[pos - 1] = data;
        StringBuilder content = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            content.append(dataArray[i]);
            if(i != 3) content.append('-');
        }
        fileContent = content.toString();
        writeData();
    }

    static FileWriter fileWriter;
    static FileReader fileReader;
    static String fileContent;
}
