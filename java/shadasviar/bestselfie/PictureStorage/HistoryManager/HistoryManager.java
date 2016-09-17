package shadasviar.bestselfie.PictureStorage.HistoryManager;

import android.content.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import shadasviar.bestselfie.BestSelfie;

/**
 * Created by vlad on 8/6/16.
 * @author Shadasviar
 * This class is SINGLETON, PAY ATTENTION!!!!
 *
 * It stores history in binary file and it is used for central management of all history.
 */
public class HistoryManager implements IHistoryManager {

    private List<Set<Object>> histories;
    private static final String filename = "History.out";
    private static HistoryManager thisInstance;


    private HistoryManager() {
        histories = new ArrayList<>();
        for(int i = 0; i < HistoryTypes.values().length; ++i){
            histories.add(new HashSet<>());
        }
        load();
    }


    public static synchronized HistoryManager getInstance(){
        if(thisInstance == null){
            thisInstance = new HistoryManager();
        }
        return thisInstance;
    }


    @Override
    public synchronized Set<? extends Object> getHistory(HistoryTypes historyID){
        return histories.get(historyID.ordinal());
    }


    @Override
    public synchronized void addToHistory(HistoryTypes historyID, List<? extends Object> data){
        histories.get(historyID.ordinal()).addAll(data);
        save();
    }


    @Override
    public synchronized void clearHistory(HistoryTypes historyID){
        histories.get(historyID.ordinal()).clear();
        save();
    }


    private void save(){
        try {
            Context context = BestSelfie.getContext();
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(histories);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void load(){
        try {
            Context context = BestSelfie.getContext();
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            histories = (ArrayList<Set<Object>>) ois.readObject();
            ois.close();
        }catch (FileNotFoundException e) {
            save();
            load();
        }catch (Exception e) {
        }
    }
}
