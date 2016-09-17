package shadasviar.bestselfie.PictureStorage.DataProviders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

import shadasviar.bestselfie.PictureStorage.HistoryManager.HistoryManager;
import shadasviar.bestselfie.PictureStorage.HistoryManager.IHistoryManager;

/**
 * Created by vlad on 8/8/16.
 * @author Shadasviar
 */
public class HistoryData implements IDataStorage {


    IHistoryManager.HistoryTypes type;


    public HistoryData(IHistoryManager.HistoryTypes type){
        this.type = type;
    }


    @Override
    public List<File> getActualData() {
        List<File> result;
        IHistoryManager hm = HistoryManager.getInstance();
        result = new ArrayList<>((HashSet<File>)hm.getHistory(type));
        return result;
    }
}
