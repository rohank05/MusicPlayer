package me.rohank05;

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_BE;

public class MusicManager extends AudioEventAdapter {
    public Scanner sc;
    public AudioPlayerManager audioPlayerManager;
    public AudioPlayer audioPlayer;

    public Stack<AudioTrack> queue = new Stack<>();

    public Thread choice;
    public Thread audio;

    public Filter audioFilter;

    AudioInputStream audioInputStream;

    public MusicManager() {
        sc = new Scanner(System.in);
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
        audioPlayerManager.getConfiguration().setOutputFormat(COMMON_PCM_S16_BE);
        audioPlayer = audioPlayerManager.createPlayer();
        audioPlayer.addListener(this);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        audioFilter = new Filter(audioPlayer);
    }

    public void run() {
        choice = new Thread(this::giveChoice);
        choice.start();

        audio = new Thread(() -> {
            while (true) {
                if (audioPlayer.getPlayingTrack() != null) {
                    try {
                        playOnSpeaker();
                        Thread.sleep(audioPlayer.getPlayingTrack().getDuration());
                    } catch (IOException | LineUnavailableException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        audio.start();


    }

    public void playOnSpeaker() throws IOException, LineUnavailableException, InterruptedException {
        AudioDataFormat audioDataFormat = audioPlayerManager.getConfiguration().getOutputFormat();
        audioInputStream = AudioPlayerInputStream.createStream(audioPlayer, audioDataFormat, 10000L, true);
        SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, audioInputStream.getFormat());

        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);


        line.open(audioInputStream.getFormat());

        line.start();
        byte[] buffer = new byte[COMMON_PCM_S16_BE.maximumChunkSize()];
        int chunkSize;
        while ((chunkSize = audioInputStream.read(buffer)) != -1) {
            if (audioPlayer.isPaused()) {
                while (true) {
                    if (audioPlayer.isPaused()) {
                        Thread.sleep(1000);
                    } else break;
                }
            }
            line.write(buffer, 0, chunkSize);
        }
    }

    public void giveChoice() {
        System.out.println("Choose a correct option");
        System.out.println("1. Play or Add Song");
        System.out.println("2. Pause");
        System.out.println("3. Resume");
        System.out.println("4. Skip");
        System.out.println("5. Stop");
        System.out.println("6. Add Filters");
        System.out.println("7. Exit");
        int choice = sc.nextInt();
        goToChoice(choice);
    }

    public void goToChoice(int choice) {
        switch (choice) {
            case 1 -> loadAndPlay();
            case 2 -> pause();
            case 3 -> resume();
            case 4 -> skip();
            case 5 -> stop();
            case 6 -> filter();
            case 7 -> System.exit(0);
            default -> {
                System.out.println("Wrong choice");
                giveChoice();
            }
        }

    }

    public void skip() {
        audioPlayer.stopTrack();
        giveChoice();
    }

    public void pause() {
        audioPlayer.setPaused(true);
        giveChoice();
    }

    public void resume() {
        audioPlayer.setPaused(false);

        giveChoice();
    }

    public void filter() {
        System.out.println("Choose the Filter you want to enable or disable");
        String enabled = audioFilter.isNightcore() ? "(Enabled)" : "(Disabled)";
        System.out.println("1. Nightcore " + enabled);
        enabled = audioFilter.isEightD() ? "(Enabled)" : "(Disabled)";
        System.out.println("2. EightD " + enabled);
        enabled = audioFilter.isTremolo() ? "(Enabled)" : "(Disabled)";
        System.out.println("3. Tremolo " + enabled);
        enabled = audioFilter.isVibrato() ? "(Enabled)" : "(Disabled)";
        System.out.println("4. Vibrato " + enabled);
        enabled = audioFilter.isEcho() ? "(Enabled)" : "(Disabled)";
        System.out.println("5. Echo " + enabled);
        enabled = audioFilter.isReverb() ? "(Enabled)" : "(Disabled)";
        System.out.println("6. Reverb " + enabled);
        System.out.println("7. Go back to main menu");

        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                audioFilter.setNightcore(!audioFilter.isNightcore());
                audioFilter.updateFilter();
                filter();
                break;
            case 2:
                audioFilter.setEightD(!audioFilter.isEightD());
                audioFilter.updateFilter();
                filter();
                break;
            case 3:
                audioFilter.setTremolo(!audioFilter.isTremolo());
                audioFilter.updateFilter();
                filter();
                break;
            case 4:
                audioFilter.setVibrato(!audioFilter.isVibrato());
                audioFilter.updateFilter();
                filter();
                break;
            case 5:
                audioFilter.setEcho(!audioFilter.isEcho());
                audioFilter.updateFilter();
                filter();
                break;
            case 6:
                audioFilter.setReverb(!audioFilter.isReverb());
                audioFilter.updateFilter();
                filter();
                break;
            case 7:
                giveChoice();
                break;
            default:
                System.out.println("Wrong choice");
                filter();
        }
        audioFilter.updateFilter();

    }

    public void stop() {
        System.out.println("Player Stopped");
        this.queue.clear();
        audioPlayer.stopTrack();
        giveChoice();
    }

    public void loadAndPlay() {
        System.out.print("Enter a song name: ");
        sc.nextLine();
        String songName = sc.nextLine();
        System.out.println("You have entered " + songName);

        audioPlayerManager.loadItem("ytmsearch:" + songName, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    try {
                        System.out.println("Added song to queue: " + playlist.getTracks().get(0).getInfo().title);
                        addToQueue(playlist.getTracks().get(0));
                        giveChoice();
                    } catch (LineUnavailableException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void noMatches() {
                System.out.println("Can't find the song");
                giveChoice();
            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    public void addToQueue(AudioTrack audioTrack) throws LineUnavailableException, IOException {
        if (!audioPlayer.startTrack(audioTrack, true)) {
            this.queue.push(audioTrack);
        }
    }

    public void playNextTrack() {
        audioPlayer.startTrack(this.queue.firstElement(), true);
        this.queue.remove(0);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (!this.queue.isEmpty()) {
            playNextTrack();
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        System.out.println("Now Playing: " + track.getInfo().title);
    }
}
