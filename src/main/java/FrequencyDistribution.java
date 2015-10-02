import java.util.Map;

/**
 * FrequencyDistribution class represents a frequency distribution between words and their count for a given source.
 */
public class FrequencyDistribution {
    public String source;
    public Map<String, Integer> freqDist;

    public FrequencyDistribution(String src, Map<String, Integer> freqDist) {
        this.source = src;
        this.freqDist = freqDist;
    }
}
