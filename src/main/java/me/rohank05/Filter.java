package me.rohank05;

import com.github.natanbc.lavadsp.rotation.RotationPcmAudioFilter;
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.github.natanbc.lavadsp.tremolo.TremoloPcmAudioFilter;
import com.github.natanbc.lavadsp.vibrato.VibratoPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.rohank05.echo.EchoPcmAudioFilter;
import me.rohank05.reverb.ReverberationPcmAudioFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Filter {
    private AudioPlayer audioPlayer;
    private boolean nightcore = false;
    private boolean eightD = false;
    private boolean tremolo = false;
    private boolean vibrato = false;
    private boolean echo = false;

    private boolean reverb = false;

    public Filter(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
    }

    public boolean isEcho() {
        return echo;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public boolean isNightcore() {
        return nightcore;
    }

    public void setNightcore(boolean nightcore) {
        this.nightcore = nightcore;
    }

    public boolean isEightD() {
        return eightD;
    }

    public void setEightD(boolean eightD) {
        this.eightD = eightD;
    }

    public boolean isTremolo() {
        return tremolo;
    }

    public void setTremolo(boolean tremolo) {
        this.tremolo = tremolo;
    }

    public boolean isVibrato() {
        return vibrato;
    }

    public void setVibrato(boolean vibrato) {
        this.vibrato = vibrato;
    }

    public boolean isReverb(){
        return reverb;
    }

    public void setReverb(boolean reverb){
        this.reverb = reverb;
    }

    public void resetFilter(){
        setEcho(false);
        setNightcore(false);
        setTremolo(false);
        setEightD(false);
        setVibrato(false);
        setReverb(false);
    }

    private boolean filterEnabled(){
        return this.nightcore || this.eightD || this.tremolo ||this.vibrato || this.echo || this.reverb;
    }

    public void updateFilter(){
        if(this.filterEnabled()){
            this.audioPlayer.setFilterFactory(this::buildChain);
        }
        else this.audioPlayer.setFilterFactory(null);
    }

    private List<AudioFilter> buildChain(AudioTrack track, AudioDataFormat audioDataFormat, UniversalPcmAudioFilter downstream){
        List<AudioFilter> filterList = new ArrayList<>();
        FloatPcmAudioFilter filter = downstream;
        if(this.nightcore){
            TimescalePcmAudioFilter timescalePcmAudioFilter = new TimescalePcmAudioFilter(filter, audioDataFormat.channelCount, audioDataFormat.sampleRate);
            timescalePcmAudioFilter.setPitch(1.29).setSpeed(1.29);
            filter = timescalePcmAudioFilter;
            filterList.add(timescalePcmAudioFilter);
        }
        if(this.eightD){
            RotationPcmAudioFilter rotationPcmAudioFilter = new RotationPcmAudioFilter(filter, audioDataFormat.sampleRate);
            rotationPcmAudioFilter.setRotationSpeed(0.1);
            filter = rotationPcmAudioFilter;
            filterList.add(rotationPcmAudioFilter);
        }
        if(this.vibrato){
            VibratoPcmAudioFilter vibratoPcmAudioFilter = new VibratoPcmAudioFilter(filter, audioDataFormat.channelCount, audioDataFormat.sampleRate);
            vibratoPcmAudioFilter.setFrequency(4);
            filter = vibratoPcmAudioFilter;
            filterList.add(vibratoPcmAudioFilter);
        }
        if(this.tremolo){
            TremoloPcmAudioFilter tremoloPcmAudioFilter = new TremoloPcmAudioFilter(filter, audioDataFormat.channelCount, audioDataFormat.sampleRate);
            tremoloPcmAudioFilter.setFrequency(1).setDepth((float) 0.8);
            filter = tremoloPcmAudioFilter;
            filterList.add(tremoloPcmAudioFilter);
        }
        if(this.echo){
            EchoPcmAudioFilter echoPcmAudioFilter = new EchoPcmAudioFilter(filter, audioDataFormat.channelCount, audioDataFormat.sampleRate);
            echoPcmAudioFilter.setDelay(0).setDecay(0.5f);
            filter = echoPcmAudioFilter;
            filterList.add(echoPcmAudioFilter);
        }
        if(this.reverb){
            ReverberationPcmAudioFilter reverbPcmAudioFilter = new ReverberationPcmAudioFilter(filter, audioDataFormat.channelCount, audioDataFormat.sampleRate);
            filterList.add(reverbPcmAudioFilter);
        }

        Collections.reverse(filterList);
        return filterList;
    }


}
