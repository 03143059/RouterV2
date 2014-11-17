/**
 * Created by Werner on 11/3/2014.
 */
public class ForwarderMessage {
    String from;
    String to;
    String text;

    public ForwarderMessage(String from, String to, String text){
        this.from = from;
        this.to = to;
        this.text = text;
    }

    @Override
    public String toString() {
        return "From:" + from + "\nTo:" + to + "\nMsg:"+text+"\n";
     }
}
