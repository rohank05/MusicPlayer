package me.rohank05;


import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws LineUnavailableException, IOException {
        MusicManager musicManager = new MusicManager();
        musicManager.run();
    }


}
